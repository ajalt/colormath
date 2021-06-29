package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps

/**
 * An ANSI-16 color code
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
    companion object {
        val model = object : ColorModel {
            override val name: String get() = "Ansi16"
            override val components: List<ColorComponentInfo> = componentInfoList(
                ColorComponentInfo("code", false, 30f, 37f),
            )
        }
    }

    override val alpha: Float get() = 1f
    override val model: ColorModel get() = Ansi16.model

    override fun toRGB(): RGB {
        val color = code % 10

        // grayscale
        if (color == 0 || color == 7) {
            val c: Double =
                if (code > 50) color + 3.5
                else color.toDouble()

            val v = c / 10.5
            return RGB(v, v, v)
        }

        // color
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

    override fun convertToThis(other: Color): Ansi16 = other.toAnsi16()
    override fun components(): FloatArray = floatArrayOf(code.toFloat(), alpha)
    override fun fromComponents(components: FloatArray): Ansi16 = withValidComps(components) {
        Ansi16(it[0].toInt())
    }
}
