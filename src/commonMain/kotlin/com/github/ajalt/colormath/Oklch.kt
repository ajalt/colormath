package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex

/**
 * Oklch color model, the cylindrical representation of [Oklab].
 *
 * | Component  | Description  | Gamut      |
 * | ---------- | ------------ | ---------- |
 * | [l]        | lightness    | `[0, 100]` |
 * | [c]        | chroma       | `[0, 230]` |
 * | [h]        | hue, degrees | `[0, 360)` |
 */
data class Oklch(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toOklab().toRGB()
    }

    override fun toXYZ(): XYZ = toOklab().toXYZ()

    override fun toOklab(): Oklab = fromPolarModel(c, h) { a, b -> return Oklab(l, a, b, alpha) }

    override fun toOklch(): Oklch = this
    override fun convertToThis(other: Color): Oklch = other.toOklch()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { i == 2 }
    override fun fromComponents(components: FloatArray): Oklch {
        requireComponentSize(components)
        return Oklch(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
