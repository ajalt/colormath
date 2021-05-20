package com.github.ajalt.colormath

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A color in the Hue-Saturation-Value color space.
 *
 * @property h The hue, as degrees in the range `[0, 360]`
 * @property s The saturation, as a percent in the range `[0, 100]`
 * @property v The value, as a percent in the range `[0, 100]`
 * @property a The alpha, as a fraction in the range `[0, 1]`
 */
data class HSV(override val h: Int, val s: Int, val v: Int, val a: Float = 1f) : Color, HueColor {
    /**
     * Construct an HSV instance from Float values, with h in `[0, 360]`, and s and v in the range `[0, 1]`.
     */
    constructor(h: Float, s: Float, v: Float, a: Float = 1f)
            : this(h.roundToInt(), (s * 100).roundToInt(), (v * 100).roundToInt(), a)

    init {
        require(h in 0..360) { "h must be in range [0, 360] in $this" }
        require(s in 0..100) { "s must be in range [0, 100] in $this" }
        require(v in 0..100) { "v must be in range [0, 100] in $this" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val h = h / 60.0
        val s = s / 100.0
        val v = v / 100.0
        val hi = floor(h) % 6

        val f = h - floor(h)
        val p =  v * (1 - s)
        val q =  v * (1 - (s * f))
        val t =  v * (1 - (s * (1 - f)))

        val (r, g, b) = when (hi.roundToInt()) {
            0 -> Triple(v, t, p)
            1 -> Triple(q, v, p)
            2 -> Triple(p, v, t)
            3 -> Triple(p, q, v)
            4 -> Triple(t, p, v)
            else -> Triple(v, p, q)
        }
        return RGB(r.toFloat(), g.toFloat(), b.toFloat(), alpha)
    }

    override fun toHSL(): HSL {
        val h = h.toDouble()
        val s = s.toDouble() / 100
        val v = v.toDouble() / 100
        val vmin = max(v, 0.01)

        val l = ((2 - s) * v) / 2
        val lmin = (2 - s) * vmin
        val sl = if (lmin == 2.0) 0.0 else (s * vmin) / (if (lmin <= 1) lmin else 2 - lmin)

        return HSL(h.roundToInt(), (sl * 100).roundToInt(), (l * 100).roundToInt(), alpha)
    }

    override fun toHSV() = this
}
