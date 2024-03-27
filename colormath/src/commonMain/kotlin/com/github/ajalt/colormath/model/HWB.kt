package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.HueColor
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.polarComponentInfo
import kotlin.math.roundToInt

/**
 * A color model represented with Hue, Whiteness, and Blackness.
 *
 * This is a cylindrical representation of the sRGB space used in [RGB].
 *
 * | Component  | Description  | Range      |
 * | ---------- | ------------ | ---------- |
 * | [h]        | hue, degrees | `[0, 360)` |
 * | [w]        | whiteness    | `[0, 1]`   |
 * | [b]        | blackness    | `[0, 1]`   |
 */
data class HWB(override val h: Float, val w: Float, val b: Float, override val alpha: Float = 1f) :
    Color,
    HueColor {
    /** Default constructors for the [HWB] color model. */
    companion object : ColorSpace<HWB> {
        override val name: String get() = "HWB"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HWB", 0f, 1f)
        override fun convert(color: Color): HWB = color.toHWB()
        override fun create(components: FloatArray): HWB = doCreate(components, ::HWB)
    }

    constructor(h: Number, w: Number, b: Number, alpha: Number = 1f)
            : this(h.toFloat(), w.toFloat(), b.toFloat(), alpha.toFloat())

    override val space: ColorSpace<HWB> get() = HWB

    override fun toSRGB(): RGB {
        // Algorithm from Smith and Lyons, http://alvyray.com/Papers/CG/HWB_JGTv208.pdf, Appendix B

        val h = this.h / 60f // Smith defines hue as normalized to [0, 6] for some reason
        val w = this.w
        val b = this.b
        val a = this.alpha

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
        return HSV(h.roundToInt(), (s * 100).roundToInt(), (v * 100).roundToInt(), alpha)
    }

    override fun toHWB(): HWB = this
    override fun toArray(): FloatArray = floatArrayOf(h, w, b, alpha)
}
