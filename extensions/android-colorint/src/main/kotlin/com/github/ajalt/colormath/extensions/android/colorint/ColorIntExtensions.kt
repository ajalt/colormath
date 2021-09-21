package com.github.ajalt.colormath.extensions.android.colorint

import androidx.annotation.ColorInt
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBInt
import com.github.ajalt.colormath.model.SRGB


/**
 * Convert this color to a packed argb color int.
 */
@ColorInt
fun Color.toColorInt(): Int {
    return toSRGB().toRGBInt().argb.toInt()
}

/**
 * Create an [SRGB] instance from a packed argb color int.
 */
fun RGB.Companion.fromColorInt(@ColorInt argb: Int): RGB {
    return RGBInt(argb.toUInt()).toSRGB()
}

/**
 * Create an [RGBInt] instance from a packed argb color int.
 */
fun RGBInt.Companion.fromColorInt(@ColorInt argb: Int): RGBInt {
    return RGBInt(argb.toUInt())
}
