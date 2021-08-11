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
}

/** Create a new [XYZColorSpace] that will be calculated relative to the given [whitePoint] */
fun XYZColorSpace(whitePoint: WhitePoint): XYZColorSpace = when (whitePoint) {
    Illuminant.D65 -> XYZ65
    Illuminant.D50 -> XYZ50
    else -> XYZColorSpaceImpl(whitePoint)
}

private data class XYZColorSpaceImpl(override val whitePoint: WhitePoint) : XYZColorSpace {
    override val name: String get() = "XYZ"
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("XYZ")
    override operator fun invoke(x: Float, y: Float, z: Float, alpha: Float): XYZ = XYZ(x, y, z, alpha, this)
    override fun convert(color: Color): XYZ = color.toXYZ()
    override fun create(components: FloatArray): XYZ = doCreate(components, ::invoke)
    override fun toString(): String = "XYZColorSpace($whitePoint)"
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
    override val alpha: Float,
    override val model: XYZColorSpace,
) : Color {
    companion object : XYZColorSpace by XYZ65

    /**
     * Apply chromatic adaptation to adapt this color to the white point in the given [space].
     *
     * The Von Kries method is used with the CIECAT02 transformation matrix.
     */
    fun adaptTo(space: XYZColorSpace): XYZ {
        return adaptTo(space, CAT02_XYZ_TO_LMS, CAT02_LMS_TO_XYZ)
    }

    /**
     * Apply chromatic adaptation to adapt this color to the white point in the given [space].
     *
     * The Von Kries method is used with the given [transformationMatrix].
     */
    fun adaptTo(space: XYZColorSpace, transformationMatrix: FloatArray): XYZ {
        return adaptTo(space, Matrix(transformationMatrix), Matrix(transformationMatrix).inverse())
    }

    private fun adaptTo(space: XYZColorSpace, m: Matrix, mi: Matrix): XYZ {
        if (space.whitePoint == model.whitePoint) return this
        val transform = space.chromaticAdaptationMatrix(model.whitePoint.chromaticity, m, mi)
        return transform.times(x, y, z) { xx, yy, zz -> space(xx, yy, zz, alpha) }
    }

    /**
     * Convert this color to the [RGB] model with the given color [space].
     */
    fun toRGB(space: RGBColorSpace): RGB {
        val (x, y, z) = adaptTo(XYZColorSpace(space.whitePoint))
        val f = space.transferFunctions
        return Matrix(space.matrixFromXyz).times(x, y, z) { r, g, b ->
            space(f.oetf(r), f.oetf(g), f.oetf(b), alpha)
        }
    }

    override fun toSRGB(): RGB = toRGB(RGBColorSpaces.SRGB)

    // http://www.brucelindbloom.com/Eqn_XYZ_to_Lab.html
    override fun toLAB(): LAB {
        fun f(t: Float) = when {
            t > CIE_E -> cbrt(t)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(x / model.whitePoint.chromaticity.x)
        val fy = f(y / model.whitePoint.chromaticity.y)
        val fz = f(z / model.whitePoint.chromaticity.z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LABColorSpace(model.whitePoint)(l, a, b, alpha)
    }

    // http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
    override fun toLUV(): LUV {
        val wp = model.whitePoint.chromaticity
        val denominator = x + 15 * y + 3 * z
        val uPrime = if (denominator == 0f) 0f else (4 * x) / denominator
        val vPrime = if (denominator == 0f) 0f else (9 * y) / denominator

        val denominatorReference = wp.x + 15 * wp.y + 3 * wp.z
        val uPrimeReference = (4 * wp.x) / denominatorReference
        val vPrimeReference = (9 * wp.y) / denominatorReference

        val yr = y / wp.y
        val l = when {
            yr > CIE_E -> 116 * cbrt(yr) - 16
            else -> CIE_K * yr
        }
        val u = 13 * l * (uPrime - uPrimeReference)
        val v = 13 * l * (vPrime - vPrimeReference)

        return LUV(model.whitePoint)(l.coerceIn(0f, 100f), u, v, alpha)
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

/** Create the transform matrix to adapt [whitePoint] to this color space */
internal fun XYZColorSpace.chromaticAdaptationMatrix(
    whitePoint: Chromaticity,
    xyzToLms: Matrix = CAT02_XYZ_TO_LMS,
    lmsToXyz: Matrix = CAT02_LMS_TO_XYZ,
): Matrix {
    val src = xyzToLms.times(whitePoint.x, whitePoint.y, whitePoint.z)
    val chromaticity = this.whitePoint.chromaticity
    val dst = xyzToLms.times(chromaticity.x, chromaticity.y, chromaticity.z)
    return lmsToXyz.timesDiagonal(dst.l / src.l, dst.m / src.m, dst.s / src.s) * xyzToLms
}
