package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ65
import kotlin.jvm.JvmOverloads
import kotlin.math.roundToInt

/**
 * Parse a string representing a CSS color value.
 *
 * Custom color spaces with dashed identifiers are not currently supported.
 *
 * @param color The CSS color string to parse
 * @param customColorSpaces A list of custom color spaces to recognize in the `color()` function.
 * Each pair should be the identifier of the color and its [ColorSpace].
 * @throws IllegalArgumentException if the value cannot be parsed
 */
@JvmOverloads // TODO(4.0) remove this
fun Color.Companion.parse(
    color: String,
    customColorSpaces: List<Pair<String, ColorSpace<*>>> = emptyList(),
): Color {
    return parseOrNull(color, customColorSpaces)
        ?: throw IllegalArgumentException("Invalid color: $color")
}

/**
 * Parse a string representing a CSS color value, or return null if the string isn't in a recognized
 * format.
 *
 * @param color The color string to parse
 * @param customColorSpaces A list of custom color spaces to recognize in the `color()` function.
 * Each pair should be the identifier of the color and its [ColorSpace].
 */
@JvmOverloads // TODO(4.0) remove this
fun Color.Companion.parseOrNull(
    color: String,
    customColorSpaces: List<Pair<String, ColorSpace<*>>> = emptyList(),
): Color? {
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
                ?: PATTERNS.OKLAB.matchEntire(color)?.let { oklab(it) }
                ?: PATTERNS.OKLCH.matchEntire(color)?.let { oklch(it) }
                ?: PATTERNS.COLOR.matchEntire(color)?.let { color(it, customColorSpaces) }
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

    val OKLAB = Regex("""oklab\(($NUMBER_OR_PERCENT)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val OKLCH = Regex("""oklch\(($NUMBER_OR_PERCENT)\s+($NUMBER)\s+($HUE)$SLASH_ALPHA\)""")

    val COLOR = Regex(
        """color\(([\w\-]+)\s+($NUMBER_OR_PERCENT(?:\s+$NUMBER_OR_PERCENT)*)$SLASH_ALPHA\)"""
    )
}

private fun color(
    match: MatchResult,
    customColorSpaces: List<Pair<String, ColorSpace<*>>>,
): Color? {
    val space = when (val name = match.groupValues[1]) {
        "srgb" -> SRGB
        "srgb-linear" -> LinearSRGB
        "display-p3" -> DisplayP3
        "a98-rgb" -> AdobeRGB
        "prophoto-rgb" -> ROMM_RGB
        "rec2020" -> BT2020
        "xyz", "xyz-d50" -> XYZ50
        "xyz-d65" -> XYZ65
        else -> customColorSpaces.firstOrNull { it.first == name }?.second
    } ?: return null

    val values = match.groupValues[2].split(Regex("\\s+")).map { percentOrNumber(it).clampF() }
    val components = FloatArray(space.components.size) { values.getOrElse(it) { 0f } }
    components[components.lastIndex] = alpha(match.groupValues[3])
    return space.create(components)
}


private fun rgb(match: MatchResult): Color {
    val r = percentOrNumber(match.groupValues[1])
    val g = percentOrNumber(match.groupValues[2])
    val b = percentOrNumber(match.groupValues[3])
    val a = alpha(match.groupValues[4])

    return if (match.groupValues[1].endsWith("%")) {
        RGB(r.clampF(), g.clampF(), b.clampF(), a)
    } else {
        RGB(r.clampInt() / 255f, g.clampInt() / 255f, b.clampInt() / 255f, a)
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
    return LAB50(l.coerceAtLeast(0f) * 100f, a, b, alpha)
}

private fun lch(match: MatchResult): Color {
    val l = percent(match.groupValues[1])
    val c = number(match.groupValues[2])
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return LCHab50(l.coerceAtLeast(0f) * 100f, c.coerceAtLeast(0f), h, a)
}

private fun hwb(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val w = percent(match.groupValues[2])
    val b = percent(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return HWB(h, w.clampF(), b.clampF(), a)
}


private fun oklab(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1])
    val a = number(match.groupValues[2])
    val b = number(match.groupValues[3])
    val alpha = alpha(match.groupValues[4])
    return Oklab(l, a, b, alpha)

}

private fun oklch(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1])
    val c = number(match.groupValues[2])
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return Oklch(l, c, h, a)
}


private fun percent(str: String) = str.dropLast(1).toFloat() / 100f
private fun number(str: String) = str.toFloat()
private fun percentOrNumber(str: String) = if (str.endsWith("%")) percent(str) else number(str)
private fun alpha(str: String) = (if (str.isEmpty()) 1f else percentOrNumber(str)).clampF()

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
