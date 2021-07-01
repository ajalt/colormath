package com.github.ajalt.colormath

import com.github.ajalt.colormath.RenderCondition.AUTO
import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps
import kotlin.math.roundToInt

/**
 * A color in the sRGB color space, which uses the D65 illuminant.
 *
 * | Component  | Description | Gamut    |
 * | ---------- | ----------- | -------- |
 * | [r]        | red         | `[0, 1]` |
 * | [g]        | green       | `[0, 1]` |
 * | [b]        | blue        | `[0, 1]` |
 */
data class RGB(val r: Float, val g: Float, val b: Float, val a: Float = 1f) : Color {
    companion object : ColorModel<RGB> {
        override val name: String get() = "RGB"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("R", false, 0f, 1f),
            ColorComponentInfo("G", false, 0f, 1f),
            ColorComponentInfo("B", false, 0f, 1f),
        )

        override fun convert(color: Color): RGB = color.toRGB()
        override fun create(components: FloatArray): RGB = withValidComps(components) {
            RGB(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }


        @Deprecated("Use RGBInt instead", ReplaceWith("RGBInt(argb.toUInt())"))
        fun fromInt(argb: Int): RGB = RGBInt(argb.toUInt()).toRGB()
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

    @Deprecated("The Byte constructor is deprecated", ReplaceWith("RGB((r + 128), (g + 128), (b + 128))"))
    constructor(r: Byte, g: Byte, b: Byte) : this(r + 128, g + 128, b + 128)

    // A UByte constructor can't be declared since it clashes with the Byte constructor on JVM
    //  constructor(r: UByte, g: UByte, b: UByte) : this(r.toInt(), g.toInt(), b.toInt())

    /**
     * Construct an RGB instance from Int values in the range `[0, 255]`.
     *
     * @property r The red channel, a value typically in the range `[0, 255]`
     * @property g The green channel, a value typically in the range `[0, 255]`
     * @property b The blue channel, a value typically in the range `[0, 255]`
     * @property a The alpha channel, a value in the range `[0f, 1f]`
     */
    constructor(r: Int, g: Int, b: Int, a: Float = 1f) : this(
        r = (r / 255f),
        g = (g / 255f),
        b = (b / 255f),
        a = a
    )

    /**
     * Construct an RGB instance from Double values in the range `[0, 1]`.
     */
    constructor(r: Double, g: Double, b: Double, a: Double = 1.0) : this(
        r = r.toFloat(),
        g = g.toFloat(),
        b = b.toFloat(),
        a = a.toFloat()
    )

    override val alpha: Float get() = a
    override val model: ColorModel<RGB> get() = RGB

    /** The red channel scaled to [0, 255]. HDR colors may exceed this range. */
    val redInt: Int get() = (r * 255).roundToInt()

    /** The green channel scaled to [0, 255]. HDR colors may exceed this range. */
    val greenInt: Int get() = (g * 255).roundToInt()

    /** The blue channel scaled to [0, 255]. HDR colors may exceed this range. */
    val blueInt: Int get() = (b * 255).roundToInt()

    /** The alpha channel scaled to [0, 255]. */
    val alphaInt: Int get() = (a * 255).roundToInt()

    @Deprecated("use toRGBInt instead", ReplaceWith("toRGBInt()"))
    fun toPackedInt(): Int = toRGBInt().argb.toInt()

    /**
     * Return this color as a packed ARGB integer.
     *
     * The color will be clamped to the SDR range `[0, 255]`.
     */
    fun toRGBInt() = RGBInt(
        r = redInt.coerceIn(0, 255).toUByte(),
        g = greenInt.coerceIn(0, 255).toUByte(),
        b = blueInt.coerceIn(0, 255).toUByte(),
        a = alphaInt.coerceIn(0, 255).toUByte()
    )

    /**
     * Convert this color to an RGB hex string.
     *
     * If [renderAlpha] is `ALWAYS`, the [alpha] value will be added e.g. the `aa` in `#ffffffaa`.
     * If it's `NEVER`, the [alpha] will be omitted. If it's `AUTO`, then the [alpha] will be added
     * if it's less than 1.
     *
     * @return A string in the form `"#ffffff"` if [withNumberSign] is true,
     *     or in the form `"ffffff"` otherwise.
     */
    fun toHex(withNumberSign: Boolean = true, renderAlpha: RenderCondition = AUTO): String {
        return toRGBInt().toHex(withNumberSign, renderAlpha)
    }

    override fun toHSL(): HSL {
        return hueMinMaxDelta { h, min, max, delta ->
            val l = (min + max) / 2
            val s = when {
                max == min -> 0.0
                l <= .5 -> delta / (max + min)
                else -> delta / (2 - max - min)
            }
            HSL(h.toFloat(), s.toFloat(), l.toFloat(), alpha)
        }
    }

    override fun toHSV(): HSV {
        return hueMinMaxDelta { h, _, max, delta ->
            val s = when (max) {
                0.0 -> 0.0
                else -> (delta / max)
            }
            HSV(h.toFloat(), s.toFloat(), max.toFloat(), alpha)
        }
    }

    override fun toXYZ(): XYZ = linearRGBToXYZ(sRGBToLinear(r), sRGBToLinear(g), sRGBToLinear(b), alpha)
    override fun toLAB(): LAB = toXYZ().toLAB()
    override fun toLUV(): LUV = toXYZ().toLUV()
    override fun toHCL(): HCL = toXYZ().toLUV().toHCL()

    override fun toCMYK(): CMYK {
        val k = 1 - maxOf(r, b, g)
        val c = if (k == 1f) 0f else (1 - r - k) / (1 - k)
        val m = if (k == 1f) 0f else (1 - g - k) / (1 - k)
        val y = if (k == 1f) 0f else (1 - b - k) / (1 - k)
        return CMYK(c, m, y, k, alpha)
    }

    override fun toHWB(): HWB {
        // https://www.w3.org/TR/css-color-4/#rgb-to-hwb
        return hueMinMaxDelta { hue, min, max, _ ->
            HWB(
                h = hue.toFloat(),
                w = min.toFloat(),
                b = (1.0 - max).toFloat(),
                a = alpha
            )
        }
    }

    override fun toLinearRGB(): LinearRGB {
        return LinearRGB(sRGBToLinear(r), sRGBToLinear(g), sRGBToLinear(b), a)
    }

    override fun toOklab(): Oklab = toLinearRGB().toOklab()

    override fun toAnsi16(): Ansi16 {
        val value = (toHSV().v * 100).roundToInt()
        if (value == 30) return Ansi16(30)
        val v = value / 50

        val ansi = 30 + ((b.roundToInt() * 4) or (g.roundToInt() * 2) or r.roundToInt())
        return Ansi16(if (v == 2) ansi + 60 else ansi)
    }

    override fun toAnsi256(): Ansi256 {
        val ri = redInt
        val gi = greenInt
        val bi = blueInt
        // grayscale
        val code = if (ri == gi && gi == bi) {
            when {
                ri < 8 -> 16
                ri > 248 -> 231
                else -> (((ri - 8) / 247.0) * 24.0).roundToInt() + 232
            }
        } else {
            16 + (36 * (r * 5).roundToInt()) +
                    (6 * (g * 5).roundToInt()) +
                    (b * 5).roundToInt()
        }
        return Ansi256(code)
    }

    override fun toRGB() = this
    override fun components(): FloatArray = floatArrayOf(r, g, b, alpha)

    /**
     * Call [block] with the hue, min of color channels, max of color channels, and the
     * delta between min and max.
     *
     * Min and max are scaled to [0, 1]
     */
    private inline fun <T> hueMinMaxDelta(block: (hue: Double, min: Double, max: Double, delta: Double) -> T): T {
        val r = this.r.toDouble()
        val g = this.g.toDouble()
        val b = this.b.toDouble()
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

        return block(h, min, max, delta)
    }
}


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
