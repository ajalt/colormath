package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.withValidComps


/**
 * The color space describing colors in the [HCL] model.
 */
interface HCLColorSpace : WhitePointColorSpace<HCL> {
    operator fun invoke(h: Float, c: Float, l: Float, alpha: Float = 1f): HCL
    operator fun invoke(h: Double, c: Double, l: Double, alpha: Double): HCL =
        invoke(h.toFloat(), c.toFloat(), l.toFloat(), alpha.toFloat())

    operator fun invoke(h: Double, c: Double, l: Double, alpha: Float = 1f): HCL =
        invoke(h.toFloat(), c.toFloat(), l.toFloat(), alpha)
}

private data class HCLColorSpaceImpl(override val whitePoint: Illuminant) : HCLColorSpace {
    override val name: String get() = "HCL"
    override val components: List<ColorComponentInfo> = componentInfoList(
        ColorComponentInfo("H", true),
        ColorComponentInfo("C", false),
        ColorComponentInfo("L", false),
    )

    override operator fun invoke(h: Float, c: Float, l: Float, alpha: Float): HCL = HCL(h, c, l, alpha, this)
    override fun convert(color: Color): HCL = color.toHCL()
    override fun create(components: FloatArray): HCL = withValidComps(components) {
        HCL(it[0], it[1], it[2], it.getOrElse(3) { 1f })
    }
}

/** An [LCH] color space calculated relative to [Illuminant.D65] */
val HCL65: HCLColorSpace = HCLColorSpaceImpl(Illuminant.D65)

/** An [HCL] color space calculated relative to [Illuminant.D50] */
val HCL50: HCLColorSpace = HCLColorSpaceImpl(Illuminant.D50)

/**
 * CIE LCh(uv) color model, the cylindrical representation of [LUV].
 *
 * | Component  | Description  | sRGB D65 Range |
 * | ---------- | ------------ | -------------- |
 * | [h]        | hue, degrees | `[0, 360)`     |
 * | [c]        | chroma       | `[0, 180]`     |
 * | [l]        | lightness    | `[0, 100]`     |
 */
data class HCL(
    override val h: Float,
    val c: Float,
    val l: Float,
    override val alpha: Float = 1f,
    override val model: HCLColorSpace,
) : HueColor {
    companion object : HCLColorSpace by HCL65 {
        /** Create a new `HCL` color space that will be calculated relative to the given [whitePoint] */
        operator fun invoke(whitePoint: Illuminant): HCLColorSpace = when (whitePoint) {
            Illuminant.D65 -> HCL65
            Illuminant.D50 -> HCL50
            else -> HCLColorSpaceImpl(whitePoint)
        }
    }

    override fun toRGB(): RGB = toLUV().toRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUV(model.whitePoint)(l, u, v, alpha) }
    override fun toHCL(): HCL = this
    override fun toArray(): FloatArray = floatArrayOf(h, c, l, alpha)
}
