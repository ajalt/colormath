package com.github.ajalt.colormath

import com.github.ajalt.colormath.XYZ.Companion.whitePoint
import com.github.ajalt.colormath.internal.*
import kotlin.math.pow

/**
 * The color space describing colors in the [XYZ] model.
 */
interface XYZColorSpace : WhitePointColorSpace<XYZ> {
    operator fun invoke(x: Float, y: Float, z: Float, alpha: Float = 1f): XYZ
    operator fun invoke(x: Double, y: Double, z: Double, alpha: Double): XYZ =
        invoke(x.toFloat(), y.toFloat(), z.toFloat(), alpha.toFloat())

    operator fun invoke(x: Double, y: Double, z: Double, alpha: Float = 1f): XYZ =
        invoke(x.toFloat(), y.toFloat(), z.toFloat(), alpha)

    /**
     * The transformation matrix from this color space to linear-light sRGB.
     *
     * The matrix is a 3x3 matrix stored in row-major order.
     */
    val matrixToSrgb: FloatArray
}

private data class XYZColorSpaceImpl(override val whitePoint: Illuminant) : XYZColorSpace {
    override val name: String get() = "XYZ"
    override val components: List<ColorComponentInfo> = componentInfoList(
        ColorComponentInfo("X", false),
        ColorComponentInfo("Y", false),
        ColorComponentInfo("Z", false),
    )

    override fun convert(color: Color): XYZ = color.toXYZ()
    override fun create(components: FloatArray): XYZ = withValidComps(components) {
        invoke(it[0], it[1], it[2], it.getOrElse(3) { 1f })
    }

    override operator fun invoke(x: Float, y: Float, z: Float, alpha: Float): XYZ {
        return XYZ(x, y, z, alpha, this)
    }

    override val matrixToSrgb: FloatArray = srgbToXyzMatrix(whitePoint).inverse().rowMajor
}

/** An [XYZ] color space calculated relative to [Illuminant.D65] */
val XYZ65: XYZColorSpace = XYZColorSpaceImpl(Illuminant.D65)

/** An [XYZ] color space calculated relative to [Illuminant.D50] */
val XYZ50: XYZColorSpace = XYZColorSpaceImpl(Illuminant.D50)

/**
 * The CIEXYZ color model
 *
 * [XYZ] is calculated relative to a [given][model] [whitePoint], which defaults to [Illuminant.D65].
 *
 * | Component  | sRGB D65 Range |
 * | ---------- | -------------- |
 * | [x]        | `[0, 0.96]`    |
 * | [y]        | `[0, 1]`       |
 * | [z]        | `[0, 1.09]`    |
 */
data class XYZ internal constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    override val alpha: Float = 1f,
    override val model: XYZColorSpace,
) : Color {
    companion object : XYZColorSpace by XYZ65 {
        /** Create a new `XYZ` color space that will be calculated relative to the given [whitePoint] */
        operator fun invoke(whitePoint: Illuminant): XYZColorSpace = when (whitePoint) {
            Illuminant.D65 -> XYZ65
            Illuminant.D50 -> XYZ50
            else -> XYZColorSpaceImpl(whitePoint)
        }
    }

    fun adaptTo(space: XYZColorSpace): XYZ {
        if (space.whitePoint == model.whitePoint) return this
        val ws = CAT02_XYZ_TO_LMS.times(model.whitePoint.x, model.whitePoint.y, model.whitePoint.z)
        val wd = CAT02_XYZ_TO_LMS.times(space.whitePoint.x, space.whitePoint.y, space.whitePoint.z)
        val transform = CAT02_LMS_TO_XYZ * Matrix.diagonal(wd.l / ws.l, wd.m / ws.m, wd.s / ws.s) * CAT02_XYZ_TO_LMS
        return transform.times(x, y, z) { xx, yy, zz -> space(xx, yy, zz, alpha) }
    }

    override fun toRGB(): RGB = Matrix(model.matrixToSrgb).times(x, y, z) { r, g, b ->
        RGB(linearToSRGB(r), linearToSRGB(g), linearToSRGB(b), alpha)
    }

    override fun toLinearRGB(): LinearRGB = Matrix(model.matrixToSrgb).times(x, y, z) { r, g, b ->
        LinearRGB(r, g, b, alpha)
    }

    // http://www.brucelindbloom.com/Eqn_XYZ_to_Lab.html
    override fun toLAB(): LAB {
        fun f(t: Float) = when {
            t > CIE_E -> cbrt(t)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(x / model.whitePoint.x)
        val fy = f(y / model.whitePoint.y)
        val fz = f(z / model.whitePoint.z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LAB(model.whitePoint)(l, a, b, alpha)
    }

    // http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
    override fun toLUV(): LUV = toD65 {
        val denominator = x + 15 * y + 3 * z
        val uPrime = if (denominator == 0f) 0f else (4 * x) / denominator
        val vPrime = if (denominator == 0f) 0f else (9 * y) / denominator

        val denominatorReference = model.whitePoint.x + 15 * model.whitePoint.y + 3 * model.whitePoint.z
        val uPrimeReference = (4 * model.whitePoint.x) / denominatorReference
        val vPrimeReference = (9 * model.whitePoint.y) / denominatorReference

        val yr = y / model.whitePoint.y
        val l = when {
            yr > CIE_E -> 116 * cbrt(yr) - 16
            else -> CIE_K * yr
        }
        val u = 13 * l * (uPrime - uPrimeReference)
        val v = 13 * l * (vPrime - vPrimeReference)

        return LUV(l.coerceIn(0f, 100f), u, v, alpha)
    }

    // https://bottosson.github.io/posts/oklab/#converting-from-xyz-to-oklab
    override fun toOklab(): Oklab = toD65 {
        val l = +0.8189330101 * x + 0.3618667424 * y - 0.1288597137 * z
        val m = +0.0329845436 * x + 0.9293118715 * y + 0.0361456387 * z
        val s = +0.0482003018 * x + 0.2643662691 * y + 0.6338517070 * z

        val ll = cbrt(l)
        val mm = cbrt(m)
        val ss = cbrt(s)

        return Oklab(
            l = +0.2104542553 * ll + 0.7936177850 * mm - 0.0040720468 * ss,
            a = +1.9779984951 * ll - 2.4285922050 * mm + 0.4505937099 * ss,
            b = +0.0259040371 * ll + 0.7827717662 * mm - 0.8086757660 * ss,
            alpha = alpha
        )
    }

    override fun toJzAzBz(): JzAzBz = toD65 {
        fun pq(x: Double): Double {
            val xx = (x * 1e-4).pow(0.1593017578125)
            return ((0.8359375 + 18.8515625 * xx) / (1 + 18.6875 * xx)).pow(134.034375)
        }

        val lp = pq(0.674207838 * x + 0.382799340 * y - 0.047570458 * z)
        val mp = pq(0.149284160 * x + 0.739628340 * y + 0.083327300 * z)
        val sp = pq(0.070941080 * x + 0.174768000 * y + 0.670970020 * z)
        val iz = 0.5 * (lp + mp)
        return JzAzBz(
            j = (0.44 * iz) / (1 - 0.56 * iz) - JzAzBz.d0,
            a = 3.524000 * lp - 4.066708 * mp + 0.542708 * sp,
            b = 0.199076 * lp + 1.096799 * mp - 1.295875 * sp,
            alpha = alpha
        )
    }

    private inline fun <T : Color> toD65(block: XYZ.() -> T): T {
        return if (model == XYZ65) this.block() else adaptTo(XYZ65).block()
    }

    override fun toXYZ(): XYZ = this
    override fun toArray(): FloatArray = floatArrayOf(x, y, z, alpha)
}
