package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.nanToZero
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.polarComponentInfo
import kotlin.math.min

/**
 * A color model represented with Hue, Saturation, and Lightness.
 *
 * This is a cylindrical representation of the sRGB space used in [RGB].
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 * | [s]        | saturation                                | `[0, 1]`   |
 * | [l]        | lightness                                 | `[0, 1]`   |
 */
data class HSL(override val h: Float, val s: Float, val l: Float, override val alpha: Float = Float.NaN) : HueColor {
    /** Default constructors for the [HSL] color model. */
    companion object : ColorSpace<HSL> {
        override val name: String get() = "HSL"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HSL")
        override fun convert(color: Color): HSL = color.toHSL()
        override fun create(components: FloatArray): HSL = doCreate(components, ::HSL)
    }

    constructor (h: Number, s: Number, l: Number, alpha: Number = Float.NaN)
            : this(h.toFloat(), s.toFloat(), l.toFloat(), alpha.toFloat())

    override val space: ColorSpace<HSL> get() = HSL

    override fun toSRGB(): RGB {
        if (s < 1e-7) return RGB(l, l, l, alpha)

        val h = (h.normalizeDeg() / 30.0).nanToZero()
        val s = s.toDouble()
        val l = l.toDouble()

        fun f(n: Int): Float {
            val k = (n + h) % 12.0
            val a = s * min(l, 1 - l)
            return (l - a * minOf(k - 3, 9 - k, 1.0).coerceAtLeast(-1.0)).toFloat()
        }

        return SRGB(f(0), f(8), f(4), alpha)
    }

    override fun toHSV(): HSV {
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
    override fun toArray(): FloatArray = floatArrayOf(h, s, l, alpha)
}
