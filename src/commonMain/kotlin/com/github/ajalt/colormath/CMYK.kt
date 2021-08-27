package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.rectangularComponentInfo
import com.github.ajalt.colormath.internal.withValidComps

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
data class CMYK(val c: Float, val m: Float, val y: Float, val k: Float, override val alpha: Float = Float.NaN) : Color {
    companion object : ColorSpace<CMYK> {
        override val name: String get() = "CMYK"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("CMYK")
        override fun convert(color: Color): CMYK = color.toCMYK()
        override fun create(components: FloatArray): CMYK = withValidComps(components) {
            CMYK(it[0], it[1], it[2], it[3], it.getOrElse(4) { 1f })
        }
    }

    constructor (c: Number, m: Number, y: Number, k: Number, alpha: Number = Float.NaN)
            : this(c.toFloat(), m.toFloat(), y.toFloat(), k.toFloat(), alpha.toFloat())

    /**
     * Construct a CMYK instance from Int values, with the color channels as percentages in the range `[0, 100]`.
     */
    constructor(c: Int, m: Int, y: Int, k: Int, alpha: Float = Float.NaN)
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
}
