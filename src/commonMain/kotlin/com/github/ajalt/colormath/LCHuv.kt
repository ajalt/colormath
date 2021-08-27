package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.adaptToThis
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.polarComponentInfo


/**
 * The color space describing colors in the [LCHuv] model.
 */
interface LCHuvColorSpace : WhitePointColorSpace<LCHuv> {
    operator fun invoke(l: Number, c: Number, h: Number, alpha: Number = Float.NaN): LCHuv
}

/** Create a new [LCHuvColorSpace] that will be calculated relative to the given [whitePoint] */
fun LCHuvColorSpace(whitePoint: WhitePoint): LCHuvColorSpace = when (whitePoint) {
    Illuminant.D65 -> LCHuvColorSpaces.LCHuv65
    Illuminant.D50 -> LCHuvColorSpaces.LCHuv50
    else -> LCHuvColorSpaceImpl(whitePoint)
}

private data class LCHuvColorSpaceImpl(override val whitePoint: WhitePoint) : LCHuvColorSpace {
    override val name: String get() = "LCHuv"
    override val components: List<ColorComponentInfo> = polarComponentInfo("LCH")
    override fun convert(color: Color): LCHuv = adaptToThis(color) { it.toLCHuv() }
    override fun create(components: FloatArray): LCHuv = doCreate(components, ::invoke)
    override fun toString(): String = "LCHuvColorSpace($whitePoint)"
    override operator fun invoke(l: Number, c: Number, h: Number, alpha: Number): LCHuv =
        LCHuv(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat(), this)
}

object LCHuvColorSpaces {
    /** An [LCHuv] color space calculated relative to [Illuminant.D65] */
    val LCHuv65: LCHuvColorSpace = LCHuvColorSpaceImpl(Illuminant.D65)

    /** An [LCHuv] color space calculated relative to [Illuminant.D50] */
    val LCHuv50: LCHuvColorSpace = LCHuvColorSpaceImpl(Illuminant.D50)
}

/**
 * CIE LCh(uv) color model, a.k.a. `HCL`, the cylindrical representation of [LUV].
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [l]        | lightness                                 | `[0, 100]` |
 * | [c]        | chroma                                    | `[0, 100]` |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 */
data class LCHuv internal constructor(
    val l: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float,
    override val space: LCHuvColorSpace,
) : HueColor {
    /** Default constructors for the [LCHuv] color model: the [LCHLCHuv65ab65][LCHuvColorSpaces.LCHuv65] space. */
    companion object : LCHuvColorSpace by LCHuvColorSpaces.LCHuv65

    override fun toHSLuv(): HSLuv {
        if (l > 99.9999) return HSLuv(h, 0f, 100f, alpha)
        if (l < 0.00001) return HSLuv(h, 0f, 0f, alpha)
        val max = HUSLColorConverter.maxChromaForLH(l.toDouble(), h.toDouble())
        val s = c / max * 100
        return HSLuv(h, s.toFloat(), l, alpha)
    }

    override fun toHPLuv(): HPLuv {
        if (l > 99.9999) return HPLuv(h, 0f, 100f, alpha)
        if (l < 0.00001) return HPLuv(h, 0f, 0f, alpha)
        val max = HUSLColorConverter.maxSafeChromaForL(l.toDouble())
        val s = c / max * 100
        return HPLuv(h, s.toFloat(), l, alpha)
    }

    override fun toSRGB(): RGB = toLUV().toSRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUVColorSpace(space.whitePoint)(l, u, v, alpha) }
    override fun toLCHuv(): LCHuv = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
}
