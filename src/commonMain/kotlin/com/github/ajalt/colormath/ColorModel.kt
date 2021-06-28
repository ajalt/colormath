package com.github.ajalt.colormath

interface ColorModel {
    /** The name of this color */
    val name: String

    /**
     * Information about the components of this color.
     *
     * The components are the same size and order as the values returned from [Color.components]
     */
    val components: List<ColorComponentInfo>
}

class ColorComponentInfo(
    /** The name of this component */
    val name: String,
    /** `true` if this component uses polar coordinates (e.g. a hue), and `false` if it's rectangular. */
    val isPolar: Boolean,
    /** The minimum value that this component will have when representing a color converted from the sRGB space */
    val min: Float,
    /** The maximum value that this component will have when representing a color converted from the sRGB space */
    val max: Float,
)
