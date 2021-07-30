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

private data class LUVColorSpaceImpl(override val whitePoint: WhitePoint) : LUVColorSpace {
    override val name: String get() = "LUV"
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("LUV")
    override operator fun invoke(l: Float, u: Float, v: Float, alpha: Float): LUV = LUV(l, u, v, alpha, this)
    override fun convert(color: Color): LUV = color.toLUV()
    override fun create(components: FloatArray): LUV = doCreate(components, ::invoke)
}

/** An [LUV] color space calculated relative to [WhitePoint.D65] */
val LUV65: LUVColorSpace = LUVColorSpaceImpl(WhitePoint.D65)

/** An [LUV] color space calculated relative to [WhitePoint.D50] */
val LUV50: LUVColorSpace = LUVColorSpaceImpl(WhitePoint.D50)

/**
 * The CIE LUV color space, also referred to as `CIE 1976 L*u*v*`.
 *
 * [LUV] is calculated relative to a [given][model] [whitePoint], which defaults to [WhitePoint.D65].
 *
 * | Component  | Description  | sRGB D65 Range |
 * | ---------- | ------------ | -------------- |
 * | [l]        | lightness    | `[0, 100]`     |
 * | [u]        |              | `[-100, 100]`  |
 * | [v]        |              | `[-100, 100]`  |
 */
data class LUV internal constructor(
    val l: Float,
    val u: Float,
    val v: Float,
    override val alpha: Float = 1f,
    override val model: LUVColorSpace,
) : Color {
    companion object : LUVColorSpace by LUV65 {
        /** Create a new `LUV` color space that will be calculated relative to the given [whitePoint] */
        operator fun invoke(whitePoint: WhitePoint): LUVColorSpace = when (whitePoint) {
            WhitePoint.D65 -> LUV65
            WhitePoint.D50 -> LUV50
            else -> LUVColorSpaceImpl(whitePoint)
        }
    }

    override fun toSRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toSRGB()
    }

    override fun toXYZ(): XYZ {
        val xyzSpace = XYZ(model.whitePoint)
        // http://www.brucelindbloom.com/Eqn_Luv_to_XYZ.html
        if (l == 0f) return xyzSpace(0.0f, 0.0f, 0.0f)

        val wp = model.whitePoint.chromaticity
        val denominator0 = wp.x + 15 * wp.y + 3 * wp.z
        val u0 = 4 * wp.x / denominator0
        val v0 = 9 * wp.y / denominator0

        val y = if (l > CIE_E_times_K) ((l + 16) / 116f).pow(3) else l / CIE_K

        val a = (52 * l / (u + 13 * l * u0) - 1) / 3
        val b = -5 * y
        val c = -1f / 3
        val d = y * ((39 * l) / (v + 13 * l * v0) - 5)

        val x = (d - b) / (a - c)
        val z = x * a + b

        return xyzSpace(x, y, z, alpha)
    }

    override fun toHCL(): HCL = toPolarModel(u, v) { c, h -> HCL(h, c, l, alpha) }
    override fun toLUV(): LUV = this
    override fun toArray(): FloatArray = floatArrayOf(l, u, v, alpha)
}
