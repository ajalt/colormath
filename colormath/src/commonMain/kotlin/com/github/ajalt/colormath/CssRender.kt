package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.nanToOne
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import kotlin.jvm.JvmOverloads
import kotlin.math.absoluteValue
import kotlin.math.pow
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
 * Render this color in CSS functional notation if this color space is defined in the CSS standard,
 * or return null if this color is in another color space.
 *
 * If CSS defines a color function syntax for this color's model (e.g. `lab()`), it will be used.
 * Otherwise, the `color()` syntax will be used.
 *
 * Note that for [LAB], and [LCHab], the CSS standard requires that the [D50][Illuminant.D50] white
 * point be used. For [XYZ], [D50][Illuminant.D50] and [D65][Illuminant.D65] are supported. Colors
 * using other white points will be adapted to D50 before being serialized. Most color models
 * default to [D65][Illuminant.D65], so you should use [XYZ50], [XYZ], [LAB50], and [LCHab50] if
 * you're serializing a color in those models.
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
 * ```
 * > JzAzBz(0.1, 0.2, 0.3).formatCssString(customColorSpaces= listOf("jzazbz" to JzAzBz))
 * "color(jzazbz 0.1 0.2 0.3)"
 * ```
 *
 * @param hueUnit The unit to use to render hue values, if this color has any.
 * @param renderAlpha Whether to render the alpha value.
 * @param unitsPercent If true, render this color's components as percentages if the color syntax supports it.
 * @param alphaPercent If true, render the alpha as a percentage. By default, it's rendered as a float.
 * @param legacyName If true, use the legacy names `hsla` or `rgba` instead of `hsl` or `rgb for those functions.
 * @param legacyFormat If true, use commas instead of spaces as separators for `rgb` and `hsl` functions. Other colors are unaffected.
 * @param customColorSpaces A list of custom color spaces to use in the `color()` function.
 * Each pair should be the identifier of the color and its [ColorSpace].
 */
fun Color.formatCssStringOrNull(
    hueUnit: AngleUnit = AngleUnit.AUTO,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
    unitsPercent: Boolean = false,
    alphaPercent: Boolean = false,
    legacyName: Boolean = false,
    legacyFormat: Boolean = false,
    customColorSpaces: Map<String, ColorSpace<*>> = emptyMap(),
): String? {
    return customColorSpaces.entries.firstOrNull { it.value == space }?.key?.let { spaceName ->
        renderFn(spaceName, unitsPercent, alphaPercent, renderAlpha)
    } ?: when (this) {
        is RGB -> when (space) {
            SRGB -> renderSRGB(legacyFormat, legacyName, unitsPercent, alphaPercent, renderAlpha)
            DisplayP3 -> renderFn("display-p3", unitsPercent, alphaPercent, renderAlpha)
            AdobeRGB -> renderFn("a98-rgb", unitsPercent, alphaPercent, renderAlpha)
            ROMM_RGB -> renderFn("prophoto-rgb", unitsPercent, alphaPercent, renderAlpha)
            BT2020 -> renderFn("rec2020", unitsPercent, alphaPercent, renderAlpha)
            LinearSRGB -> renderFn("srgb-linear", unitsPercent, alphaPercent, renderAlpha)
            else -> null
        }

        is HSL -> renderHsl(legacyFormat, legacyName, hueUnit, alphaPercent, renderAlpha)
        is LAB -> convertTo(LAB50).renderLab(alphaPercent, renderAlpha)
        is LCHab -> convertTo(LCHab50).renderLCH(hueUnit, alphaPercent, renderAlpha)
        is HWB -> renderHWB(hueUnit, alphaPercent, renderAlpha)
        is Oklab -> renderOklab(alphaPercent, renderAlpha)
        is Oklch -> renderOklch(hueUnit, alphaPercent, renderAlpha)
        is XYZ -> when (space.whitePoint) {
            Illuminant.D65 -> renderFn("xyz-d65", unitsPercent, alphaPercent, renderAlpha)
            else -> adaptTo(XYZ50).renderFn("xyz", unitsPercent, alphaPercent, renderAlpha)
        }

        else -> null
    }
}


/**
 * Render this color in CSS functional notation.
 *
 * If CSS defines a color function syntax for this color's model (e.g. `lab()`), it will be used.
 * Otherwise, the `color()` syntax will be used. For color spaces not predefined by CSS, you can
 * pass them in [customColorSpaces]. Other color spaces will use a dashed identifier based on the
 * [space's name][ColorSpace.name].
 *
 * Note that for [LAB], and [LCHab], the CSS standard requires that the [D50][Illuminant.D50] white
 * point be used. For [XYZ], [D50][Illuminant.D50] and [D65][Illuminant.D65] are supported. Colors
 * using other white points will be adapted to D50 before being serialized. Most color models
 * default to [D65][Illuminant.D65], so you should use [XYZ50], [XYZ], [LAB50], and [LCHab50] if
 * you're serializing a color in those models.
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
 * ```
 * > JzAzBz(0.1, 0.2, 0.3).formatCssString(customColorSpaces=listOf("jzazbz" to JzAzBz))
 * "color(jzazbz 0.1 0.2 0.3)"
 * ```
 *
 * @param hueUnit The unit to use to render hue values, if this color has any.
 * @param renderAlpha Whether to render the alpha value.
 * @param unitsPercent If true, render this color's components as percentages if the color syntax supports it.
 * @param alphaPercent If true, render the alpha as a percentage. By default, it's rendered as a float.
 * @param legacyName If true, use the legacy names `hsla` or `rgba` instead of `hsl` or `rgb for those functions.
 * @param legacyFormat If true, use commas instead of spaces as separators for `rgb` and `hsl` functions. Other colors are unaffected.
 * @param customColorSpaces A list of custom color spaces to use in the `color()` function.
 * Each pair should be the identifier of the color and its [ColorSpace].
 */
@JvmOverloads // TODO(4.0) remove this
fun Color.formatCssString(
    hueUnit: AngleUnit = AngleUnit.AUTO,
    renderAlpha: RenderCondition = RenderCondition.AUTO,
    unitsPercent: Boolean = false,
    alphaPercent: Boolean = false,
    legacyName: Boolean = false,
    legacyFormat: Boolean = false,
    customColorSpaces: Map<String, ColorSpace<*>> = emptyMap(),
): String {
    return formatCssStringOrNull(
        hueUnit,
        renderAlpha,
        unitsPercent,
        alphaPercent,
        legacyName,
        legacyFormat,
        customColorSpaces
    ) ?: renderFn(dashName, unitsPercent, alphaPercent, renderAlpha)
}

private fun RGB.renderSRGB(
    commas: Boolean,
    namedRgba: Boolean,
    rgbPercent: Boolean,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String {
    return renderColorFn(
        if (namedRgba) "rgba" else "rgb",
        if (rgbPercent) r.render(true) else toRGBInt().r.toString(),
        if (rgbPercent) g.render(true) else toRGBInt().g.toString(),
        if (rgbPercent) b.render(true) else toRGBInt().b.toString(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas,
    )
}

private fun HSL.renderHsl(
    commas: Boolean,
    namedHsla: Boolean,
    hueUnit: AngleUnit,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String {
    return renderColorFn(
        if (namedHsla) "hsla" else "hsl",
        renderHue(hueUnit),
        s.render(true),
        l.render(true),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
        commas = commas
    )
}

private fun LAB.renderLab(alphaPercent: Boolean, renderAlpha: RenderCondition): String {
    return renderColorFn(
        "lab",
        (l / 100).render(percent = true),
        a.render(),
        b.render(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )
}

private fun LCHab.renderLCH(
    hueUnit: AngleUnit,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String {
    return renderColorFn(
        "lch",
        (l / 100).render(percent = true),
        c.render(),
        renderHue(hueUnit),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )
}

private fun HWB.renderHWB(
    hueUnit: AngleUnit,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String {
    return renderColorFn(
        "hwb",
        renderHue(hueUnit),
        w.render(percent = true),
        b.render(percent = true),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )
}

private fun Oklab.renderOklab(alphaPercent: Boolean, renderAlpha: RenderCondition): String {
    return renderColorFn(
        "oklab",
        l.render(percent = true),
        a.render(),
        b.render(),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )
}

private fun Oklch.renderOklch(
    hueUnit: AngleUnit,
    alphaPercent: Boolean,
    renderAlpha: RenderCondition,
): String {
    return renderColorFn(
        "oklch",
        l.render(percent = true),
        c.render(),
        renderHue(hueUnit),
        alphaPercent = alphaPercent,
        renderAlpha = renderAlpha,
    )
}

private fun Color.renderColorFn(
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


private fun HueColor.renderHue(hueUnit: AngleUnit): String = if (h.isNaN()) "none" else
    when (hueUnit) {
        AngleUnit.AUTO -> h.render()
        AngleUnit.DEGREES -> "${h.render()}deg"
        AngleUnit.RADIANS -> "${hueAsRad().render()}rad"
        AngleUnit.GRADIANS -> "${hueAsGrad().render()}grad"
        AngleUnit.TURNS -> "${hueAsTurns().render()}turn"
    }

private fun Color.renderFn(
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

private fun Color.renderAlpha(
    commas: Boolean,
    renderAlpha: RenderCondition,
    alphaPercent: Boolean,
): String {
    return when {
        renderAlpha == RenderCondition.ALWAYS ||
                renderAlpha == RenderCondition.AUTO && !alpha.isNaN() && alpha != 1f -> {
            (if (commas) ", " else " / ") + alpha.nanToOne().render(alphaPercent)
        }

        else -> ""
    }
}

// TODO: HSL, HWB have different ref ranges
private fun Float.render(percent: Boolean = false, precision: Int = 4): String = when {
    isNaN() -> "none"
    percent -> "${(this * 100).roundToInt()}%"
    else -> {
        val abs = absoluteValue
        val i = abs.toInt()
        val sgn = if (this < 0) "-" else ""
        val d = ((abs - i) * (10.0.pow(precision))).roundToInt()
        if (d == 0) toInt().toString()
        else "$sgn$i.${d.toString().padStart(precision, '0').trimEnd('0')}"
    }
}

private val Color.dashName: String get() = "--" + space.name.replace(Regex("\\W"), "-").lowercase()

