package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

fun <T : Color> T.interpolate(other: Color, amount: Float, premultiplyAlpha: Boolean = true): T =
    transform { components ->
        val l = mult(this, premultiplyAlpha, components)
        val r = convertToThis(other).let { mult(it, premultiplyAlpha, it.components()) }
        div(this, premultiplyAlpha, FloatArray(components.size) {
            lerp(l[it], r[it], amount)
        })
    }

private fun mult(color: Color, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) color.multiplyAlphaTransform(components) else components
}

private fun div(color: Color, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) color.divideAlphaTransform(components) else components
}

private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
