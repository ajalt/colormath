package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RGB
import kotlin.math.max
import kotlin.math.min

/**
 * Calculate the relative luminance of this color according to the
 * [Web Content Accessibility Guidelines][https://www.w3.org/TR/WCAG21/#dfn-relative-luminance]
 *
 * @return The relative luminance of this color, which ranges from 0 to 1 for in-gamut sRGB colors
 */
fun Color.wcagLuminance(): Float {
    val (rs, gs, bs) = toLinearRGB()
    return (0.2126 * rs + 0.7152 * gs + 0.0722 * bs).toFloat()
}

/**
 * Calculate the contrast ration of this color with [other] according to the
 * [Web Content Accessibility Guidelines][https://www.w3.org/TR/WCAG21/#dfn-contrast-ratio]
 *
 * @return The contrast ratio of this color with [other], which ranges from 1 to 21 for in-gamut sRGB colors
 */
fun Color.wcagContrastRatio(other: Color): Float {
    val l = wcagLuminance()
    val r = other.wcagLuminance()
    return ((max(l, r) + 0.05) / (min(l, r) + 0.05)).toFloat()
}

/**
 * Return the [color][colors] with the highest [contrast ratio][wcagContrastRatio] against this color.
 *
 * This implements the `color-contrast` functionality specified in the
 * [CSS Color 5 Spec][https://www.w3.org/TR/css-color-5/#colorcontrast]
 */
fun Color.mostContrasting(vararg colors: Color): Color {
    require(colors.isNotEmpty()) { "colors cannot be empty" }
    return colors.maxByOrNull { wcagContrastRatio(it) }!!
}

/**
 * Return the first [color][colors] with a [contrast ratio][wcagContrastRatio] greater or equal to the [targetContrast]
 * against this color, or `null` if no color meets the target.
 *
 * This implements the `color-contrast` functionality specified in the
 * [CSS Color 5 Spec][https://www.w3.org/TR/css-color-5/#colorcontrast]
 */
fun Color.firstWithContrastOrNull(vararg colors: Color, targetContrast: Float): Color? {
    require(colors.isNotEmpty()) { "colors cannot be empty" }
    return colors.firstOrNull { wcagContrastRatio(it) >= targetContrast }
}

/**
 * Return the first [color][colors] with a [contrast ratio][wcagContrastRatio] exceeding the [targetContrast] against
 * this color. If no color meets the target, black or white will be returned, whichever has the most contrast.
 *
 * This implements the `color-contrast` functionality specified in the
 * [CSS Color 5 Spec][https://www.w3.org/TR/css-color-5/#colorcontrast]
 */
fun Color.firstWithContrast(vararg colors: Color, targetContrast: Float): Color {
    require(colors.isNotEmpty()) { "colors cannot be empty" }
    return colors.firstOrNull { wcagContrastRatio(it) >= targetContrast }
        ?: listOf(RGB(0f, 0f, 0f), RGB(1f, 1f, 1f)).maxByOrNull { wcagContrastRatio(it) }!!
}
