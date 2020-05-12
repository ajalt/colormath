package com.github.ajalt.colormath

import kotlin.math.pow

/**
 * CIE LAB color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [l] is in the range `[0, 100]`. [a] and [b] are in the range [-128, 128]
 */
data class LAB(val l: Double, val a: Double, val b: Double, override val alpha: Float = 1f) : ConvertibleColor {
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

        val d = 6 / 29.0
        fun f(t: Double) = when {
            t > d -> t.pow(3)
            else -> (3 * d.pow(2)) * (t - (4 / 29.0))
        }

        val lp = (l + 16) / 116
        val x = 0.95047 * f(lp + (a / 500))
        val y = f(lp)
        val z = 1.08883 * f(lp - (b / 200))

        return XYZ(x * 100, y * 100, z * 100, alpha)
    }

    override fun toLAB(): LAB = this
}
