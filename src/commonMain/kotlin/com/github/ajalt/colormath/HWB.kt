package com.github.ajalt.colormath

import kotlin.math.roundToInt

/**
 * A color in the Hue-Whiteness-Blackness color space.
 *
 * @property h The hue, as degrees in the range `[0, 360]`
 * @property w The amount of white to mix in, as a percent in the range `[0, 100]`
 * @property b The lightness, as a percent in the range `[0, 100]`
 * @property a The alpha, as a fraction in the range `[0, 1]`
 */
data class HWB(val h: Float, val w: Float, val b: Float, val a: Float = 1f) : Color {
    constructor(h: Double, w: Double, b: Double, alpha: Double = 1.0)
            : this(h.toFloat(), w.toFloat(), b.toFloat(), alpha.toFloat())

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        // Algorithm from Smith and Lyons, http://alvyray.com/Papers/CG/HWB_JGTv208.pdf, Appendix B

        val h = this.h / 60.0 // Smith defines hue as normalized to [0, 6] for some reason
        val w = this.w / 100.0
        val b = this.b / 100.0
        val a = this.a.toDouble()

        // Smith just declares that w + b must be <= 1. We use the fast-exit from
        // https://www.w3.org/TR/css-color-4/#hwb-to-rgb rather than normalizing.
        if (w + b >= 1) {
            val gray = (w / (w + b))
            return RGB(gray, gray, gray, a)
        }

        val v = 1 - b
        val i = h.toInt()
        val f = when {
            i % 2 == 1 -> 1 - (h - i)
            else -> h - i
        }
        val n = w + f * (v - w) // linear interpolation between w and v
        return when (i) {
            1 -> RGB(n, v, w, a)
            2 -> RGB(w, v, n, a)
            3 -> RGB(w, n, v, a)
            4 -> RGB(n, w, v, a)
            5 -> RGB(v, w, n, a)
            else -> RGB(v, n, w, a)
        }
    }

    override fun toHSV(): HSV {
        // http://alvyray.com/Papers/CG/HWB_JGTv208.pdf, Page 3
        val w = this.w / 100
        val b = this.b / 100
        val s = 1 - w / (1 - b)
        val v = 1 - b
        return HSV(h.roundToInt(), (s * 100).roundToInt(), (v * 100).roundToInt(), a)
    }

    override fun toHWB(): HWB = this
}
