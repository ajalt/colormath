package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.*
import com.github.ajalt.colormath.model.XYZ.Companion.whitePoint
import kotlin.math.cbrt
import kotlin.math.pow

/**
 * The color space describing colors in the [XYZ] model.
 */
interface XYZColorSpace : WhitePointColorSpace<XYZ> {
    operator fun invoke(x: Float, y: Float, z: Float, alpha: Float = 1f): XYZ
    operator fun invoke(x: Number, y: Number, z: Number, alpha: Number = 1f): XYZ =
        invoke(x.toFloat(), y.toFloat(), z.toFloat(), alpha.toFloat())
}

/** Create a new [XYZColorSpace] that will be calculated relative to the given [whitePoint] */
fun XYZColorSpace(whitePoint: WhitePoint): XYZColorSpace = when (whitePoint) {
    Illuminant.D65 -> XYZColorSpaces.XYZ65
    Illuminant.D50 -> XYZColorSpaces.XYZ50
    else -> XYZColorSpaceImpl(whitePoint)
}

private data class XYZColorSpaceImpl(override val whitePoint: WhitePoint) : XYZColorSpace {
    override val name: String get() = "XYZ"
    override val components: List<ColorComponentInfo> = zeroOneComponentInfo("XYZ")
    override fun convert(color: Color): XYZ = color.toXYZ().adaptTo(this)
    override fun create(components: FloatArray): XYZ = doCreate(components, ::invoke)
    override fun toString(): String = "XYZColorSpace($whitePoint)"
    override operator fun invoke(x: Float, y: Float, z: Float, alpha: Float): XYZ {
        return XYZ(x, y, z, alpha, this)
    }

    override fun hashCode(): Int = whitePoint.hashCode()
    override fun equals(other: Any?): Boolean {
        return other is XYZColorSpace && whitePoint == other.whitePoint
    }
}

object XYZColorSpaces {
    /** An [XYZ] color space calculated relative to [Illuminant.D65] */
    val XYZ65: XYZColorSpace = XYZColorSpaceImpl(Illuminant.D65)

    /** An [XYZ] color space calculated relative to [Illuminant.D50] */
    val XYZ50: XYZColorSpace = XYZColorSpaceImpl(Illuminant.D50)
}

/**
 * The CIEXYZ color model
 *
 * [XYZ] is calculated relative to a [given][space] [whitePoint], which defaults to [Illuminant.D65].
 *
 * | Component  | Range    |
 * | ---------- | -------- |
 * | [x]        | `[0, 1]` |
 * | [y]        | `[0, 1]` |
 * | [z]        | `[0, 1]` |
 */
data class XYZ internal constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    override val alpha: Float,
    override val space: XYZColorSpace,
) : Color {
    /** Default constructors for the [XYZ] color model: the [XYZ65][XYZColorSpaces.XYZ65] space. */
    companion object : XYZColorSpace by XYZColorSpaces.XYZ65 {
        override fun hashCode(): Int = XYZColorSpaces.XYZ65.hashCode()
        override fun equals(other: Any?): Boolean = XYZColorSpaces.XYZ65 == other
    }

    /**
     * Apply chromatic adaptation to adapt this color to the white point in the given [space].
     *
     * The Von Kries method is used with the CIECAT02 transformation matrix.
     */
    fun adaptTo(space: XYZColorSpace): XYZ {
        return adaptToM(space, CAT02_XYZ_TO_LMS, CAT02_LMS_TO_XYZ)
    }

    /**
     * Apply chromatic adaptation to adapt this color to the white point in the given [space].
     *
     * The Von Kries method is used with the given [transformationMatrix].
     */
    fun adaptTo(space: XYZColorSpace, transformationMatrix: FloatArray): XYZ {
        return adaptToM(space, Matrix(transformationMatrix), Matrix(transformationMatrix).inverse())
    }

    /**
     * Apply chromatic adaptation to adapt this color to the white point in the given [space].
     *
     * The Von Kries method is used with the given [transformationMatrix] and its [inverseTransformationMatrix].
     */
    fun adaptTo(
        space: XYZColorSpace,
        transformationMatrix: FloatArray,
        inverseTransformationMatrix: FloatArray,
    ): XYZ {
        return adaptToM(space, Matrix(transformationMatrix), Matrix(inverseTransformationMatrix))
    }

    private fun adaptToM(space: XYZColorSpace, m: Matrix, mi: Matrix): XYZ {
        if (space.whitePoint == this.space.whitePoint) return this
        val transform = space.chromaticAdaptationMatrix(this.space.whitePoint.chromaticity, m, mi)
        return transform.dot(x, y, z) { xx, yy, zz -> space(xx, yy, zz, alpha) }
    }

    /**
     * Convert this color to the [RGB] model with the given color [space].
     */
    fun toRGB(space: RGBColorSpace): RGB {
        val (x, y, z) = adaptTo(XYZColorSpace(space.whitePoint))
        val f = space.transferFunctions
        return Matrix(space.matrixFromXyz).dot(x, y, z) { r, g, b ->
            space(f.oetf(r), f.oetf(g), f.oetf(b), alpha)
        }
    }

    override fun toSRGB(): RGB = toRGB(RGBColorSpaces.SRGB)

    // http://www.brucelindbloom.com/Eqn_XYZ_to_Lab.html
    override fun toLAB(): LAB {
        fun f(t: Double) = when {
            t > CIE_E -> cbrt(t)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(x.toDouble() / space.whitePoint.chromaticity.X)
        val fy = f(y.toDouble() / space.whitePoint.chromaticity.Y)
        val fz = f(z.toDouble() / space.whitePoint.chromaticity.Z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LABColorSpace(space.whitePoint)(l, a, b, alpha)
    }

    // http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
    override fun toLUV(): LUV {
        val wp = space.whitePoint.chromaticity
        val denominator = x + 15.0 * y + 3.0 * z
        val uPrime = if (denominator == 0.0) 0.0 else (4 * x) / denominator
        val vPrime = if (denominator == 0.0) 0.0 else (9 * y) / denominator

        val denominatorReference = wp.X + 15.0 * wp.Y + 3.0 * wp.Z
        val uPrimeReference = (4.0 * wp.X) / denominatorReference
        val vPrimeReference = (9.0 * wp.Y) / denominatorReference

        val yr = y / wp.Y.toDouble()
        val l = when {
            yr > CIE_E -> 116 * cbrt(yr) - 16
            else -> CIE_K * yr
        }
        val u = 13.0 * l * (uPrime - uPrimeReference)
        val v = 13.0 * l * (vPrime - vPrimeReference)

        return LUVColorSpace(space.whitePoint)(l.coerceIn(0.0, 100.0), u, v, alpha)
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
            val v = ((0.8359375 + 18.8515625 * xx) / (1 + 18.6875 * xx)).pow(134.034375)
            return if (v.isNaN()) 0.0 else v
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

    override fun toICtCp(): ICtCp = convertXYZToICtCp(this)

    /**
     * Convert this color to `xyY` [xyY] coordinates.
     *
     * If [x], [y], and [z] are all 0, the resulting coordinates will use the [x][xyY.x] and
     * [y][xyY.y] values from this space's [whitePoint].
     */
    fun toCIExyY(): xyY {
        if (x == 0f && y == 0f && z == 0f) {
            return xyY(space.whitePoint.chromaticity.x, space.whitePoint.chromaticity.y, 0f)
        }
        val sum = x + y + z
        return xyY(x / sum, y / sum, y)
    }

    private inline fun <T : Color> toD65(block: XYZ.() -> T): T {
        return if (space == XYZColorSpaces.XYZ65) this.block() else adaptTo(XYZColorSpaces.XYZ65).block()
    }

    override fun toXYZ(): XYZ = this
    override fun toArray(): FloatArray = floatArrayOf(x, y, z, alpha)
}

/** Create the transform matrix to adapt [srcWp] to this color space */
internal fun XYZColorSpace.chromaticAdaptationMatrix(
    srcWp: xyY,
    xyzToLms: Matrix = CAT02_XYZ_TO_LMS,
    lmsToXyz: Matrix = CAT02_LMS_TO_XYZ,
): Matrix {
    val dstWp = this.whitePoint.chromaticity
    val src = xyzToLms.dot(srcWp.X, srcWp.Y, srcWp.Z)
    val dst = xyzToLms.dot(dstWp.X, dstWp.Y, dstWp.Z)
    return lmsToXyz.dotDiagonal(dst.l / src.l, dst.m / src.m, dst.s / src.s).dot(xyzToLms)
}
