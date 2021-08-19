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
 * @param renderAlpha Whether to render the alpha value.
 * @param unitsPercent If true, render this color's components as percentages if the color syntax supports it.
 * @param alphaPercent If true, render the alpha as a percentage. By default, it's rendered as a float.
 * @param legacyName If true, use the legacy names `hsla` or `rgba` instead of `hsl` or `rgb for those functions.
 * @param legacyFormat If true, use commas instead of spaces as separators for `rgb` and `hsl` functions. Other colors are unaffected.
 */
fun Color.formatCssString(
    hueUnit: AngleUnit = AngleUnit.AUTO,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
    unitsPercent: Boolean = false,
    alphaPercent: Boolean = false,
    legacyName: Boolean = false,
    legacyFormat: Boolean = false,
): String {
    return when (this) {
        is RGB -> when (space) {
            SRGB -> renderSRGB(legacyFormat, legacyName, unitsPercent, alphaPercent, renderAlpha)
            RGBColorSpaces.DISPLAY_P3 -> renderColorFunction("display-p3", unitsPercent, alphaPercent, renderAlpha)
            RGBColorSpaces.ADOBE_RGB -> renderColorFunction("a98-rgb", unitsPercent, alphaPercent, renderAlpha)
            RGBColorSpaces.ROMM_RGB -> renderColorFunction("prophoto-rgb", unitsPercent, alphaPercent, renderAlpha)
            RGBColorSpaces.BT_2020 -> renderColorFunction("rec2020", unitsPercent, alphaPercent, renderAlpha)
            else -> renderColorFunction(dashName, unitsPercent, alphaPercent, renderAlpha)
        }
        is HSL -> renderHsl(legacyFormat, legacyName, hueUnit, alphaPercent, renderAlpha)
        is LAB -> convertTo(LAB50).renderLab(alphaPercent, renderAlpha)
        is LCHab -> convertTo(LCHab50).renderLCH(hueUnit, alphaPercent, renderAlpha)
        is HWB -> renderHWB(hueUnit, alphaPercent, renderAlpha)
        is XYZ -> adaptTo(XYZ50).renderColorFunction("xyz", unitsPercent, alphaPercent, renderAlpha)
        else -> renderColorFunction(dashName, unitsPercent, alphaPercent, renderAlpha)
    }
}

private fun RGB.renderSRGB(
    commas: Boolean,
    namedRgba: Boolean,
    rgbPercent: Boolean,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String =
    renderFunction(
        if (namedRgba) "rgba" else "rgb",
        if (rgbPercent) r.render(true) else toRGBInt().r.toString(),
        if (rgbPercent) g.render(true) else toRGBInt().g.toString(),
        if (rgbPercent) b.render(true) else toRGBInt().b.toString(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas,
    )

private fun HSL.renderHsl(
    commas: Boolean,
    namedHsla: Boolean,
    hueUnit: AngleUnit,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String =
    renderFunction(
        if (namedHsla) "hsla" else "hsl",
        renderHue(hueUnit),
        s.render(true),
        l.render(true),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas
    )

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
    unitsPercent: Boolean,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String = buildString {
    append("color(").append(name).append(" ")
    toArray().dropLast(1).joinTo(this, " ") { it.render(percent = unitsPercent) }
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

