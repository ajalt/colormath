package com.github.ajalt.colormath

import kotlin.math.roundToInt

/**
 * Parse a string representing any value from CSS Color Module Level 1 through 4
 *
 * @throws IllegalArgumentException if the value cannot be parsed
 */
fun Color.Companion.fromCss(color: String): Color {
    val keywordColor = CssColors.colorsByName[color]
    return when {
        keywordColor != null -> keywordColor
        color.startsWith("#") -> RGB(color)
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
                ?: throw IllegalArgumentException("Invalid color: $color")
        }
    }
}

// https://www.w3.org/TR/css-color-4/#color-syntax
private object PATTERNS {
    private const val NUMBER = """[+-]?(?:\d+|\d*\.\d+)(?:[eE][+-]?\d+)?"""
    private const val PERCENT = "$NUMBER%"
    private const val ALPHA = "$NUMBER%?"
    private const val HUE = "$NUMBER(?:deg|grad|rad|turn)?"

    val RGB_1 = Regex("""rgba?\(($PERCENT)\s+($PERCENT)\s+($PERCENT)\s*(?:/\s*($ALPHA))?\s*\)""")
    val RGB_2 = Regex("""rgba?\(($PERCENT)\s*,\s*($PERCENT)\s*,\s*($PERCENT)(?:\s*,\s*($ALPHA))?\s*\)""")
    val RGB_3 = Regex("""rgba?\(($NUMBER)\s+($NUMBER)\s+($NUMBER)\s*(?:/\s*($ALPHA)\s*)?\)""")
    val RGB_4 = Regex("""rgba?\(($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)(?:\s*,\s*($ALPHA))?\s*\)""")

    val HSL_1 = Regex("""hsla?\(($HUE)\s+($PERCENT)\s+($PERCENT)\s*(?:/\s*($ALPHA))?\s*\)""")
    val HSL_2 = Regex("""hsla?\(($HUE)\s*,\s*($PERCENT)\s*,\s*($PERCENT)(?:\s*,\s*($ALPHA))?\s*\)""")

    val LAB = Regex("""lab\(($PERCENT)\s+($NUMBER)\s+($NUMBER)\s*(?:/\s*($ALPHA))?\s*\)""")
    val LCH = Regex("""lch\(($PERCENT)\s+($NUMBER)\s+($HUE)\s*(?:/\s*($ALPHA))?\s*\)""")
    val HWB = Regex("""hwb\(($HUE)\s+($PERCENT)\s+($PERCENT)\s*(?:/\s*($ALPHA))?\s*\)""")
}


private fun rgb(match: MatchResult): Color {
    val r = percentOrNumber(match.groupValues[1])
    val g = percentOrNumber(match.groupValues[2])
    val b = percentOrNumber(match.groupValues[3])
    val a = alpha(match.groupValues[4])

    return if (match.groupValues[1].endsWith("%")) {
        RGB(r.clampF(), g.clampF(), b.clampF(), a)
    } else {
        RGB(r.clampInt(), g.clampInt(), b.clampInt(), a)
    }
}

fun hsl(match: MatchResult): Color {
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
    return LAB(l.coerceAtLeast(0f) * 100.0, a.toDouble(), b.toDouble(), alpha)
}

private fun lch(match: MatchResult): Color {
    val l = percent(match.groupValues[1])
    val c = number(match.groupValues[2])
    val h = hue(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return LCH(100 * l.coerceAtLeast(0f), c.coerceAtLeast(0f), h, a)
}

private fun hwb(match: MatchResult): Color {
    val h = hue(match.groupValues[1])
    val w = percent(match.groupValues[2])
    val b = percent(match.groupValues[3])
    val a = alpha(match.groupValues[4])
    return HWB(h, 100 * w.clampF(), 100 * b.clampF(), a)
}

private fun percent(str: String) = str.dropLast(1).toFloat() / 100
private fun number(str: String) = str.toFloat()
private fun percentOrNumber(str: String) = if (str.endsWith("%")) percent(str) else number(str)
private fun alpha(str: String) = (if (str.isEmpty()) 1f else percentOrNumber(str).toFloat()).clampF()

/** return degrees in [-360, 360] */
private fun hue(str: String): Float {
    val deg = when {
        str.endsWith("deg") -> str.dropLast(3).toFloat()
        str.endsWith("grad") -> str.dropLast(4).toFloat().gradToDeg()
        str.endsWith("rad") -> str.dropLast(3).toFloat().radToDeg()
        str.endsWith("turn") -> str.dropLast(4).toFloat().turnToDeg()
        else -> str.toFloat()
    }
    val mod = deg % 360
    return if (mod < 0) mod + 360 else mod
}

private fun Float.clampInt(min: Int = 0, max: Int = 255) = roundToInt().coerceIn(min, max)
private fun Float.clampF(min: Float = 0f, max: Float = 1f) = coerceIn(min, max)
