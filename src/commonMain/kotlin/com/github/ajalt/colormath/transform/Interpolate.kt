package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

fun <T : Color> T.interpolate(other: Color, amount: Float, premultiplyAlpha: Boolean = true): T =
    transform { model, components ->
        val l = mult(this, premultiplyAlpha, components)
        val r = model.convert(other).let { mult(it, premultiplyAlpha, it.components()) }
        div(this, premultiplyAlpha, FloatArray(components.size) {
            lerp(l[it], r[it], amount)
        })
    }

private fun mult(color: Color, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) multiplyAlphaTransform(color.model, components) else components
}

private fun div(color: Color, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) divideAlphaTransform(color.model, components) else components
}

private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
