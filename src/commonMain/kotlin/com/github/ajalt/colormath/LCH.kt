package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.polarComponentInfo

/**
 * The color space describing colors in the [LCH] model.
 */
interface LCHColorSpace : WhitePointColorSpace<LCH> {
    operator fun invoke(l: Float, c: Float, h: Float, alpha: Float = 1f): LCH
    operator fun invoke(l: Double, c: Double, h: Double, alpha: Double): LCH =
        invoke(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    operator fun invoke(l: Double, c: Double, h: Double, alpha: Float = 1f): LCH =
        invoke(l.toFloat(), c.toFloat(), h.toFloat(), alpha)
}

private data class LCHColorSpaceImpl(override val whitePoint: Illuminant) : LCHColorSpace {
    override val name: String get() = "LCH"
    override val components: List<ColorComponentInfo> = polarComponentInfo("LCH")
    override operator fun invoke(l: Float, c: Float, h: Float, alpha: Float): LCH = LCH(l, c, h, alpha, this)
    override fun convert(color: Color): LCH = color.toLCH()
    override fun create(components: FloatArray): LCH = doCreate(components, ::invoke)
}

/** An [LCH] color space calculated relative to [Illuminant.D65] */
val LCH65: LCHColorSpace = LCHColorSpaceImpl(Illuminant.D65)

/** An [LCH] color space calculated relative to [Illuminant.D50] */
val LCH50: LCHColorSpace = LCHColorSpaceImpl(Illuminant.D50)


/**
 * CIE LCh(ab) color model, the cylindrical representation of [LAB].
 *
 * | Component  | Description  | sRGB D65 Range |
 * | ---------- | ------------ | -------------- |
 * | [l]        | lightness    | `[0, 100]`     |
 * | [c]        | chroma       | `[0, 133.8]`   |
 * | [h]        | hue, degrees | `[0, 360)`     |
 */
data class LCH internal constructor(
    val l: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float = 1f,
    override val model: LCHColorSpace,
) : HueColor {
    companion object : LCHColorSpace by LCH65 {
        /** Create a new `LCH` color space that will be calculated relative to the given [whitePoint] */
        operator fun invoke(whitePoint: Illuminant): LCHColorSpace = when (whitePoint) {
            Illuminant.D65 -> LCH65
            Illuminant.D50 -> LCH50
            else -> LCHColorSpaceImpl(whitePoint)
        }
    }

    override fun toRGB(): RGB = toLAB().toRGB()
    override fun toXYZ(): XYZ = toLAB().toXYZ()
    override fun toLAB(): LAB = fromPolarModel(c, h) { a, b -> LAB(model.whitePoint)(l, a, b, alpha) }
    override fun toLCH(): LCH = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
}
