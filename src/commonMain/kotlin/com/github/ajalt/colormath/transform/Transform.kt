package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

typealias ColorMapper<T> = (model: ColorModel<T>, components: FloatArray) -> FloatArray

@Suppress("UNCHECKED_CAST")
fun <T : Color> T.map(mapper: ColorMapper<T>): T {
    return model.create(mapper(model as ColorModel<T>, toArray())) as T
}
