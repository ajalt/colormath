package com.github.ajalt.colormath

import com.github.ajalt.colormath.LABColorSpaces.LAB50
import com.github.ajalt.colormath.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.XYZColorSpaces.XYZ50
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
 * Render this color in CSS functional notation.
 *
 * If CSS defines a color function syntax for this color's model (e.g. `lab()`), it will be used.
 * Otherwise, the `color()` syntax will be used. For color spaces not predefined by CSS, a dashed
 * identifier based on the [space's][ColorSpace.name] will be used.
 *
 * Note that for [XYZ], [LAB], and [LCHab], the CSS standard requires that the [D50][Illuminant.D50]
 * white point be used, colors using other white points will be adapted to D50 before being
 * serialized. Those color models default to [D65][Illuminant.D65], so you should use [XYZ50],
 * [LAB50], and [LCHab50], respectively, if you're specifying a color to be serialized.
 *
 * ## Examples
 * ```
 * > RGB(255, 0, 128, .5f).formatCssString()
 * "rgb(255 0 128 / .5)"
 * ```
 *
 * ```
 * > ROMM_RGB(0.1, 0.2, 0.3).formatCssString()
 * "color(prophoto-rgb 0.1 0.2 0.3)"
 * ```
 *
 * ```
 * > JzAzBz(0.1, 0.2, 0.3).formatCssString()
 * "color(--jzazbz 0.1 0.2 0.3)"
 * ```
 *
 * @param hueUnit The unit to use to render hue values, if this color has any.
 * @param alphaPercent If true, render the alpha as a percentage. By default, it's rendered as a float.
 * @param renderAlpha Whether or not to render the alpha value.
 */
fun Color.formatCssString(
    hueUnit: AngleUnit = AngleUnit.AUTO,
    alphaPercent: Boolean = false,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
): String {
    return when (this) {
        is RGB -> when (space) {
            SRGB -> formatCssRgb(alphaPercent = alphaPercent, renderAlpha = renderAlpha)
            RGBColorSpaces.DISPLAY_P3 -> renderColorFunction("display-p3", alphaPercent, renderAlpha)
            RGBColorSpaces.ADOBE_RGB -> renderColorFunction("a98-rgb", alphaPercent, renderAlpha)
            RGBColorSpaces.ROMM_RGB -> renderColorFunction("prophoto-rgb", alphaPercent, renderAlpha)
            RGBColorSpaces.BT_2020 -> renderColorFunction("rec2020", alphaPercent, renderAlpha)
            else -> renderColorFunction(dashName, alphaPercent, renderAlpha)
        }
        is HSL -> formatCssHsl(hueUnit = hueUnit, alphaPercent = alphaPercent, renderAlpha = renderAlpha)
        is LAB -> convertTo(LAB50).renderLab(alphaPercent, renderAlpha)
        is LCHab -> convertTo(LCHab50).renderLCH(hueUnit, alphaPercent, renderAlpha)
        is HWB -> renderHWB(hueUnit, alphaPercent, renderAlpha)
        is XYZ -> adaptTo(XYZ50).renderColorFunction("xyz", alphaPercent, renderAlpha)
        else -> renderColorFunction(dashName, alphaPercent, renderAlpha)
    }
}

/**
 * Render this color in CSS `rgb` functional notation.
 *
 * ## Examples
 * ```
 * > RGB(255, 0, 128, .5f).formatCssRgb()
 * "rgb(255 0 128 / .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).formatCssRgb(commas=true)
 * "rgb(255, 0, 128 .5)"
 * ```
 *
 * ```
 * > RGB(255, 0, 128, .5f).formatCssRgb(rgbStyle=PERCENT)
 * "rgb(100% 0% 50% / .5)"
 * ```
 *
 * @param commas If false, arguments will be space-separated. By default, they are comma separated.
 * @param namedRgba If true, use the name `rgba`. By default, use the name `rgb`.
 * @param rgbPercent If true, render the red, green, and blue values as percentages. By default, they're rendered as floats.
 * @param alphaPercent If true, render the alpha as a percentage. By default, it's rendered as a float.
 * @param renderAlpha Whether or not to render the alpha value.
 */
fun Color.formatCssRgb(
    commas: Boolean = false,
    namedRgba: Boolean = false,
    rgbPercent: Boolean = false,
    alphaPercent: Boolean = false,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
): String {
    val rgb = toSRGB()
    val int = rgb.toRGBInt()
    return rgb.renderFunction(
        if (namedRgba) "rgba" else "rgb",
        if (rgbPercent) rgb.r.render(true) else int.r.toString(),
        if (rgbPercent) rgb.g.render(true) else int.g.toString(),
        if (rgbPercent) rgb.b.render(true) else int.b.toString(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas,
    )
}

/**
 * Render this color in CSS `hsl` functional notation.
 *
 * ## Examples
 * ```
 * > HSL(180, 50, 25, .5f).formatCssHsl(hueUnit=TURNS)
 * "hsl(.5turn, .5, .25, .5)"
 * ```
 *
 * @param commas If false, arguments will be space-separated. By default, they are comma separated.
 * @param namedHsla If true, use the name `hsla`. By default, use the name `hsl`.
 * @param hueUnit The unit to use to render the hue value. Defaults to degree with implicit units.
 * @param alphaPercent If true, render the alpha as a percentage. By default, its rendered as a float.
 * @param renderAlpha Whether or not to render the alpha value.
 */
fun Color.formatCssHsl(
    commas: Boolean = false,
    namedHsla: Boolean = false,
    hueUnit: AngleUnit = AngleUnit.AUTO,
    alphaPercent: Boolean = false,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
): String {
    val hsl = toHSL()
    return hsl.renderFunction(
        if (namedHsla) "hsla" else "hsl",
        hsl.renderHue(hueUnit),
        hsl.s.render(true),
        hsl.l.render(true),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas
    )
}

private fun LAB.renderLab(alphaPercent: Boolean, renderAlpha: RenderCondition): String =
    renderFunction(
        "lab",
        (l / 100).render(percent = true),
        a.render(),
        b.render(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )

private fun LCHab.renderLCH(hueUnit: AngleUnit, alphaPercent: Boolean, renderAlpha: RenderCondition): String =
    renderFunction(
        "lch",
        (l / 100).render(percent = true),
        c.render(),
        renderHue(hueUnit),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )

private fun HWB.renderHWB(hueUnit: AngleUnit, alphaPercent: Boolean, renderAlpha: RenderCondition): String =
    renderFunction(
        "hwb",
        renderHue(hueUnit),
        w.render(percent = true),
        b.render(percent = true),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )

private fun Color.renderFunction(
    name: String,
    vararg components: String,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
    commas: Boolean = false,
): String = buildString {
    val sep = if (commas) ", " else " "
    append(name).append("(")
    components.joinTo(this, sep)
    append(renderAlpha(commas, renderAlpha, alphaPercent))
    append(")")
}


private fun HueColor.renderHue(hueUnit: AngleUnit): String = when (hueUnit) {
    AngleUnit.AUTO -> h.render()
    AngleUnit.DEGREES -> "${h.render()}deg"
    AngleUnit.RADIANS -> "${hueAsRad().render()}rad"
    AngleUnit.GRADIANS -> "${hueAsGrad().render()}grad"
    AngleUnit.TURNS -> "${hueAsTurns().render()}turn"
}

private fun Color.renderColorFunction(
    name: String,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String = buildString {
    append("color(").append(name).append(" ")
    toArray().dropLast(1).joinTo(this, " ") { it.render() }
    append(renderAlpha(false, renderAlpha, alphaPercent))
    append(")")
}

private fun Color.renderAlpha(commas: Boolean, renderAlpha: RenderCondition, alphaPercent: Boolean): String {
    return when {
        renderAlpha == RenderCondition.ALWAYS || renderAlpha == RenderCondition.AUTO && alpha != 1f -> {
            (if (commas) ", " else " / ") + alpha.render(alphaPercent)
        }
        else -> ""
    }
}

private fun Float.render(percent: Boolean = false): String = when (percent) {
    true -> "${(this * 100).roundToInt()}%"
    false -> when (this) {
        0f -> "0"
        1f -> "1"
        else -> {
            val str = toString()
            val s = if (str.startsWith(".")) "0$str" else str
            val i = s.indexOf('.')
            if (i < 0) s else s.take(i + 5).trimEnd('0').trimEnd('.')
        }
    }
}

private val Color.dashName: String get() = "--" + space.name.replace(Regex("\\W"), "-").lowercase()

