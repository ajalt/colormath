package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfo
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize

/**
 * CIE LCh(uv) color model, the cylindrical representation of [LUV].
 *
 * | Component  | Description  | Gamut      |
 * | ---------- | ------------ | ---------- |
 * | [h]        | hue, degrees | `[0, 360)` |
 * | [c]        | chroma       | `[0, 180]` |
 * | [l]        | lightness    | `[0, 100]` |
 */
data class HCL(override val h: Float, val c: Float, val l: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object {
        val model = object : ColorModel {
            override val name: String get() = "HCL"
            override val components: List<ColorComponentInfo> = componentInfo(
                ColorComponentInfo("H", true, 0f, 360f),
                ColorComponentInfo("C", false, 0f, 179.04138f),
                ColorComponentInfo("L", false, 0f, 100f),
            )
        }
    }

    constructor(h: Double, c: Double, l: Double, alpha: Double)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha.toFloat())

    constructor(h: Double, c: Double, l: Double, alpha: Float = 1.0f)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha)

    override val model: ColorModel get() = HCL.model

    override fun toRGB(): RGB = toLUV().toRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUV(l, u, v, alpha) }
    override fun toHCL(): HCL = this

    override fun convertToThis(other: Color): HCL = other.toHCL()
    override fun components(): FloatArray = floatArrayOf(h, c, l, alpha)
    override fun fromComponents(components: FloatArray): HCL {
        requireComponentSize(components)
        return HCL(components[2], components[1], components[0], components.getOrElse(3) { 1f })
    }
}
