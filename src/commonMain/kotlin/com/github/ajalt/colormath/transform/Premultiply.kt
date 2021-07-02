package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorModel

/**
 * Multiply this color's components by its alpha value.
 *
 * [Polar components][ColorComponentInfo.isPolar] and the alpha value itself are not changed.
 */
fun <T : Color> T.multiplyAlpha() = transform { model, components ->
    components.also { multiplyAlphaInPlace(model, it) }
}

internal fun multiplyAlphaInPlace(model: ColorModel<*>, components: FloatArray) {
    val a = components.last()
    if (a == 1f) return
    for (i in 0 until components.lastIndex) {
        if (model.components[i].isPolar) continue
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
fun <T : Color> T.divideAlpha(): T = transform { model, components ->
    components.also { divideAlphaInPlace(model, it) }
}

internal fun divideAlphaInPlace(model: ColorModel<*>, components: FloatArray) {
    val a = components.last()
    if (a == 0f || a == 1f) return
    for (i in 0 until components.lastIndex) {
        if (model.components[i].isPolar) continue
        components[i] = components[i] / a
    }
}

