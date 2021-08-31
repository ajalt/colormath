package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps
import kotlin.math.floor

/**
 * An ANSI-256 color code
 *
 * Unlike [Ansi16], these codes don't have separate values for foreground and background.
 *
 * ## Valid codes
 *
 * - `0-7`: Standard colors corresponding to [Ansi16] codes `30-37`
 * - `8-15`: Bright colors corresponding to [Ansi16] codes `90-97`
 * - `16-231`: 216 colors encoded in a 6×6×6 cube
 * - `232-255`: Grayscale colors
 */
data class Ansi256(val code: Int) : Color {
    /** Default constructors for the [Ansi256] color model. */
    companion object : ColorSpace<Ansi256> {
        override val name: String get() = "Ansi256"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("code", false),
        )

        override fun convert(color: Color): Ansi256 = color.toAnsi256()
        override fun create(components: FloatArray): Ansi256 = withValidComps(components) {
            Ansi256(it[0].toInt())
        }
    }


    override val alpha: Float get() = Float.NaN
    override val space: ColorSpace<Ansi256> get() = Ansi256

    override fun toSRGB(): RGB {
        // ansi16 colors
        if (code < 16) return toAnsi16().toSRGB()

        // grayscale
        if (code >= 232) {
            val c = (code - 232) * 10 + 8
            return RGB.from255(c, c, c)
        }

        // color
        val c = code - 16
        val rem = c % 36
        val r = floor(c / 36.0) / 5.0
        val g = floor(rem / 6.0) / 5.0
        val b = (rem % 6) / 5.0
        return RGB(r, g, b)
    }

    // 0-7 are standard ansi16 colors
    // 8-15 are bright ansi16 colors
    override fun toAnsi16() = when {
        code < 8 -> Ansi16(code + 30)
        code < 16 -> Ansi16(code - 8 + 90)
        else -> toSRGB().toAnsi16()
    }

    override fun toAnsi256() = this
    override fun toArray(): FloatArray = floatArrayOf(code.toFloat(), alpha)
}
