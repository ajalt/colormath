package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfo
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize

/**
 * CIE LCh(ab) color model, the cylindrical representation of [LAB].
 *
 * | Component  | Description  | sRGB Gamut   |
 * | ---------- | ------------ | ------------ |
 * | [l]        | lightness    | `[0, 100]`   |
 * | [c]        | chroma       | `[0, 133.8]` |
 * | [h]        | hue, degrees | `[0, 360)`   |
 */
data class LCH(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object {
        val model = object : ColorModel {
            override val name: String get() = "LCH"
            override val components: List<ColorComponentInfo> = componentInfo(
                ColorComponentInfo("L", false, 0f, 100f),
                ColorComponentInfo("C", false, 0f, 133.80763f),
                ColorComponentInfo("H", true, 0f, 360f),
            )
        }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val model: ColorModel get() = LCH.model

    override fun toRGB(): RGB = toLAB().toRGB()
    override fun toXYZ(): XYZ = toLAB().toXYZ()
    override fun toLAB(): LAB = fromPolarModel(c, h) { a, b -> LAB(l, a, b, alpha) }
    override fun toLCH(): LCH = this

    override fun convertToThis(other: Color): LCH = other.toLCH()
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun fromComponents(components: FloatArray): LCH {
        requireComponentSize(components)
        return LCH(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
