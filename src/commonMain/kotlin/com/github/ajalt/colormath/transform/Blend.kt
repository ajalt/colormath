package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

class Blend(private val other: Color, private val amount: Float) : ColorTransform {
    override fun apply(color: Color, components: FloatArray): FloatArray {
        // TODO: premultiply alpha
        val oc = color.convertToThis(other).components()
        return FloatArray(components.size) {
            lerp(components[it], oc[it], amount)
        }
    }

    private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
}

fun <T : Color> T.blend(other: Color, amount: Float): T = transform(Blend(other, amount))
