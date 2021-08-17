package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.polarComponentInfo
import kotlin.math.max

/**
 * A color model represented with Hue, Saturation, and Value.
 *
 * This is a cylindrical representation of the sRGB space used in [RGB].
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 * | [s]        | saturation                                | `[0, 1]`   |
 * | [v]        | value                                     | `[0, 1]`   |
 */
data class HSV(override val h: Float, val s: Float, val v: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorSpace<HSV> {
        override val name: String get() = "HSV"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HSV")
        override fun convert(color: Color): HSV = color.toHSV()
        override fun create(components: FloatArray): HSV = doCreate(components, ::HSV)
    }

    /**
     * Construct an HSV instance from Int values, with [h] in `[0, 360]`, and [s] and [v] in the range `[0, 100]`.
     */
    constructor(h: Int, s: Int, v: Int, a: Float = 1f) : this(h.toFloat(), s / 100f, v / 100f, a)

    constructor (l: Double, a: Double, b: Double, alpha: Double)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    constructor (l: Double, a: Double, b: Double, alpha: Float = 1f)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha)

    override val space: ColorSpace<HSV> get() = HSV

    override fun toSRGB(): RGB {
        if (s < 1e-7) return RGB(v, v, v, alpha)
        val v = v.toDouble()
        val h = (h.normalizeDeg() / 60.0)
        val s = s.toDouble()

        fun f(n: Int): Float {
            val k = (n + h) % 6
            return (v - v * s * minOf(k, 4 - k, 1.0).coerceAtLeast(0.0)).toFloat()
        }
        return SRGB(f(5), f(3), f(1), alpha)
    }

    override fun toHSL(): HSL {
        val vmin = max(v, 0.01f)
        val l = ((2 - s) * v) / 2
        val lmin = (2 - s) * vmin
        val sl = if (lmin == 2f) 0f else (s * vmin) / (if (lmin <= 1) lmin else 2 - lmin)
        return HSL(h, sl, l, alpha)
    }

    override fun toHSV() = this
    override fun toArray(): FloatArray = floatArrayOf(h, s, v, alpha)
}
