package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.withValidComps

/**
 * CIE LCh(ab) color model, the cylindrical representation of [LAB].
 *
 * | Component  | Description  | sRGB Range   |
 * | ---------- | ------------ | ------------ |
 * | [l]        | lightness    | `[0, 100]`   |
 * | [c]        | chroma       | `[0, 133.8]` |
 * | [h]        | hue, degrees | `[0, 360)`   |
 */
data class LCH(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorModel<LCH> {
        override val name: String get() = "LCH"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("L", false),
            ColorComponentInfo("C", false),
            ColorComponentInfo("H", true),
        )

        override fun convert(color: Color): LCH = color.toLCH()
        override fun create(components: FloatArray): LCH = withValidComps(components) {
            LCH(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val model: ColorModel<LCH> get() = LCH

    override fun toRGB(): RGB = toLAB().toRGB()
    override fun toXYZ(): XYZ = toLAB().toXYZ()
    override fun toLAB(): LAB = fromPolarModel(c, h) { a, b -> LAB(l, a, b, alpha) }
    override fun toLCH(): LCH = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
}
