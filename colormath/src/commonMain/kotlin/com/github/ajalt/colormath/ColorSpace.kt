package com.github.ajalt.colormath

interface ColorSpace<T : Color> {
    /** The name of this color */
    val name: String

    /**
     * Information about the components (sometimes called channels) of this color.
     *
     * The list of components is the same size and order as the values returned from [Color.toArray]
     */
    val components: List<ColorComponentInfo>

    /** Convert a [color] to this space */
    fun convert(color: Color): T

    /**
     * Create a new instance of a color in this space from an array of [components].
     *
     * The [components] array must have a size equal to either the size of this
     * [space's components][ColorSpace.components], or one less,
     * in which case alpha will default to 1.
     */
    fun create(components: FloatArray): T
}

class ColorComponentInfo(
    /**
     * The name of this component
     */
    val name: String,

    /**
     * `true` if this component uses polar coordinates (e.g. a hue), and `false` if it's
     * rectangular.
     */
    val isPolar: Boolean,

    /**
     * The minimum of the reference range for this component.
     *
     * Note that some color models have components with no strict limits. For those components, this
     * is the limit of typical values.
     */
    val min: Float,

    /**
     * The maximum of the reference range for this component.
     *
     * Note that some color models have components with no strict limits. For those components, this
     * is the limit of typical values.
     */
    val max: Float,
) {
    @Deprecated(
        "Use the constructor with a max and min",
        ReplaceWith("ColorComponentInfo(name, isPolar, 0f, 1f)"),
    )
    constructor(name: String, isPolar: Boolean) : this(name, isPolar, 0f, 1f)

    init {
        require(min <= max) { "min must be less than or equal to max" }
    }
}

/** A color space that is defined with a reference [whitePoint]. */
interface WhitePointColorSpace<T : Color> : ColorSpace<T> {
    /** The white point that colors in this space are calculated relative to. */
    val whitePoint: WhitePoint
}
