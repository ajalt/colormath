package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A color model represented with Hue, Saturation, and Value.
 *
 * The color space is the same sRGB space used in [RGB].
 *
 * @property h The hue, as degrees in the range `[0, 360]`
 * @property s The saturation, as a fraction in the range `[0, 1]`
 * @property v The value, as a fraction in the range `[0, 1]`
 * @property a The alpha, as a fraction in the range `[0, 1]`
 */
data class HSV(override val h: Float, val s: Float, val v: Float, val a: Float = 1f) : Color, HueColor {
    /**
     * Construct an HSV instance from Float values, with h in `[0, 360]`, and s and v in the range `[0, 1]`.
     */
    constructor(h: Int, s: Int, v: Int, a: Float = 1f) : this(h.toFloat(), s / 100f, v / 100f, a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val h = h.normalizeDeg() / 60f
        val hi = floor(h) % 6

        val f = h - floor(h)
        val p = v * (1f - s)
        val q = v * (1f - (s * f))
        val t = v * (1f - (s * (1f - f)))

        return when (hi.roundToInt()) {
            0 -> RGB(v, t, p, alpha)
            1 -> RGB(q, v, p, alpha)
            2 -> RGB(p, v, t, alpha)
            3 -> RGB(p, q, v, alpha)
            4 -> RGB(t, p, v, alpha)
            else -> RGB(v, p, q, alpha)
        }
    }

    override fun toHSL(): HSL {
        val vmin = max(v, 0.01f)
        val l = ((2 - s) * v) / 2
        val lmin = (2 - s) * vmin
        val sl = if (lmin == 2f) 0f else (s * vmin) / (if (lmin <= 1) lmin else 2 - lmin)
        return HSL(h, sl, l, alpha)
    }

    override fun toHSV() = this

    override fun convertToThis(other: Color): HSV = other.toHSV()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(h, s, v, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { i == 0 }
    override fun fromComponents(components: FloatArray): HSV {
        requireComponentSize(components)
        return HSV(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
