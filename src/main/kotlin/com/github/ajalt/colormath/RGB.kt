package com.github.ajalt.colormath

import com.github.ajalt.colormath.ConvertibleColor.RenderAlpha
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A color in the sRGB color space
 *
 * @property r The red channel, a value in the range `[0, 255]`
 * @property g The green channel, a value in the range `[0, 255]`
 * @property b The blue channel, a value in the range `[0, 255]`
 * @property a The alpha channel, a value in the range `[0f, 1f]`
 */
data class RGB(val r: Int, val g: Int, val b: Int, val a: Float = 1f) : ConvertibleColor {
    companion object {
        /**
         * Create an [RGB] instance from a packed (a)rgb integer, such as those returned from
         * `android.graphics.Color.argb` or `java.awt.image.BufferedImage.getRGB`.
         */
        fun fromInt(argb: Int): RGB = RGB(
                r = (argb ushr 16) and 0xff,
                g = (argb ushr 8) and 0xff,
                b = (argb) and 0xff,
                a = ((argb ushr 24) and 0xff) / 255f)
    }

    init {
        require(r in 0..255) { "r must be in range [0, 255] in $this" }
        require(g in 0..255) { "g must be in range [0, 255] in $this" }
        require(b in 0..255) { "b must be in range [0, 255] in $this" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    /**
     * Construct an RGB instance from a hex string with optional alpha channel.
     *
     * @param hex An rgb hex string in the form "#ffffff" or "ffffff", or an rgba hex string in the
     *   form "#ffffffaa", or "ffffaa"
     */
    constructor(hex: String) : this(
            r = hex.validateHex().parseHex(0),
            g = hex.parseHex(2),
            b = hex.parseHex(4),
            a = if (hex.length < 8) 1f else hex.parseHex(6) / 255f
    )

    /**
     * Construct an RGB instance from [Byte] values.
     *
     * The signed byte values will be translated into the range `[0, 255]`
     */
    constructor(r: Byte, g: Byte, b: Byte) : this(r + 128, g + 128, b + 128)

    /**
     * Construct an RGB instance from Float values in the range `[0, 1]`.
     */
    constructor(r: Float, g: Float, b: Float, a: Float = 1f) : this(
            r = (r * 255).roundToInt(),
            g = (g * 255).roundToInt(),
            b = (b * 255).roundToInt(),
            a = a
    )

    /**
     * Construct an RGB instance from Double values in the range `[0, 1]`.
     */
    constructor(r: Double, g: Double, b: Double, a: Double = 1.0) : this(
            r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat()
    )

    override val alpha: Float get() = a

    override fun toHex(withNumberSign: Boolean, renderAlpha: RenderAlpha): String = buildString(9) {
        if (withNumberSign) append('#')
        append(r.renderHex()).append(g.renderHex()).append(b.renderHex())
        if (renderAlpha == RenderAlpha.ALWAYS || renderAlpha == RenderAlpha.AUTO && a < 1) {
            append((a * 255).roundToInt().renderHex())
        }
    }

    override fun toHSL(): HSL {
        val r = this.r / 255.0
        val g = this.g / 255.0
        val b = this.b / 255.0
        val min = minOf(r, g, b)
        val max = maxOf(r, g, b)
        val delta = max - min
        var h = when {
            max == min -> 0.0
            r == max -> (g - b) / delta
            g == max -> 2 + (b - r) / delta
            b == max -> 4 + (r - g) / delta
            else -> 0.0
        }

        h = minOf(h * 60, 360.0)
        if (h < 0) h += 360
        val l = (min + max) / 2.0
        val s = when {
            max == min -> 0.0
            l <= 0.5 -> delta / (max + min)
            else -> delta / (2 - max - min)
        }

        return HSL(h.roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt(), alpha)
    }

    override fun toHSV(): HSV {
        val r = this.r.toDouble()
        val g = this.g.toDouble()
        val b = this.b.toDouble()
        val min = minOf(r, g, b)
        val max = maxOf(r, g, b)
        val delta = max - min

        val s = when (max) {
            0.0 -> 0.0
            else -> (delta / max * 1000) / 10
        }

        var h = when {
            max == min -> 0.0
            r == max -> (g - b) / delta
            g == max -> 2 + (b - r) / delta
            b == max -> 4 + (r - g) / delta
            else -> 0.0
        }

        h = minOf(h * 60, 360.0)

        if (h < 0) {
            h += 360
        }

        val v = ((max / 255) * 1000) / 10

        return HSV(h.roundToInt(), s.roundToInt(), v.roundToInt(), alpha)
    }

    override fun toXYZ(): XYZ {
        // linearize sRGB
        fun adj(num: Int): Double {
            val c = num.toDouble() / 255.0
            return when {
                c > 0.04045 -> ((c + 0.055) / 1.055).pow(2.4)
                else -> c / 12.92
            }
        }

        val rL = adj(r)
        val gL = adj(g)
        val bL = adj(b)

        val x = 0.4124564 * rL + 0.3575761 * gL + 0.1804375 * bL
        val y = 0.2126729 * rL + 0.7151522 * gL + 0.0721750 * bL
        val z = 0.0193339 * rL + 0.1191920 * gL + 0.9503041 * bL
        return XYZ(x * 100, y * 100, z * 100, alpha)
    }

    override fun toLAB(): LAB = toXYZ().toLAB()

    override fun toCMYK(): CMYK {
        val r = this.r / 255.0
        val b = this.b / 255.0
        val g = this.g / 255.0
        val k = 1 - maxOf(r, b, g)
        val c = if (k == 1.0) 0.0 else (1 - r - k) / (1 - k)
        val m = if (k == 1.0) 0.0 else (1 - g - k) / (1 - k)
        val y = if (k == 1.0) 0.0 else (1 - b - k) / (1 - k)
        return CMYK((c * 100).roundToInt(), (m * 100).roundToInt(), (y * 100).roundToInt(), (k * 100).roundToInt(), alpha)
    }

    override fun toAnsi16(): Ansi16 = toAnsi16(toHSV().v)

    private fun toAnsi16(value: Int): Ansi16 {
        if (value == 30) return Ansi16(30)
        val v = (value / 50.0).roundToInt()

        val ansi = 30 +
                ((b / 255.0).roundToInt() * 4
                        or ((g / 255.0).roundToInt() * 2)
                        or (r / 255.0).roundToInt())
        return Ansi16(if (v == 2) ansi + 60 else ansi)
    }

    override fun toAnsi256(): Ansi256 {
        // grayscale
        val code = if (r == g && g == b) {
            when {
                r < 8 -> 16
                r > 248 -> 231
                else -> (((r - 8) / 247.0) * 24.0).roundToInt() + 232
            }
        } else {
            16 + (36 * (r / 255.0 * 5).roundToInt()) +
                    (6 * (g / 255.0 * 5).roundToInt()) +
                    (b / 255.0 * 5).roundToInt()
        }
        return Ansi256(code)
    }

    override fun toRGB() = this
}

private fun Int.renderHex() = toString(16).padStart(2, '0')
private fun String.validateHex() = apply {
    require(if (startsWith('#')) length == 7 || length == 9 else length == 6 || length == 8) {
        "Hex string must be in the format \"#ffffff\" or \"ffffff\""
    }
}

private fun String.parseHex(startIndex: Int): Int {
    val i = if (this[0] == '#') startIndex + 1 else startIndex
    return slice(i..i + 1).toInt(16)
}
