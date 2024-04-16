package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.HueColor
import com.github.ajalt.colormath.internal.clamp3
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
data class HSV(override val h: Float, val s: Float, val v: Float, override val alpha: Float = 1f) :
    HueColor {
    /** Default constructors for the [HSV] color model. */
    companion object : ColorSpace<HSV> {
        override val name: String get() = "HSV"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HSV", 0f, 1f)
        override fun convert(color: Color): HSV = color.toHSV()
        override fun create(components: FloatArray): HSV = doCreate(components, ::HSV)
    }

    constructor (h: Number, s: Number, v: Number, alpha: Number = 1f)
            : this(h.toFloat(), s.toFloat(), v.toFloat(), alpha.toFloat())

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
    override fun clamp(): HSV = clamp3(h, s, v, alpha, ::copy)
}
