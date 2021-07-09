package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.withValidComps

/**
 * CIE LCh(uv) color model, the cylindrical representation of [LUV].
 *
 * | Component  | Description  | sRGB Range |
 * | ---------- | ------------ | ---------- |
 * | [h]        | hue, degrees | `[0, 360)` |
 * | [c]        | chroma       | `[0, 180]` |
 * | [l]        | lightness    | `[0, 100]` |
 */
data class HCL(override val h: Float, val c: Float, val l: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorModel<HCL> {
        override val name: String get() = "HCL"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("H", true, 0f, 360f),
            ColorComponentInfo("C", false, 0f, 179.04138f),
            ColorComponentInfo("L", false, 0f, 100f),
        )

        override fun convert(color: Color): HCL = color.toHCL()
        override fun create(components: FloatArray): HCL = withValidComps(components) {
            HCL(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }


    constructor(h: Double, c: Double, l: Double, alpha: Double)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha.toFloat())

    constructor(h: Double, c: Double, l: Double, alpha: Float = 1.0f)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha)

    override val model: ColorModel<HCL> get() = HCL

    override fun toRGB(): RGB = toLUV().toRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUV(l, u, v, alpha) }
    override fun toHCL(): HCL = this
    override fun toArray(): FloatArray = floatArrayOf(h, c, l, alpha)
}
