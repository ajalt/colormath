package com.github.ajalt.colormath

/**
 * A color in the CMYK (cyan, magenta, yellow, and key) color model.
 *
 * All color channels are fractions in the range `[0, 1]`.
 */
data class CMYK(val c: Float, val m: Float, val y: Float, val k: Float, val a: Float = 1f) : Color {
    /**
     * Construct a CMYK instance from Int values, with the the color channels as percentages in the range `[0, 100]`.
     */
    constructor(c: Int, m: Int, y: Int, k: Int, a: Float = 1f)
            : this(c / 100f, m / 100f, y / 100f, k / 100f, a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val r = (1 - c) * (1 - k)
        val g = (1 - m) * (1 - k)
        val b = (1 - y) * (1 - k)
        return RGB(r, g, b, alpha)
    }

    override fun toCMYK() = this
}
