package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

fun interface ColorTransform {
    fun apply(color: Color, components: FloatArray): FloatArray
}

@Suppress("UNCHECKED_CAST")
fun <T : Color> T.transform(transform: ColorTransform): T {
    return this.fromComponents(transform.apply(this, this.components())) as T
}
