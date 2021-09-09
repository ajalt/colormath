package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.polarComponentInfo

/**
 * HPLuv color space, an alternative to [HSLuv] that preserves as many colors as it can without distorting chroma.
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 * | [p]        | percentage saturation                     | `[0, 100]` |
 * | [l]        | lightness                                 | `[0, 100]` |
 *
 * ### References
 * - [HSLuv homepage](https://www.hsluv.org/)
 */
data class HPLuv(override val h: Float, val p: Float, val l: Float, override val alpha: Float = Float.NaN) : HueColor {
    /** Default constructors for the [HPLuv] color model. */
    companion object : ColorSpace<HPLuv> {
        override val name: String get() = "HPLuv"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HPL")
        override fun convert(color: Color): HPLuv = color.toHPLuv()
        override fun create(components: FloatArray): HPLuv = doCreate(components, ::HPLuv)
    }

    constructor (h: Number, p: Number, l: Number, alpha: Number = Float.NaN)
            : this(h.toFloat(), p.toFloat(), l.toFloat(), alpha.toFloat())

    override val space: ColorSpace<HPLuv> get() = HPLuv

    override fun toLCHuv(): LCHuv {
        if (l > 99.9999) return LCHuv(100f, 0f, h, alpha)
        if (l < 0.00001) return LCHuv(0f, 0f, h, alpha)
        val max = HUSLColorConverter.maxSafeChromaForL(l.toDouble())
        val c = max / 100 * p
        return LCHuv(l, c.toFloat(), h, alpha)
    }

    override fun toSRGB(): RGB = toXYZ().toSRGB()
    override fun toLUV(): LUV = toLCHuv().toLUV()
    override fun toXYZ(): XYZ = toLCHuv().toXYZ()
    override fun toHPLuv(): HPLuv = this
    override fun toArray(): FloatArray = floatArrayOf(h, p, l, alpha)
}
