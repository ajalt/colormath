package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

typealias ColorTransform = (model: ColorModel, components: FloatArray) -> FloatArray

@Suppress("UNCHECKED_CAST")
fun <T : Color> T.transform(transform: ColorTransform): T {
    return this.fromComponents(transform(model, components())) as T
}
