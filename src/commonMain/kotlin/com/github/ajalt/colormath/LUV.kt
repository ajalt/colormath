package com.github.ajalt.colormath

import com.github.ajalt.colormath.LUV.Companion.whitePoint
import com.github.ajalt.colormath.XYZ.Companion.whitePoint
import com.github.ajalt.colormath.internal.*
import kotlin.math.pow

/**
 * The color space describing colors in the [LUV] model.
 */
interface LUVColorSpace : WhitePointColorSpace<LUV> {
    operator fun invoke(l: Float, u: Float, v: Float, alpha: Float = 1f): LUV
    operator fun invoke(l: Double, u: Double, v: Double, alpha: Double): LUV =
        invoke(l.toFloat(), u.toFloat(), v.toFloat(), alpha.toFloat())

    operator fun invoke(l: Double, u: Double, v: Double, alpha: Float = 1f): LUV =
        invoke(l.toFloat(), u.toFloat(), v.toFloat(), alpha)
}

/** Create a new [LUVColorSpace] that will be calculated relative to the given [whitePoint] */
fun LUVColorSpace(whitePoint: WhitePoint): LUVColorSpace = when (whitePoint) {
    Illuminant.D65 -> LUV65
    Illuminant.D50 -> LUV50
    else -> LUVColorSpaceImpl(whitePoint)
}

private data class LUVColorSpaceImpl(override val whitePoint: WhitePoint) : LUVColorSpace {
    override val name: String get() = "LUV"
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("LUV")
    override operator fun invoke(l: Float, u: Float, v: Float, alpha: Float): LUV = LUV(l, u, v, alpha, this)
    override fun convert(color: Color): LUV = color.toLUV()
    override fun create(components: FloatArray): LUV = doCreate(components, ::invoke)
    override fun toString(): String = "LUVColorSpace($whitePoint)"
}

/** An [LUV] color space calculated relative to [Illuminant.D65] */
val LUV65: LUVColorSpace = LUVColorSpaceImpl(Illuminant.D65)

/** An [LUV] color space calculated relative to [Illuminant.D50] */
val LUV50: LUVColorSpace = LUVColorSpaceImpl(Illuminant.D50)

/**
 * The CIE LUV color space, also referred to as `CIE 1976 L*u*v*`.
 *
 * [LUV] is calculated relative to a [given][space] [whitePoint], which defaults to [Illuminant.D65].
 *
 * | Component  | Description  | Range         |
 * | ---------- | ------------ | ------------- |
 * | [l]        | lightness    | `[0, 100]`    |
 * | [u]        |              | `[-100, 100]` |
 * | [v]        |              | `[-100, 100]` |
 */
data class LUV internal constructor(
    val l: Float,
    val u: Float,
    val v: Float,
    override val alpha: Float = 1f,
    override val space: LUVColorSpace,
) : Color {
    companion object : LUVColorSpace by LUV65 {
        /** Create a new `LUV` color space that will be calculated relative to the given [whitePoint] */
        operator fun invoke(whitePoint: WhitePoint): LUVColorSpace = when (whitePoint) {
            Illuminant.D65 -> LUV65
            Illuminant.D50 -> LUV50
            else -> LUVColorSpaceImpl(whitePoint)
        }
    }

    override fun toSRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toSRGB()
    }

    override fun toXYZ(): XYZ {
        val xyzSpace = XYZColorSpace(space.whitePoint)
        // http://www.brucelindbloom.com/Eqn_Luv_to_XYZ.html
        if (l == 0f) return xyzSpace(0.0f, 0.0f, 0.0f)

        val wp = space.whitePoint.chromaticity
        val denominator0 = wp.X + 15 * wp.Y + 3 * wp.Z
        val u0 = 4 * wp.X / denominator0
        val v0 = 9 * wp.Y / denominator0

        val y = if (l > CIE_E_times_K) ((l + 16) / 116f).pow(3) else l / CIE_K

        val a = (52 * l / (u + 13 * l * u0) - 1) / 3
        val b = -5 * y
        val c = -1f / 3
        val d = y * ((39 * l) / (v + 13 * l * v0) - 5)

        val x = (d - b) / (a - c)
        val z = x * a + b

        return xyzSpace(x, y, z, alpha)
    }

    override fun toLCHuv(): LCHuv = toPolarModel(u, v) { c, h -> LCHuv(l, c, h, alpha) }
    override fun toLUV(): LUV = this
    override fun toArray(): FloatArray = floatArrayOf(l, u, v, alpha)
}
