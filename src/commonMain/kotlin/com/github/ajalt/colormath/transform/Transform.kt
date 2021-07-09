package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

/**
 * A mapping function used with [map].
 *
 * ### Parameters
 * - `model`: The [ColorModel] of the color being mapped
 * - `components`: The [components][Color.toArray] of the color to map.
 *
 * ### Returns
 * The new color components. You may alter and return `components` directly, or a new array the same size as
 * `components`.
 */
typealias ColorMapper<T> = (model: ColorModel<T>, components: FloatArray) -> FloatArray

/**
 * Return an new color in the same color space that is the result of applying [transform] to the components of this
 * color.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Color> T.map(transform: ColorMapper<T>): T {
    return model.create(transform(model as ColorModel<T>, toArray())) as T
}
