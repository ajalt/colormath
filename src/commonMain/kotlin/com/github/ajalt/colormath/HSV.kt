package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.withValidComps
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A color model represented with Hue, Saturation, and Value.
 *
 * This is a cylindrical representation of the sRGB space used in [RGB].
 *
 * | Component  | Description  | Gamut      |
 * | ---------- | ------------ | ---------- |
 * | [h]        | hue, degrees | `[0, 360)` |
 * | [s]        | saturation   | `[0, 1]`   |
 * | [v]        | value        | `[0, 1]`   |
 */
data class HSV(override val h: Float, val s: Float, val v: Float, val a: Float = 1f) : Color, HueColor {
    companion object : ColorModel<HSV> {
        override val name: String get() = "HSV"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("H", true, 0f, 360f),
            ColorComponentInfo("S", false, 0f, 1f),
            ColorComponentInfo("V", false, 0f, 1f),
        )

        override fun convert(color: Color): HSV = color.toHSV()
        override fun create(components: FloatArray): HSV = withValidComps(components) {
            HSV(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    /**
     * Construct an HSV instance from Int values, with [h] in `[0, 360]`, and [s] and [v] in the range `[0, 100]`.
     */
    constructor(h: Int, s: Int, v: Int, a: Float = 1f) : this(h.toFloat(), s / 100f, v / 100f, a)

    override val alpha: Float get() = a
    override val model: ColorModel<HSV> get() = HSV

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
    override fun toArray(): FloatArray = floatArrayOf(h, s, v, alpha)
}
