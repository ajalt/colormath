package com.github.ajalt.colormath

interface ColorModel<T : Color> {
    /** The name of this color */
    val name: String

    /**
     * Information about the components of this color.
     *
     * The components are the same size and order as the values returned from [Color.toArray]
     */
    val components: List<ColorComponentInfo>

    /** Convert a [color] this model */
    fun convert(color: Color): T

    /**
     * Create a new instance of a color in this model from an array of [components].
     *
     * The [components] array must have a size equal to either the size of this model's [ColorModel.components], or one
     * less, in which case alpha will default to 1.
     */
    fun create(components: FloatArray): T
}

class ColorComponentInfo(
    /** The name of this component */
    val name: String,
    /** `true` if this component uses polar coordinates (e.g. a hue), and `false` if it's rectangular. */
    val isPolar: Boolean,
)

/** A color space that is defined with a reference [whitePoint]. */
interface WhitePointColorSpace<T : Color> : ColorModel<T> {
    /** The white point that colors in this space are calculated relative to. */
    val whitePoint: Illuminant
}
