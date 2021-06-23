package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

private class Interpolate(private val other: Color, private val amount: Float) : ColorTransform {
    override fun apply(color: Color, components: FloatArray): FloatArray {
        // TODO: premultiply alpha
        val oc = color.convertToThis(other).components()
        return FloatArray(components.size) {
            lerp(components[it], oc[it], amount)
        }
    }

    private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
}

fun <T : Color> T.interpolate(other: Color, amount: Float): T = transform(Interpolate(other, amount))
