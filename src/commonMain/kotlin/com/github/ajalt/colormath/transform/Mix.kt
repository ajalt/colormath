package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.internal.nanToOne

fun <T : Color> ColorSpace<T>.mix(
    color1: Color,
    color2: Color,
    hueAdjustment: ComponentAdjustment = HueAdjustments.shorter,
): Color = mix(color1, .5f, color2, .5f, hueAdjustment)

fun <T : Color> ColorSpace<T>.mix(
    color1: Color,
    amount1: Float,
    color2: Color,
    hueAdjustment: ComponentAdjustment = HueAdjustments.shorter,
): Color = mix(color1, amount1, color2, 1f - amount1, hueAdjustment)

fun <T : Color> ColorSpace<T>.mix(
    color1: Color,
    color2: Color,
    amount2: Float,
    hueAdjustment: ComponentAdjustment = HueAdjustments.shorter,
): Color = mix(color1, 1f - amount2, color2, amount2, hueAdjustment)

/**
 * Mix [amount1] of [color1] and [amount2] of [color2] in this color space.
 *
 * The sum of the amounts is greater than one, they will be normalized so the sum equals one. If the sum is less than
 * one, they will be normalized and the final alpha value will be multiplied by their sum.
 *
 * This implements the `color-mix` functionality specified in
 * [CSS Color Module 5][https://www.w3.org/TR/css-color-5/#color-mix]
 *
 * @param amount1 The amount of [color1] to mix. A fraction in `[0, 1]`. If omitted, defaults to `1 - amount2`
 * @param amount2 The amount of [color2] to mix. A fraction in `[0, 1]`. If omitted, defaults to `1 - amount1`
 * @param hueAdjustment An optional adjustment to the hue components of the colors, if there is one. Defaults to [HueAdjustments.shorter].
 */
fun <T : Color> ColorSpace<T>.mix(
    color1: Color,
    amount1: Float,
    color2: Color,
    amount2: Float,
    hueAdjustment: ComponentAdjustment = HueAdjustments.shorter,
): Color {
    val sum = amount1 + amount2
    require(sum != 0f) { "mix amounts cannot sum to 0" }
    val c = convert(color1).interpolate(color2, amount2 / sum, true, hueAdjustment)
    return if (sum < 1f) c.map { _, comps -> comps.also { it[it.lastIndex] = it.last().nanToOne() * sum } } else c
}
