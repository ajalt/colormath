package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps

/**
 * An ANSI-16 color code
 *
 * Conversions to [Ansi16] will always use foreground color codes. Conversions from [Ansi16] to [RGB] use the Windows XP
 * Console palette.
 *
 * ## Valid codes
 *
 * | Color  | Foreground | Background | Bright FG | Bright BG |
 * | ------ | ---------- | ---------- | --------- | --------- |
 * | black  | 30         | 40         | 90        | 100       |
 * | red    | 31         | 41         | 91        | 101       |
 * | green  | 32         | 42         | 92        | 102       |
 * | yellow | 33         | 43         | 93        | 103       |
 * | blue   | 34         | 44         | 94        | 104       |
 * | purple | 35         | 45         | 95        | 105       |
 * | cyan   | 36         | 46         | 96        | 106       |
 * | white  | 37         | 47         | 97        | 107       |
 */
data class Ansi16(val code: Int) : Color {
    /** Default constructors for the [Ansi16] color model. */
    companion object : ColorSpace<Ansi16> {
        override val name: String get() = "Ansi16"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("code", false),
        )

        override fun convert(color: Color): Ansi16 = color.toAnsi16()
        override fun create(components: FloatArray): Ansi16 = withValidComps(components) {
            Ansi16(it[0].toInt())
        }
    }

    override val alpha: Float get() = Float.NaN
    override val space: ColorSpace<Ansi16> get() = Ansi16

    override fun toSRGB(): RGB {
        // grayscale
        when (code) {
            30, 40 -> return RGB(0f, 0f, 0f)
            90, 100 -> return RGB.from255(128, 128, 128)
            37, 47 -> return RGB.from255(192, 192, 192)
            97, 107 -> return RGB(1.0f, 1.0f, 1.0f)
        }

        // color
        val color = code % 10
        val mul = if (code > 50) 1f else 0.5f
        val r = ((color % 2) * mul)
        val g = (((color / 2) % 2) * mul)
        val b = (((color / 4) % 2) * mul)

        return RGB(r, g, b)
    }

    override fun toAnsi256() = when {
        code >= 90 -> Ansi256(code - 90 + 8)
        else -> Ansi256(code - 30)
    }

    override fun toAnsi16() = this
    override fun toArray(): FloatArray = floatArrayOf(code.toFloat(), alpha)
}
