package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.requireComponentSize
import kotlin.math.floor

/**
 * An ANSI-256 color code
 */
data class Ansi256(val code: Int) : Color {
    init {
        check(code in 0..255) { "code must be in range [0,255]: $code" }
    }

    override val alpha: Float get() = 1f

    override fun toRGB(): RGB {
        // ansi16 colors
        if (code < 16) return toAnsi16().toRGB()

        // grayscale
        if (code >= 232) {
            val c = (code - 232) * 10 + 8
            return RGB(c, c, c)
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
        else -> toRGB().toAnsi16()
    }

    override fun toAnsi256() = this

    override fun convertToThis(other: Color): Ansi256 = other.toAnsi256()
    override fun componentCount(): Int = 2
    override fun components(): FloatArray = floatArrayOf(code.toFloat(), alpha)
    override fun fromComponents(components: FloatArray): Ansi256 {
        requireComponentSize(components)
        return Ansi256(components[0].toInt())
    }
}
