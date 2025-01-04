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

/**
 * Parse a string representing a CSS color value.
 *
 * @param color The CSS color string to parse
 * @param customColorSpaces A list of custom color spaces to recognize in the `color()` function.
 * Each pair should be the identifier of the color and its [ColorSpace].
 * @throws IllegalArgumentException if the value cannot be parsed
 */
@JvmOverloads // TODO(4.0) remove this
fun Color.Companion.parse(
    color: String,
    customColorSpaces: Map<String, ColorSpace<*>> = emptyMap(),
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
    customColorSpaces: Map<String, ColorSpace<*>> = emptyMap(),
): Color? {
    val keywordColor = CssColors.colorsByName[color]
    return when {
        keywordColor != null -> keywordColor
        color.startsWith("#") -> runCatching { RGB(color) }.getOrNull()
        else -> {
            PATTERNS.RGB_1.matchEntire(color)?.let { rgb(it) }
                ?: PATTERNS.RGB_2.matchEntire(color)?.let { rgb(it) }
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
@Suppress("RegExpUnnecessaryNonCapturingGroup")
private object PATTERNS {
    private const val FLOAT = """[+-]?(?:\d+|\d*\.\d+)(?:[eE][+-]?\d+)?"""
    private const val NUMBER = "(?:none|$FLOAT%?)"
    private const val ALPHA = "$FLOAT%?"
    private const val SLASH_ALPHA = """\s*(?:/\s*($ALPHA))?\s*"""
    private const val COMMA_ALPHA = """\s*(?:,\s*($ALPHA))?\s*"""
    private const val HUE = "(?:none|$FLOAT(?:deg|grad|rad|turn)?)"

    val RGB_1 = Regex("""rgba?\(($NUMBER)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val RGB_2 = Regex("""rgba?\(($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)$COMMA_ALPHA\)""")

    val HSL_1 = Regex("""hsla?\(($HUE)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val HSL_2 = Regex("""hsla?\(($HUE)\s*,\s*($NUMBER)\s*,\s*($NUMBER)$COMMA_ALPHA\)""")

    val LAB = Regex("""lab\(($NUMBER)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val LCH = Regex("""lch\(($NUMBER)\s+($NUMBER)\s+($HUE)$SLASH_ALPHA\)""")
    val HWB = Regex("""hwb\(($HUE)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")

    val OKLAB = Regex("""oklab\(($NUMBER)\s+($NUMBER)\s+($NUMBER)$SLASH_ALPHA\)""")
    val OKLCH = Regex("""oklch\(($NUMBER)\s+($NUMBER)\s+($HUE)$SLASH_ALPHA\)""")

    val COLOR = Regex("""color\(([\w\-]+)\s+($NUMBER(?:\s+$NUMBER)*)$SLASH_ALPHA\)""")
}

private fun color(
    match: MatchResult,
    customColorSpaces: Map<String, ColorSpace<*>>,
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
        else -> customColorSpaces.entries.firstOrNull { it.key == name }?.value
    } ?: return null

    val values = match.groupValues[2].split(Regex("\\s+")).mapIndexed { i, str ->
        percentOrNumber(str, space.components[i].max)
    }
    val components = FloatArray(space.components.size) { values.getOrElse(it) { 0f } }
    components[components.lastIndex] = alpha(match.groupValues[3])
    return space.create(components)
}

private fun rgb(match: MatchResult): Color {
    val r = percentOrNumber(match.groupValues[1], RGB.components[0].max)
    val g = percentOrNumber(match.groupValues[2], RGB.components[1].max)
    val b = percentOrNumber(match.groupValues[3], RGB.components[2].max)
    val a = alpha(match.groupValues[4])

    return if (match.groupValues[1].endsWith("%")) {
        RGB(r, g, b, a)
    } else {
        RGB(r / 255f, g / 255f, b / 255f, a)
    }
}

private fun hsl(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val s = percentOrNumber(match.groupValues[2], HSL.components[1].max)
    val l = percentOrNumber(match.groupValues[3], HSL.components[2].max)
    val a = alpha(match.groupValues[4])
    return HSL(h, s, l, a)
}

private fun lab(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1], LAB.components[0].max)
    val a = percentOrNumber(match.groupValues[2], LAB.components[1].max)
    val b = percentOrNumber(match.groupValues[3], LAB.components[2].max)
    val alpha = alpha(match.groupValues[4])
    return LAB50(l, a, b, alpha)
}

private fun lch(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1], LCHab.components[0].max)
    val c = percentOrNumber(match.groupValues[2], LCHab.components[1].max)
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return LCHab50(l, c, h, a)
}

private fun hwb(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val w = percentOrNumber(match.groupValues[2], HWB.components[1].max)
    val b = percentOrNumber(match.groupValues[3], HWB.components[2].max)
    val a = alpha(match.groupValues[4])
    return HWB(h, w, b, a)
}


private fun oklab(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1], Oklab.components[0].max)
    val a = percentOrNumber(match.groupValues[2], Oklab.components[1].max)
    val b = percentOrNumber(match.groupValues[3], Oklab.components[2].max)
    val alpha = alpha(match.groupValues[4])
    return Oklab(l, a, b, alpha)

}

private fun oklch(match: MatchResult): Color {
    val l = percentOrNumber(match.groupValues[1], Oklch.components[0].max)
    val c = percentOrNumber(match.groupValues[2], Oklch.components[1].max)
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return Oklch(l, c, h, a)
}


// CSS uses the "none" keyword for NaN https://www.w3.org/TR/css-color-4/#missing
private fun number(str: String) = if (str == "none") Float.NaN else str.toFloat()
private fun percentOrNumber(str: String, max: Float): Float {
    return when {
        str.endsWith("%") -> str.dropLast(1).toFloat() * max / 100f
        else -> number(str)
    }
}

private fun alpha(str: String): Float {
    return (if (str.isEmpty()) 1f else percentOrNumber(str, 1f)).coerceIn(0f, 1f)
}

/** return degrees in [0, 360] */
private fun hue(str: String): Float {
    return when {
        str.endsWith("deg") -> str.dropLast(3).toFloat()
        str.endsWith("grad") -> str.dropLast(4).toFloat().gradToDeg()
        str.endsWith("rad") -> str.dropLast(3).toFloat().radToDeg()
        str.endsWith("turn") -> str.dropLast(4).toFloat().turnToDeg()
        else -> number(str)
    }.normalizeDeg()
}
