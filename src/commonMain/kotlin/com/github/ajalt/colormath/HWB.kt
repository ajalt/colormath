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
    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        // https://www.w3.org/TR/css-color-4/#hwb-to-rgb
        val white = w / 100
        val black = b / 100
        if (white + black > 1) {
            val gray = white / (white + black)
            return RGB(gray, gray, gray, a)
        }

        val (r, g, b) = HSL(h, 1f, .5f).toRGB()
        val mul = 1 - white - black
        return RGB(
            r = r * mul + white,
            g = g * mul + white,
            b = b * mul + white,
            a = a
        )
    }

    override fun toHSV(): HSV {
        // http://alvyray.com/Papers/CG/HWB_JGTv208.pdf
        val s = 1 - w / (1 - b)
        val v = 1 - b
        return HSV(h.roundToInt(), (s * 100).roundToInt(), (v * 100).roundToInt(), a)
    }

    override fun toHWB(): HWB = this
}
