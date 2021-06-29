package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo

/**
 * Multiply this color's components by its alpha value.
 *
 * [Polar components][ColorComponentInfo.isPolar] and the alpha value itself are not changed.
 */
fun <T : Color> T.multiplyAlpha() = transform(multiplyAlphaTransform)
internal val multiplyAlphaTransform: ColorTransform<*> = { model, components ->
    val a = components.last()
    FloatArray(components.size) { i ->
        if (i == components.lastIndex || model.components[i].isPolar) components[i]
        else components[i] * a
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
fun <T : Color> T.divideAlpha(): T = transform(divideAlphaTransform)
val divideAlphaTransform: ColorTransform<*> = { model, components ->
    val a = components.last()
    FloatArray(components.size) { i ->
        if (a == 0f || i == components.lastIndex || model.components[i].isPolar) components[i]
        else components[i] / a
    }
}

