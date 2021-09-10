package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.XYZColorSpaces.XYZ50
import com.github.ajalt.colormath.internal.gradToDeg
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.radToDeg
import com.github.ajalt.colormath.internal.turnToDeg
import kotlin.math.roundToInt

/**
 * Parse a string representing a CSS color value.
 *
 * Custom color spaces with dashed identifiers are not currently supported.
 *
 * @throws IllegalArgumentException if the value cannot be parsed
 */
fun Color.Companion.parse(color: String): Color {
    return parseOrNull(color) ?: throw IllegalArgumentException("Invalid color: $color")
}

/**
 * Parse a string representing a CSS color value, or return null if the string isn't in a recognized
 * format.
 */
fun Color.Companion.parseOrNull(color: String): Color? {
    val keywordColor = CssColors.colorsByName[color]
    return when {
        keywordColor != null -> keywordColor
        color.startsWith("#") -> runCatching { RGB(color) }.getOrNull()
        else -> {
            PATTERNS.RGB_1.matchEntire(color)?.let { rgb(it) }
                ?: PATTERNS.RGB_2.matchEntire(color)?.let { rgb(it) }
                ?: PATTERNS.RGB_3.matchEntire(color)?.let { rgb(it) }
                ?: PATTERNS.RGB_4.matchEntire(color)?.let { rgb(it) }
                ?: PATTERNS.HSL_1.matchEntire(color)?.let { hsl(it) }
                ?: PATTERNS.HSL_2.matchEntire(color)?.let { hsl(it) }
                ?: PATTERNS.LAB.matchEntire(color)?.let { lab(it) }
                ?: PATTERNS.LCH.matchEntire(color)?.let { lch(it) }
                ?: PATTERNS.HWB.matchEntire(color)?.let { hwb(it) }
                ?: PATTERNS.COLOR.matchEntire(color)?.let { color(it) }
        }
    }
}

// https://www.w3.org/TR/css-color-4/#color-syntax
private object PATTERNS {
    private const val NUMBER = """[+-]?(?:\d+|\d*\.\d+)(?:[eE][+-]?\d+)?"""
    private const val PERCENT = "$NUMBER%"
    private const val NUMBER_OR_PERCENT = "$NUMBER%?"
    private const val SLASH_ALPHA = """\s*(?:/\s*($NUMBER_OR_PERCENT))?\s*"""
    private const val COMMA_ALPHA = """(?:\s*,\s*($NUMBER_OR_PERCENT))?\s*"""
    private const val HUE = "$NUMBER(?:deg|grad|rad|turn)?"

    val RGB_1 = Regex("""rgba?\(($PERCENT)\s+($PERCENT)\s+($PERCENT)$SLASH_ALPHA\)""")
    val RGB_2 = Regex("""rgba?\(($PERCENT)\s*,\s*($PERCENT)\s*,\s*($PERCENT)$COMMA_ALPHA\)""")
    val RGB_3 = Regex("""rgba?\(($NUMBER)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val RGB_4 = Regex("""rgba?\(($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)$COMMA_ALPHA\)""")

    val HSL_1 = Regex("""hsla?\(($HUE)\s+($PERCENT)\s+($PERCENT)$SLASH_ALPHA\)""")
    val HSL_2 = Regex("""hsla?\(($HUE)\s*,\s*($PERCENT)\s*,\s*($PERCENT)$COMMA_ALPHA\)""")

    val LAB = Regex("""lab\(($PERCENT)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val LCH = Regex("""lch\(($PERCENT)\s+($NUMBER)\s+($HUE)$SLASH_ALPHA\)""")
    val HWB = Regex("""hwb\(($HUE)\s+($PERCENT)\s+($PERCENT)$SLASH_ALPHA\)""")

    val COLOR = Regex("""color\(([\w\-]+)\s+($NUMBER_OR_PERCENT(?:\s+$NUMBER_OR_PERCENT)*)$SLASH_ALPHA\)""")
}

private fun color(match: MatchResult): Color? {
    val space = when (match.groupValues[1]) {
        "srgb" -> SRGB
        "display-p3" -> DisplayP3
        "a98-rgb" -> AdobeRGB
        "prophoto-rgb" -> ROMM_RGB
        "rec2020" -> BT2020
        "xyz" -> XYZ50
        else -> return null
    }

    val values = match.groupValues[2].split(Regex("\\s+")).map { percentOrNumber(it).clampF() }
    return space.create(floatArrayOf(
        values.getOrElse(0) { 0f },
        values.getOrElse(1) { 0f },
        values.getOrElse(2) { 0f },
        alpha(match.groupValues[3])
    ))
}


private fun rgb(match: MatchResult): Color {
    val r = percentOrNumber(match.groupValues[1])
    val g = percentOrNumber(match.groupValues[2])
    val b = percentOrNumber(match.groupValues[3])
    val a = alpha(match.groupValues[4])

    return if (match.groupValues[1].endsWith("%")) {
        RGB(r.clampF(), g.clampF(), b.clampF(), a)
    } else {
        RGB.from255(r.clampInt(), g.clampInt(), b.clampInt(), a)
    }
}

private fun hsl(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val s = percent(match.groupValues[2])
    val l = percent(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return HSL(h, s.clampF(), l.clampF(), a.clampF())
}

private fun lab(match: MatchResult): Color {
    val l = percent(match.groupValues[1])
    val a = number(match.groupValues[2])
    val b = number(match.groupValues[3])
    val alpha = alpha(match.groupValues[4])
    return LAB(l.coerceAtLeast(0f) * 100f, a, b, alpha)
}

private fun lch(match: MatchResult): Color {
    val l = percent(match.groupValues[1])
    val c = number(match.groupValues[2])
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return LCHab(l.coerceAtLeast(0f) * 100f, c.coerceAtLeast(0f), h, a)
}

private fun hwb(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val w = percent(match.groupValues[2])
    val b = percent(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return HWB(h, w.clampF(), b.clampF(), a)
}

private fun percent(str: String) = str.dropLast(1).toFloat() / 100f
private fun number(str: String) = str.toFloat()
private fun percentOrNumber(str: String) = if (str.endsWith("%")) percent(str) else number(str)
private fun alpha(str: String) = (if (str.isEmpty()) Float.NaN else percentOrNumber(str)).clampF()

/** return degrees in [0, 360] */
private fun hue(str: String): Float {
    val deg = when {
        str.endsWith("deg") -> str.dropLast(3).toFloat()
        str.endsWith("grad") -> str.dropLast(4).toFloat().gradToDeg()
        str.endsWith("rad") -> str.dropLast(3).toFloat().radToDeg()
        str.endsWith("turn") -> str.dropLast(4).toFloat().turnToDeg()
        else -> str.toFloat()
    }
    return deg.normalizeDeg()
}

private fun Float.clampInt(min: Int = 0, max: Int = 255) = roundToInt().coerceIn(min, max)
private fun Float.clampF(min: Float = 0f, max: Float = 1f) = coerceIn(min, max)
