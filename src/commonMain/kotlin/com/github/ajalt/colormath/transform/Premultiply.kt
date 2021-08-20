package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace

/**
 * Multiply this color's components by its alpha value.
 *
 * [Polar components][ColorComponentInfo.isPolar] and the alpha value itself are not changed.
 */
fun <T : Color> T.multiplyAlpha() = map { space, components ->
    components.also { multiplyAlphaInPlace(space, it) }
}

internal fun multiplyAlphaInPlace(space: ColorSpace<*>, components: FloatArray) {
    val a = components.last()
    if (a.isNaN() || a == 1f) return
    for (i in 0 until components.lastIndex) {
        if (space.components[i].isPolar) continue
        components[i] = components[i] * a
    }
}

/**
 * Divide this color's components by its alpha value.
 *
 * This is the inverse of [multiplyAlpha].
 *
 * [Polar components][ColorComponentInfo.isPolar] and the alpha value itself are not changed.
 * If `alpha == 0`, all components are left unchanged.
 */
fun <T : Color> T.divideAlpha(): T = map { space, components ->
    components.also { divideAlphaInPlace(space, it) }
}

internal fun divideAlphaInPlace(space: ColorSpace<*>, components: FloatArray) {
    val a = components.last()
    if (a.isNaN() || a == 0f || a == 1f) return
    for (i in 0 until components.lastIndex) {
        if (space.components[i].isPolar) continue
        components[i] = components[i] / a
    }
}

