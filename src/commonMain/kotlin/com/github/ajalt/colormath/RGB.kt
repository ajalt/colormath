package com.github.ajalt.colormath

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A color in the sRGB color space, which uses the D65 illuminant.
 *
 * @property r The red channel, a value in the range `[0, 255]`
 * @property g The green channel, a value in the range `[0, 255]`
 * @property b The blue channel, a value in the range `[0, 255]`
 * @property a The alpha channel, a value in the range `[0f, 1f]`
 */
data class RGB(val r: Int, val g: Int, val b: Int, val a: Float = 1f) : Color {
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
     * [hex] may optionally start with a `#`. The remaining characters should be one of the following forms:
     *
     * - `ddeeff`: The RGB values specified in pairs of hex digits
     * - `ddeeffaa`: Like the 6 digit form, but with an extra pair of hex digits for specifying the alpha channel
     * - `def`: A shorter version of the 6 digit form. Each digit is repeated, so `def` is equivalent to `ddeeff`
     * - `defa`: A shorter version of the 8 digit for.Each digit is repeated, so `defa` is equivalent to `ddeeffaa`
     */
    constructor(hex: String) : this(
        r = hex.validateHex().parseHex(0),
        g = hex.parseHex(1),
        b = hex.parseHex(2),
        a = if (hex.hexLength.let { it == 4 || it == 8 }) hex.parseHex(3) / 255f else 1f
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
        r = (r * 255).roundToInt(),
        g = (g * 255).roundToInt(),
        b = (b * 255).roundToInt(),
        a = a.toFloat()
    )

    override val alpha: Float get() = a

    /**
     * Return this color as a packed ARGB integer, such as those returned from
     * `android.graphics.Color.argb` or `java.awt.image.BufferedImage.getRGB`.
     */
    fun toPackedInt(): Int {
        return (a * 0xff).roundToInt() shl 24 or (r shl 16) or (g shl 8) or b
    }

    override fun toHex(withNumberSign: Boolean, renderAlpha: RenderCondition): String = buildString(9) {
        if (withNumberSign) append('#')
        append(r.renderHex()).append(g.renderHex()).append(b.renderHex())
        if (renderAlpha == RenderCondition.ALWAYS || renderAlpha == RenderCondition.AUTO && a < 1) {
            append((a * 255).roundToInt().renderHex())
        }
    }

    override fun toHSL(): HSL {
        val (h, min, max, delta) = hueMinMaxDelta()
        val l = (min + max) / 2
        val s = when {
            max == min -> 0.0
            l <= .5 -> delta / (max + min)
            else -> delta / (2 - max - min)
        }
        return HSL(h.roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt(), alpha)
    }

    override fun toHSV(): HSV {
        val (h, _, max, delta) = hueMinMaxDelta()
        val s = when (max) {
            0.0 -> 0.0
            else -> (delta / max)
        }
        return HSV(h.roundToInt(), (s * 100).roundToInt(), (max * 100).roundToInt(), alpha)
    }

    override fun toXYZ(): XYZ {
        // linearize sRGB
        fun adj(num: Int): Double {
            val c = num / 255.0
            return when {
                c > 0.04045 -> ((c + 0.055) / 1.055).pow(2.4)
                else -> c / 12.92
            }
        }

        val rL = adj(r)
        val gL = adj(g)
        val bL = adj(b)

        // Matrix from http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
        val x = 0.4124564 * rL + 0.3575761 * gL + 0.1804375 * bL
        val y = 0.2126729 * rL + 0.7151522 * gL + 0.0721750 * bL
        val z = 0.0193339 * rL + 0.1191920 * gL + 0.9503041 * bL
        return XYZ(x * 100, y * 100, z * 100, alpha)
    }

    override fun toLAB(): LAB = toXYZ().toLAB()

    override fun toLUV(): LUV = toXYZ().toLUV()

    override fun toLCH(): LCH = toXYZ().toLUV().toLCH()

    override fun toCMYK(): CMYK {
        val r = this.r / 255.0
        val b = this.b / 255.0
        val g = this.g / 255.0
        val k = 1 - maxOf(r, b, g)
        val c = if (k == 1.0) 0.0 else (1 - r - k) / (1 - k)
        val m = if (k == 1.0) 0.0 else (1 - g - k) / (1 - k)
        val y = if (k == 1.0) 0.0 else (1 - b - k) / (1 - k)
        return CMYK(
            (c * 100).roundToInt(),
            (m * 100).roundToInt(),
            (y * 100).roundToInt(),
            (k * 100).roundToInt(),
            alpha
        )
    }

    override fun toHWB(): HWB {
        // https://www.w3.org/TR/css-color-4/#rgb-to-hwb
        val (hue, min, max) = hueMinMaxDelta()
        return HWB(
            h = hue,
            w = 100 * min,
            b = 100 * (1 - max),
            alpha = alpha.toDouble()
        )
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

    /**
     * Return an array containing the hue, min of color channels, max of color channels, and the
     * delta between min and max.
     *
     * Min and max are scaled to [0, 1]
     */
    private fun hueMinMaxDelta(): DoubleArray {
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

        return doubleArrayOf(h, min, max, delta)
    }
}

private fun Int.renderHex() = toString(16).padStart(2, '0')

private fun String.validateHex() = apply {
    require(hexLength.let { it == 3 || it == 4 || it == 6 || it == 8 }) {
        "Hex string must be in the format \"#ffffff\" or \"ffffff\""
    }
}

private fun String.parseHex(startIndex: Int): Int {
    return if (hexLength > 4) {
        val i = if (this[0] == '#') startIndex * 2 + 1 else startIndex * 2
        slice(i..i + 1).toInt(16)
    } else {
        val i = if (this[0] == '#') startIndex + 1 else startIndex
        get(i).let { "$it$it" }.toInt(16)
    }
}

private val String.hexLength get() = if (startsWith("#")) length - 1 else length
