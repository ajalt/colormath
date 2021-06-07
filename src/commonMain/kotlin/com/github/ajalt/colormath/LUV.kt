package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * CIE LUV (CIE 1976 L*u*v*) color space.
 *
 * [l] is in the range `[0, 100]`. [u] and [v] are in the range `[-100, 100]`
 */
data class LUV(val l: Float, val u: Float, val v: Float, override val alpha: Float = 1f) : Color {
    constructor(l: Double, u: Double, v: Double, alpha: Double = 1.0)
            : this(l.toFloat(), u.toFloat(), v.toFloat(), alpha.toFloat())

    init {
        require(l in 0.0..100.0) { "l must be in interval [0, 100] in $this" }
        require(alpha in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Luv_to_XYZ.html
        if (l == 0f) return XYZ(0.0, 0.0, 0.0)

        val denominator0 = D65.x + 15 * D65.y + 3 * D65.z
        val u0 = 4 * D65.x / denominator0
        val v0 = 9 * D65.y / denominator0

        val y = if (l > CIE_E_times_K) ((l + 16) / 116f).pow(3) else l / CIE_K

        val a = (52 * l / (u + 13 * l * u0) - 1) / 3
        val b = -5 * y
        val c = -1f / 3
        val d = y * ((39 * l) / (v + 13 * l * v0) - 5)

        val x = (d - b) / (a - c)
        val z = x * a + b

        // scale XYZ values from `[0, 1]` to `[0, 100]`.
        return XYZ(100 * x, 100 * y, 100 * z, alpha)
    }

    override fun toLUV(): LUV = this

    override fun toLCH(): LCH {
        // http://www.brucelindbloom.com/Eqn_Luv_to_LCH.html
        val c = sqrt(u * u + v * v)
        val h = if (c < 1e-8) 0f else atan2(v, u).radToDeg()
        return LCH(l, c, h.normalizeDeg())
    }
}
