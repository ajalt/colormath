package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex

/**
 * CIE LCh(ab) color model, the cylindrical representation of [LAB].
 *
 * | Component  | Description  | Gamut      |
 * | ---------- | ------------ | ---------- |
 * | [l]        | lightness    | `[0, 100]` |
 * | [c]        | chroma       | `[0, 230]` |
 * | [h]        | hue, degrees | `[0, 360)` |
 */
data class LCH(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override fun toRGB(): RGB = toLAB().toRGB()
    override fun toXYZ(): XYZ = toLAB().toXYZ()
    override fun toLAB(): LAB = fromPolarModel(c, h) { a, b -> LAB(l, a, b, alpha) }
    override fun toLCH(): LCH = this

    override fun convertToThis(other: Color): LCH = other.toLCH()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { i == 2 }
    override fun fromComponents(components: FloatArray): LCH {
        requireComponentSize(components)
        return LCH(components[2], components[1], components[0], components.getOrElse(3) { 1f })
    }
}
