package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

typealias ColorTransform<T> = (model: ColorModel<T>, components: FloatArray) -> FloatArray

@Suppress("UNCHECKED_CAST")
fun <T : Color> T.transform(transform: ColorTransform<T>): T {
    return model.create(transform(model as ColorModel<T>, components())) as T
}
