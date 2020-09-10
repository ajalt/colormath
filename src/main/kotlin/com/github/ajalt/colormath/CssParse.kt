package com.github.ajalt.colormath

import kotlin.math.PI

/**
 * Parse a string representing any value from CSS Color Module Level 1 through 4
 *
 * @throws IllegalArgumentException if the value cannot be parsed
 */
fun Color.Companion.fromCss(color: String): Color {
    val trimmed = color.trim()
    try {
        val keywordColor = CssColors.colorsByName[trimmed]
        return when {
            keywordColor != null -> keywordColor
            trimmed.startsWith("#") -> parseHex(trimmed)
            trimmed.startsWith("rgb") -> parseRgb(trimmed)
            trimmed.startsWith("hsl") -> parseHsl(trimmed)
            else -> throw IllegalArgumentException("Invalid color: $color")
        }
    } catch (e: NumberFormatException) {
        val origMsg = e.message
        // Throw a more helpful message
        val msg = when {
            origMsg == null -> "Invalid color: $color"
            origMsg.startsWith("For input string") -> "Invalid number: ${origMsg.drop("For input string: ".length)}"
            else -> origMsg
        }
        throw IllegalArgumentException(msg)
    }
}

private val funcSyntax = run {
    val group = """([^,)\s]+)"""
    val sep = """(\s*,\s*|\s+)"""
    val slashSep = """(\s*,\s*|\s*\/\s*)"""
    Regex("""(?:rgb|hsl)a?\(\s*$group$sep$group$sep$group(?:$slashSep$group)?\s*\)""")
}

private fun parseHsl(color: String): HSL {
    val (vals, alpha) = extractArgs(color, validatePercents = false)
    return HSL(
            h = vals[0].parse(isAngle = true),
            s = vals[1].parse(requirePercent = true),
            l = vals[2].parse(requirePercent = true),
            a = alpha
    )
}

private fun parseRgb(color: String): RGB {
    val (vals, alpha) = extractArgs(color, validatePercents = true)
    return RGB(
            r = vals[0].parse(divBy255 = true),
            g = vals[1].parse(divBy255 = true),
            b = vals[2].parse(divBy255 = true),
            a = alpha
    )
}

private fun parseHex(hex: String): Color {
    return when (hex.length) {
        4, 5 -> RGB(hex.map { "$it$it" }.joinToString("").drop(1))
        else -> RGB(hex)
    }
}

private fun extractArgs(color: String, validatePercents: Boolean): Pair<List<String>, Float> {
    val groups = funcSyntax.matchEntire(color)?.groupValues
            ?: throw IllegalArgumentException("Invalid format: $color")
    val vals = listOf(groups[1], groups[3], groups[5])
    val alphaVal = groups[7]
    val seps = listOf(groups[2], groups[4], groups[6])

    val commaCount = seps.count { ',' in it }
    require(commaCount == 0 || commaCount == 3 || commaCount == 2 && alphaVal.isEmpty()) {
        "Invalid format, separator mismatch: $color"
    }

    if (validatePercents) {
        val percentsCount = vals.count { it.endsWith('%') }
        if (percentsCount in 1..2) {
            throw IllegalArgumentException("Invalid format, percent and numbers can't be mixed: $color")
        }
    }
    val alpha = if (alphaVal.isEmpty()) 1f else alphaVal.parse()
    return vals to alpha
}

private fun String.parse(
        divBy255: Boolean = false,
        requirePercent: Boolean = false,
        isAngle: Boolean = false
): Float {
    fun normAngle(deg: Float): Float {
        require(isAngle) { "Invalid use of angle: $this" }
        val fl = deg % 360
        return if (fl < 0) fl + 360 else fl
    }
    require(isAngle || !startsWith('-')) { "Invalid negative number: $this" }
    require(!requirePercent || endsWith('%')) { "Invalid percentage: $this" }
    require(!isAngle || !endsWith('%')) { "Invalid angle: $this" }

    return when {
        endsWith("deg") -> normAngle(withoutUnit(3))
        endsWith("grad") -> normAngle((withoutUnit(4) * 9 / 10))
        endsWith("rad") -> normAngle((withoutUnit(3) * 180 / PI).toFloat())
        endsWith("turn") -> normAngle((withoutUnit(4) * 360))
        endsWith('%') -> withoutUnit(1) / 100
        isAngle -> normAngle(toFloat())
        divBy255 -> toFloat() / 255
        else -> toFloat()
    }
}

private fun String.withoutUnit(count: Int): Float {
    val num = dropLast(count)
    require(num.lastOrNull()?.isDigit() == true) { "Invalid number: $this" }
    return num.toFloat()
}
