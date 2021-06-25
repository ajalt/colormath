package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color

typealias ColorTransform = Color.(components: FloatArray) -> FloatArray

@Suppress("UNCHECKED_CAST")
fun <T : Color> T.transform(transform: ColorTransform): T {
    return this.fromComponents(transform(components())) as T
}
