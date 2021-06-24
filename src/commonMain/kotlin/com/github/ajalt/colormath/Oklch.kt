package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.degToRad
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex
import kotlin.math.cos
import kotlin.math.sin

/**
 * Oklch color model, the cylindrical representation of [Oklab].
 *
 * [l], lightness, is a percentage, typically in the range `[0, 100]`, but theoretically the maximum is unbounded.
 * [c], chroma, is typically in the range `[0, 230]`, but theoretically the maximum is unbounded.
 * [h], hue, is an angle in degrees, in the range `[0, 360]`
 */
data class Oklch(val l: Float, val c: Float, val h: Float, override val alpha: Float = 1f) : Color {
    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)


    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toOklab().toRGB()
    }

    // https://bottosson.github.io/posts/oklab/#the-oklab-color-space
    // This is the same formula as LCH -> LAB
    override fun toOklab(): Oklab {
        val hDegrees = h.degToRad()
        val a = c * cos(hDegrees)
        val b = c * sin(hDegrees)
        return Oklab(l.toDouble(), a.toDouble(), b.toDouble())
    }

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
