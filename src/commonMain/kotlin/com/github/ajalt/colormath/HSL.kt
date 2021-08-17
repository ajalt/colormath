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
data class HSL(override val h: Float, val s: Float, val l: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorSpace<HSL> {
        override val name: String get() = "HSL"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HSL")
        override fun convert(color: Color): HSL = color.toHSL()
        override fun create(components: FloatArray): HSL = doCreate(components, ::HSL)
    }

    /**
     * Construct an HSL instance from `Int` values, with [h] in `[0, 360)`, and [s] and [l] as percentages in the range
     * `[0, 100]`.
     */
    constructor(h: Int, s: Int, l: Int, alpha: Float = 1f) : this(h.toFloat(), s / 100f, l / 100f, alpha)

    constructor (l: Double, a: Double, b: Double, alpha: Double)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    constructor (l: Double, a: Double, b: Double, alpha: Float = 1f)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha)

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
