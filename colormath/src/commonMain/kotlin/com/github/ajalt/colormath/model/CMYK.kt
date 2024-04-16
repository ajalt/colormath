package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.internal.withValidComps
import com.github.ajalt.colormath.internal.zeroOneComponentInfo

/**
 * A color in the CMYK (cyan, magenta, yellow, and key) color model.
 *
 * Conversions to and from this model use the device-independent ("naive") formulas.
 *
 * | Component  | Description | Range    |
 * | ---------- | ----------- | -------- |
 * | [c]        | cyan        | `[0, 1]` |
 * | [m]        | magenta     | `[0, 1]` |
 * | [y]        | yellow      | `[0, 1]` |
 * | [k]        | key / black | `[0, 1]` |
 */
data class CMYK(
    val c: Float,
    val m: Float,
    val y: Float,
    val k: Float,
    override val alpha: Float = 1f,
) : Color {
    /** Default constructors for the [CMYK] color model. */
    companion object : ColorSpace<CMYK> {
        override val name: String get() = "CMYK"
        override val components: List<ColorComponentInfo> = zeroOneComponentInfo("CMYK")
        override fun convert(color: Color): CMYK = color.toCMYK()
        override fun create(components: FloatArray): CMYK = withValidComps(components) {
            CMYK(it[0], it[1], it[2], it[3], it.getOrElse(4) { 1f })
        }
    }

    constructor (c: Number, m: Number, y: Number, k: Number, alpha: Number = 1f)
            : this(c.toFloat(), m.toFloat(), y.toFloat(), k.toFloat(), alpha.toFloat())

    /**
     * Construct a CMYK instance from Int values, with the color channels as percentages in the range `[0, 100]`.
     */
    constructor(c: Int, m: Int, y: Int, k: Int, alpha: Float = 1f)
            : this(c / 100f, m / 100f, y / 100f, k / 100f, alpha)

    override val space: ColorSpace<CMYK> get() = CMYK

    override fun toSRGB(): RGB {
        val r = (1 - c) * (1 - k)
        val g = (1 - m) * (1 - k)
        val b = (1 - y) * (1 - k)
        return RGB(r, g, b, alpha)
    }

    override fun toCMYK() = this
    override fun toArray(): FloatArray = floatArrayOf(c, m, y, k, alpha)
    override fun clamp(): Color {
        return when {
            c in 0f..1f && m in 0f..1f && y in 0f..1f && k in 0f..1f && alpha in 0f..1f -> this
            else -> copy(
                c = c.coerceIn(0f, 1f),
                m = m.coerceIn(0f, 1f),
                y = y.coerceIn(0f, 1f),
                k = k.coerceIn(0f, 1f),
                alpha = alpha.coerceIn(0f, 1f)
            )
        }
    }
}
