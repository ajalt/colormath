package com.github.ajalt.colormath

import kotlin.math.pow

/**
 * Linear sRGB color space.
 *
 * This color space removes the nonlinearity present in [RGB].
 */
data class LinearRGB(val r: Float, val g: Float, val b: Float, val a: Float = 1f) : Color {
    constructor(r: Double, g: Double, b: Double, a: Float = 1f)
            : this(r.toFloat(), g.toFloat(), b.toFloat(), a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        return RGB(gamma(r), gamma(g), gamma(b), a)
    }

    override fun toLinearRGB(): LinearRGB = this

    // http://entropymine.com/imageworsener/srgbformula/
    private fun gamma(l: Float): Float {
        return when {
            l <= 0.0031308 -> l * 12.92f
            else -> 1.055f * l.pow(1 / 2.4f) - 0.055f
        }
    }
}
