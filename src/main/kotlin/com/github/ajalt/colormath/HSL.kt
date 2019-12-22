package com.github.ajalt.colormath

import kotlin.math.roundToInt

/**
 * A color in the Hue-Saturation-Lightness color space.
 *
 * @property h The hue, as degrees in the range `[0, 360]`
 * @property s The saturation, as a percent in the range `[0, 100]`
 * @property l The lightness, as a percent in the range `[0, 100]`
 * @property a The alpha, as a fraction in the range `[0, 1]`
 */
data class HSL(override val h: Int, val s: Int, val l: Int, val a: Float = 1f) : ConvertibleColor, HueColor {
    init {
        require(h in 0..360) { "h must be in range [0, 360]" }
        require(s in 0..100) { "s must be in range [0, 100]" }
        require(l in 0..100) { "l must be in range [0, 100]" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    /**
     * Construct an HSL instance from Float values, with h in `[0, 360]`, and s and l in the range `[0, 1]`.
     */
    constructor(h: Float, s: Float, l: Float, a: Float = 1f)
            : this(h.roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt(), a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val h = this.h / 360.0
        val s = this.s / 100.0
        val l = this.l / 100.0
        if (s == 0.0) {
            val v = (l * 255).roundToInt()
            return RGB(v, v, v)
        }

        val t2 = when {
            l < 0.5 -> l * (1 + s)
            else -> l + s - l * s
        }

        val t1 = 2 * l - t2

        val rgb = arrayOf(0.0, 0.0, 0.0)
        for (i in 0..2) {
            var t3: Double = h + 1.0 / 3.0 * -(i - 1.0)
            if (t3 < 0) t3 += 1.0
            if (t3 > 1) t3 -= 1.0

            val v: Double = when {
                6 * t3 < 1 -> t1 + (t2 - t1) * 6 * t3
                2 * t3 < 1 -> t2
                3 * t3 < 2 -> t1 + (t2 - t1) * (2.0 / 3.0 - t3) * 6
                else -> t1
            }

            rgb[i] = v
        }

        return RGB(rgb[0], rgb[1], rgb[2], alpha.toDouble())
    }

    override fun toHSV(): HSV {
        val h = this.h.toDouble()
        var s = this.s.toDouble() / 100
        var l = this.l.toDouble() / 100
        var smin = s
        val lmin = maxOf(l, 0.01)

        l *= 2
        s *= if (l <= 1) l else 2 - l
        smin *= if (lmin <= 1) lmin else 2 - lmin
        val v = (l + s) / 2
        val sv = if (l == 0.0) (2 * smin) / (lmin + smin) else (2 * s) / (l + s)

        return HSV(h.roundToInt(), (sv * 100).roundToInt(), (v * 100).roundToInt(), alpha)
    }

    override fun toHSL() = this
}
