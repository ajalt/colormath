package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.polarComponentInfo


/**
 * The color space describing colors in the [LCHuv] model.
 */
interface LCHuvColorSpace : WhitePointColorSpace<LCHuv> {
    operator fun invoke(l: Float, c: Float, h: Float, alpha: Float = 1f): LCHuv
    operator fun invoke(l: Double, c: Double, h: Double, alpha: Double): LCHuv =
        invoke(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    operator fun invoke(l: Double, c: Double, h: Double, alpha: Float = 1f): LCHuv =
        invoke(l.toFloat(), c.toFloat(), h.toFloat(), alpha)
}

/** Create a new [LCHuvColorSpace] that will be calculated relative to the given [whitePoint] */
fun LCHuvColorSpace(whitePoint: WhitePoint): LCHuvColorSpace = when (whitePoint) {
    Illuminant.D65 -> LCHuv65
    Illuminant.D50 -> LCHuv50
    else -> LCHuvColorSpaceImpl(whitePoint)
}

private data class LCHuvColorSpaceImpl(override val whitePoint: WhitePoint) : LCHuvColorSpace {
    override val name: String get() = "LCJ"
    override val components: List<ColorComponentInfo> = polarComponentInfo("LCH")
    override operator fun invoke(l: Float, c: Float, h: Float, alpha: Float): LCHuv = LCHuv(l, c, h, alpha, this)
    override fun convert(color: Color): LCHuv = color.toLCHuv()
    override fun create(components: FloatArray): LCHuv = doCreate(components, ::invoke)
    override fun toString(): String = "HCLColorSpace($whitePoint)"
}

/** An [LCHuv] color space calculated relative to [Illuminant.D65] */
val LCHuv65: LCHuvColorSpace = LCHuvColorSpaceImpl(Illuminant.D65)

/** An [LCHuv] color space calculated relative to [Illuminant.D50] */
val LCHuv50: LCHuvColorSpace = LCHuvColorSpaceImpl(Illuminant.D50)

/**
 * CIE LCh(uv) color model, a.k.a. `HCL`, the cylindrical representation of [LUV].
 *
 * | Component  | Description  | Range      |
 * | ---------- | ------------ | ---------- |
 * | [l]        | lightness    | `[0, 100]` |
 * | [c]        | chroma       | `[0, 100]` |
 * | [h]        | hue, degrees | `[0, 360)` |
 */
data class LCHuv(
    val l: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float = 1f,
    override val space: LCHuvColorSpace,
) : HueColor {
    companion object : LCHuvColorSpace by LCHuv65

    override fun toSRGB(): RGB = toLUV().toSRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUV(space.whitePoint)(l, u, v, alpha) }
    override fun toLCHuv(): LCHuv = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
}