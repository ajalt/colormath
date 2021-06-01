package com.github.ajalt.colormath

/**
 * A color in the sRGB color space, represented with Hue, Saturation, and Lightness.
 *
 * @property h The hue, as degrees in the range `[0, 360]`
 * @property s The saturation, as a fraction in the range `[0, 1]`
 * @property l The lightness, as a fraction in the range `[0, 1]`
 * @property a The alpha, as a fraction in the range `[0, 1]`
 */
data class HSL(override val h: Float, val s: Float, val l: Float, val a: Float = 1f) : Color, HueColor {
    /**
     * Construct an HSL instance from Int values, with h in `[0, 360]`, and s and l as percentages in the range `[0,
     * 100]`.
     */
    constructor(h: Int, s: Int, l: Int, a: Float = 1f) : this(h.toFloat(), s / 100f, l / 100f, a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val h = this.h.normalizeDeg() / 360.0
        val s = this.s.toDouble()
        val l = this.l.toDouble()
        if (s == 0.0) {
            return RGB(l, l, l)
        }

        val t2 = when {
            l < 0.5 -> l * (1 + s)
            else -> l + s - l * s
        }

        val t1 = 2 * l - t2

        fun t(i: Int): Float {
            var t3: Double = h + 1.0 / 3.0 * -(i - 1.0)
            if (t3 < 0) t3 += 1.0
            if (t3 > 1) t3 -= 1.0

            return when {
                6 * t3 < 1 -> t1 + (t2 - t1) * 6 * t3
                2 * t3 < 1 -> t2
                3 * t3 < 2 -> t1 + (t2 - t1) * (2.0 / 3.0 - t3) * 6
                else -> t1
            }.toFloat()
        }

        return RGB(t(0), t(1), t(2), alpha)
    }

    override fun toHSV(): HSV {
        val h = this.h.normalizeDeg()
        var s = this.s
        var l = this.l
        var smin = s
        val lmin = maxOf(l, 0.01f)

        l *= 2
        s *= if (l <= 1) l else 2 - l
        smin *= if (lmin <= 1) lmin else 2 - lmin
        val v = (l + s) / 2
        val sv = if (l == 0f) (2 * smin) / (lmin + smin) else (2 * s) / (l + s)

        return HSV(h, sv, v, alpha)
    }

    override fun toHSL() = this
}
