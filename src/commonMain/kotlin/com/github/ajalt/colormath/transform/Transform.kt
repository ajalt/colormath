package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorSpace

/**
 * A mapping function used with [map].
 *
 * ### Parameters
 * - `space`: The [ColorSpace] of the color being mapped
 * - `components`: The [components][Color.toArray] of the color to map.
 *
 * ### Returns
 * The new color components. You may alter and return `components` directly, or a new array the same size as
 * `components`.
 */
typealias ColorMapper<T> = ColorSpace<T>.(components: FloatArray) -> FloatArray

/**
 * Return an new color in the same color space that is the result of applying [transform] to the components of this
 * color.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Color> T.map(transform: ColorMapper<T>): T {
    return space.create(transform(space as ColorSpace<T>, toArray())) as T
}
