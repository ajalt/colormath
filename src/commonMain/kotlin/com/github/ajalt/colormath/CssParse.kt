package com.github.ajalt.colormath

import kotlin.math.roundToInt

/**
 * Parse a string representing any value from CSS Color Module Level 1 through 4
 *
 * @throws IllegalArgumentException if the value cannot be parsed
 */
fun Color.Companion.fromCss(color: String): Color {
    return Parser(color).parse()
}

private class Parser(str: String) {
    private val str = str.trim()
    private var i = 0

    // https://www.w3.org/TR/css-color-4/#color-syntax
    fun parse(): Color {
        val keywordColor = CssColors.colorsByName[str]
        return when {
            keywordColor != null -> keywordColor
            peek("#") -> hex()
            peek("rgba") -> rgb("rgba")
            peek("rgb") -> rgb("rgb")
            peek("hsla") -> hsl("hsla")
            peek("hsl") -> hsl("hsl")
            peek("hwb") -> throw NotImplementedError("HWB color space is not currently supported")
            peek("lab") -> lab()
            peek("lch") -> lch()
            else -> error()
        }

    }

    // region == colors ==
    private fun hex(): Color = RGB(str)

    private fun rgb(name: String): Color = colorFunction(name) {
        val (r, percents) = numberOrPercent()
        val commas = commaOrWs()

        val (g, p2) = numberOrPercent()
        if (percents != p2) error()
        if (commas != commaOrWs()) error()

        val (b, p3) = numberOrPercent()
        if (percents != p3) error()

        val a = alpha(sep = if (commas) "," else "/")

        if (percents) {
            RGB((r * 255).clampInt(), (g * 255).clampInt(), (b * 255).clampInt(), a.coerceIn(0f, 1f))
        } else {
            RGB(r.clampInt(), g.clampInt(), b.clampInt(), a.clampF())
        }
    }

    private fun hsl(name: String): Color = colorFunction(name) {
        val h = hue()
        val commas = commaOrWs()

        val s = percentage()
        if (commas != commaOrWs()) error()

        val l = percentage()

        val a = alpha(sep = if (commas) "," else "/")
        HSL(h.toFloat(), s.clampF(), l.clampF(), a.clampF())
    }

    private fun lab(): Color = colorFunction("lab") {
        val l = percentage()
        requireWs()
        val a = number()
        requireWs()
        val b = number()
        val alpha = alpha()

        LAB(l.coerceAtLeast(0.0), a, b, alpha)
    }

    private fun lch(): Color = colorFunction("lch") {
        val l = percentage()
        requireWs()
        val c = number()
        requireWs()
        val h = hue()
        val alpha = alpha()

        LCH(l.coerceAtLeast(0.0), c.coerceAtLeast(0.0), h, alpha)
    }

    // endregion
    // region == sub parsers ==

    /** require a comma or whitespace, return true if a comma was encountered */
    private fun commaOrWs(): Boolean {
        val ws = skipWs()
        if (skip(",")) {
            skipWs()
            return true
        }
        if (!ws) error()
        return false
    }

    private inline fun colorFunction(name: String, body: () -> Color): Color {
        require(name)
        require("(")
        skipWs()
        val color = body()
        skipWs()
        require(")")
        return color
    }

    /** return degrees in [-360, 360] */
    private fun hue(): Double {
        val n = number()
        val deg = when {
            skip("deg") -> n
            skip("grad") -> n.gradToDeg()
            skip("rad") -> n.radToDeg()
            skip("turn") -> n.turnToDeg()
            else -> n
        }

        val mod = deg % 360
        return if (mod < 0) mod + 360 else mod
    }

    private fun alpha(sep: String = "/"): Float {
        skipWs()
        return if (skip(sep)) {
            skipWs()
            numberOrPercent().first.clampF()
        } else 1f
    }

    private fun number() = numberOrNull() ?: error()

    private fun numberOrNull(): Double? = tryParse {
        val start = i
        skipAny("+-")
        val intDigits = skipDigits()
        val dot = skip(".")
        val decDigits = skipDigits()

        if (dot && !decDigits) error()

        if (skipAny("eE")) {
            if (!intDigits && !decDigits) error()
            requireDigits()
        }
        return str.slice(start until i).toDouble()
    }

    private fun percentage() = percentageOrNull() ?: error()

    private fun percentageOrNull(): Double? = tryParse {
        val num = numberOrNull() ?: return null
        if (skip("%")) return num / 100
        return null
    }

    private fun numberOrPercent(): Pair<Double, Boolean> {
        val num = numberOrNull() ?: error()
        if (skip("%")) return (num / 100) to true
        return num to false
    }


    // endregion
    // region == utils ==

    private fun peek(needle: String) = str.startsWith(needle, i)

    private fun require(needle: String) {
        if (peek(needle)) i += needle.length
        else error()
    }

    private fun skip(needle: String): Boolean {
        val matches = peek(needle)
        if (matches) i += needle.length
        return matches
    }

    private fun skipAny(chars: String): Boolean {
        val matches = str.getOrNull(i)?.let { it in chars } == true
        if (matches) {
            i += 1
        }
        return matches
    }

    private fun requireWs() {
        if (!skipWs()) error()
    }

    private fun skipWs(): Boolean {
        var matches = false
        while (i < str.length && str[i].isWhitespace()) {
            matches = true
            i += 1
        }
        return matches
    }

    private fun skipDigits(): Boolean {
        var matches = false
        while (i < str.length && str[i].isDigit()) {
            matches = true
            i += 1
        }
        return matches
    }

    private fun requireDigits() {
        if (!skipDigits()) error()
    }

    private fun error(): Nothing {
        throw IllegalArgumentException("Invalid color: $str")
    }

    /** Run [block], and return its result. Reset [i] if the result is `null` */
    private inline fun <R : Any> tryParse(block: () -> R?): R? {
        val start = i
        val res = block()
        if (res == null) {
            i = start
        }
        return res
    }

    private fun Double.clampInt(min: Int = 0, max: Int = 255) = roundToInt().coerceIn(min, max)
    private fun Double.clampF(min: Float = 0f, max: Float = 1f) = toFloat().coerceIn(min, max)
    private fun Float.clampF(min: Float = 0f, max: Float = 1f) = coerceIn(min, max)
    private fun Char.isDigit() = this in "0123456789"
    // endregion
}

