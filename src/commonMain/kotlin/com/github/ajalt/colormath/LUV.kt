package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.pow

/**
 * CIE LUV (CIE 1976 L*u*v*) color space.
 *
 * Conversions use D65/2° illuminant, and sRGB profile.
 *
 * [l] is in the range `[0, 100]`. [u] and [v] are in the range `[-100, 100]`
 */
data class LUV(val l: Double, val u: Double, val v: Double, override val alpha: Float = 1f) : Color {
    init {
        require(l in 0.0..100.0) { "l must be in interval [0, 100] in $this" }
        require(alpha in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override fun toRGB(): RGB = when (l) {
        0.0 -> RGB(0, 0, 0, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        if (l == 0.0) return XYZ(0.0, 0.0, 0.0)

        // Equations from http://www.brucelindbloom.com/index.html?Eqn_Luv_to_XYZ.html
        val denominator0 = D65.x + 15 * D65.y + 3 * D65.z
        val u0 = 4 * D65.x / denominator0
        val v0 = 9 * D65.y / denominator0

        val y = if (l > CIE_E_times_K) ((l + 16) / 116).pow(3) else l / CIE_K

        val a = (52 * l / (u + 13 * l * u0) - 1) / 3
        val b = -5 * y
        val c = -1.0 / 3
        val d = y * ((39 * l) / (v + 13 * l * v0) - 5)

        val x = (d - b) / (a - c)
        val z = x * a + b

        // scale XYZ values from `[0, 1]` to `[0, 100]`.
        return XYZ(100 * x, 100 * y, 100 * z, alpha)
    }

    override fun toLUV(): LUV = this
}
