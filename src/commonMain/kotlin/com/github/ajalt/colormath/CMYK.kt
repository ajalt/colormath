package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps

/**
 * A color in the CMYK (cyan, magenta, yellow, and key) color model.
 *
 * | Component  | Description | sRGB Range |
 * | ---------- | ----------- | ---------- |
 * | [c]        | cyan        | `[0, 1]`   |
 * | [m]        | magenta     | `[0, 1]`   |
 * | [y]        | yellow      | `[0, 1]`   |
 * | [k]        | key / black | `[0, 1]`   |
 */
data class CMYK(val c: Float, val m: Float, val y: Float, val k: Float, val a: Float = 1f) : Color {
    companion object : ColorModel<CMYK> {
        override val name: String get() = "CMYK"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("C", false, 0f, 1f),
            ColorComponentInfo("M", false, 0f, 1f),
            ColorComponentInfo("Y", false, 0f, 1f),
            ColorComponentInfo("K", false, 0f, 1f),
        )

        override fun convert(color: Color): CMYK = color.toCMYK()
        override fun create(components: FloatArray): CMYK = withValidComps(components) {
            CMYK(it[0], it[1], it[2], it[3], it.getOrElse(4) { 1f })
        }
    }

    /**
     * Construct a CMYK instance from Int values, with the the color channels as percentages in the range `[0, 100]`.
     */
    constructor(c: Int, m: Int, y: Int, k: Int, a: Float = 1f)
            : this(c / 100f, m / 100f, y / 100f, k / 100f, a)

    override val alpha: Float get() = a
    override val model: ColorModel<CMYK> get() = CMYK

    override fun toRGB(): RGB {
        val r = (1 - c) * (1 - k)
        val g = (1 - m) * (1 - k)
        val b = (1 - y) * (1 - k)
        return RGB(r, g, b, alpha)
    }

    override fun toCMYK() = this
    override fun toArray(): FloatArray = floatArrayOf(c, m, y, k, alpha)
}
