package com.github.ajalt.colormath

import com.github.ajalt.colormath.LAB.Companion.whitePoint
import com.github.ajalt.colormath.LUV.Companion.whitePoint
import com.github.ajalt.colormath.XYZ.Companion.whitePoint
import com.github.ajalt.colormath.internal.*
import kotlin.math.pow


/**
 * The color space describing colors in the [LAB] model.
 */
interface LABColorSpace : WhitePointColorSpace<LAB> {
    operator fun invoke(l: Float, a: Float, b: Float, alpha: Float = 1f): LAB
    operator fun invoke(l: Double, a: Double, b: Double, alpha: Double): LAB =
        invoke(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    operator fun invoke(l: Double, a: Double, b: Double, alpha: Float = 1f): LAB =
        invoke(l.toFloat(), a.toFloat(), b.toFloat(), alpha)
}

/** Create a new [LABColorSpace] that will be calculated relative to the given [whitePoint] */
fun LABColorSpace(whitePoint: WhitePoint): LABColorSpace = when (whitePoint) {
    Illuminant.D65 -> LAB65
    Illuminant.D50 -> LAB50
    else -> LABColorSpaceImpl(whitePoint)
}

private data class LABColorSpaceImpl(override val whitePoint: WhitePoint) : LABColorSpace {
    override val name: String get() = "LAB"
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("LAB")
    override operator fun invoke(l: Float, a: Float, b: Float, alpha: Float): LAB = LAB(l, a, b, alpha, this)
    override fun convert(color: Color): LAB = color.toLAB()
    override fun create(components: FloatArray): LAB = doCreate(components, ::invoke)
    override fun toString(): String = "LABColorSpace($whitePoint)"
}

/** An [LAB] color space calculated relative to [Illuminant.D65] */
val LAB65: LABColorSpace = LABColorSpaceImpl(Illuminant.D65)

/** An [LAB] color space calculated relative to [Illuminant.D50] */
val LAB50: LABColorSpace = LABColorSpaceImpl(Illuminant.D50)

/**
 * CIE LAB color space, also referred to as `CIE 1976 L*a*b*`.
 *
 * The cylindrical representation of this space is [LCH].
 *
 * [LAB] is calculated relative to a [given][space] [whitePoint], which defaults to [Illuminant.D65].
 *
 * | Component  | Description | sRGB D65 Range     |
 * | ---------- | ----------- | ------------------ |
 * | [l]        | lightness   | `[0, 100]`         |
 * | [a]        | green/red   | `[-86.1, 98.23]`   |
 * | [b]        | blue/yellow | `[-107.86, 94.48]` |
 */
data class LAB internal constructor(
    val l: Float,
    val a: Float,
    val b: Float,
    override val alpha: Float = 1f,
    override val space: LABColorSpace,
) : Color {
    companion object : LABColorSpace by LAB65

    override fun toSRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toSRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Lab_to_XYZ.html
        val xyzSpace = XYZColorSpace(space.whitePoint)
        if (l == 0f) return xyzSpace(0.0, 0.0, 0.0)

        val fy = (l + 16) / 116f
        val fz = fy - b / 200f
        val fx = a / 500f + fy

        val yr = if (l > CIE_E_times_K) fy.pow(3) else l / CIE_K
        val zr = fz.pow(3).let { if (it > CIE_E) it else (116 * fz - 16) / CIE_K }
        val xr = fx.pow(3).let { if (it > CIE_E) it else (116 * fx - 16) / CIE_K }

        val wp = space.whitePoint.chromaticity
        return xyzSpace(xr * wp.X, yr * wp.Y, zr * wp.Z, alpha)
    }

    override fun toLCH(): LCH = toPolarModel(a, b) { c, h -> LCHColorSpace(space.whitePoint)(l, c, h, alpha) }
    override fun toLAB(): LAB = this

    override fun toArray(): FloatArray = floatArrayOf(l, a, b, alpha)
}
