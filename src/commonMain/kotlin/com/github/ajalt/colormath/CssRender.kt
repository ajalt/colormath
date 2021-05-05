package com.github.ajalt.colormath

import kotlin.math.roundToInt

enum class RenderCondition {
    /** Always show the value */
    ALWAYS,

    /** Never show the value */
    NEVER,

    /** Only show the value if it differs from its default */
    AUTO
}

enum class AngleUnit {
    /** Degrees with implicit units */
    AUTO,

    /** Degrees with explicit units */
    DEGREES,

    /** 360° == 2π radians */
    RADIANS,

    /** 360° == 400 gradians */
    GRADIANS,

    /** 360° == 1 turn */
    TURNS
}

/**
 * Render this color in CSS `rgb` functional notation.
 *
 * ## Examples
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb()
 * "rgb(255, 0, 128 .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb(commas=false)
 * "rgb(255 0 128 / .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb(rgbStyle=PERCENT)
 * "rgb(100%, 0%, 50%, .5)"
 * ```
 *
 * @param commas If false, arguments will be space-separated. By default, they are comma separated.
 * @param namedRgba If true, use the name `rgba`. By default, use the name `rgb`.
 * @param rgbPercent If true, render the red, green, and blue values as percentages. By default, they're rendered as floats.
 * @param alphaPercent If true, render the alpha as a percentage. By default, its rendered as a float.
 * @param renderAlpha Whether or not to render the alpha value.
 */
fun Color.toCssRgb(
    commas: Boolean = true,
    namedRgba: Boolean = false,
    rgbPercent: Boolean = false,
    alphaPercent: Boolean = false,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
): String {
    val (r, g, b, a) = toRGB()
    val sep = if (commas) ", " else " "
    val args = listOf(r, g, b).joinToString(sep) {
        when (rgbPercent) {
            true -> it.div(255f).render(percent = true)
            false -> it.toString()
        }
    }.withAlpha(a, commas, renderAlpha, alphaPercent)
    val name = if (namedRgba) "rgba" else "rgb"
    return "$name($args)"
}

/**
 * Render this color in CSS `hsl` functional notation.
 *
 * ## Examples
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb()
 * "rgb(255, 0, 128 .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb(commas=false)
 * "rgb(255 0 128 / .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).toCssRgb(rgbStyle=PERCENT)
 * "rgb(100%, 0%, 50%, .5)"
 * ```
 *
 * ```
 * > HSL(180, 50, 25, .5f).toCssHsl(hueUnit=TURNS)
 * "hsl(.5turn, .5, .25, .5)"
 * ```
 *
 * @param commas If false, arguments will be space-separated. By default, they are comma separated.
 * @param namedHsla If true, use the name `hsla`. By default, use the name `hsl`.
 * @param hueUnit The unit to use to render the hue value. Defaults to degree with implicit units.
 * @param alphaPercent If true, render the alpha as a percentage. By default, its rendered as a float.
 * @param renderAlpha Whether or not to render the alpha value.
 */
fun Color.toCssHsl(
    commas: Boolean = true,
    namedHsla: Boolean = false,
    hueUnit: AngleUnit = AngleUnit.AUTO,
    alphaPercent: Boolean = false,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
): String {
    val hsl = toHSL()
    val (h, s, l, a) = hsl
    val sep = if (commas) ", " else " "
    val hue = when (hueUnit) {
        AngleUnit.AUTO -> "$h"
        AngleUnit.DEGREES -> "${h}deg"
        AngleUnit.RADIANS -> "${hsl.hueAsRad().render()}rad"
        AngleUnit.GRADIANS -> "${hsl.hueAsGrad().render()}grad"
        AngleUnit.TURNS -> "${hsl.hueAsTurns().render()}turn"
    }

    val args = listOf(hue, (s / 100f).render(true), (l / 100f).render(true)).joinToString(sep)
        .withAlpha(a, commas, renderAlpha, alphaPercent)
    val name = if (namedHsla) "hsla" else "hsl"
    return "$name($args)"
}

private fun String.withAlpha(a: Float, commas: Boolean, renderAlpha: RenderCondition, alphaPercent: Boolean) = when {
    renderAlpha == RenderCondition.ALWAYS || renderAlpha == RenderCondition.AUTO && a != 1f -> {
        this + (if (commas) ", " else " / ") + a.render(alphaPercent)
    }
    else -> this
}

private fun Float.render(percent: Boolean = false): String = when (percent) {
    true -> "${(this * 100).roundToInt()}%"
    false -> when (this) {
        0f -> "0"
        1f -> "1"
        else -> {
            val str = toString()
            val i = str.indexOf('.')
            if (i < 0) str else str.take(i + 5).trim('0').trimEnd('.')
        }
    }
}


