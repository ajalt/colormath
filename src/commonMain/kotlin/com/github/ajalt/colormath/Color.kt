package com.github.ajalt.colormath

/**
 * A color that can be converted to other representations.
 *
 * The conversion functions can return the object they're called on if it is already in the
 * correct format.
 *
 * Note that there is not a direct conversion between every pair of representations. In those cases,
 * the values may be converted through one or more intermediate representations. This may cause a
 * loss of precision.
 *
 * All colors have an [alpha] value, which is the opacity of the color as a fraction between 0 and 1. If a model doesn't
 * support an alpha channel, the value 1 (fully opaque) is used.
 */
interface Color {
    /** The opacity of this color, in the range `[0, 1]`. */
    val alpha: Float

    /** Convert this color to Red-Green-Blue (using sRGB color space) */
    fun toRGB(): RGB

    /**
     * Convert this color to an RGB hex string.
     *
     * If [renderAlpha] is `ALWAYS`, the [alpha] value will be added e.g. the `aa` in `#ffffffaa`.
     * If it's `NEVER`, the [alpha] will be omitted. If it's `AUTO`, then the [alpha] will be added
     * if it's less than 1.
     *
     * @return A string in the form `"#ffffff"` if [withNumberSign] is true,
     *     or in the form `"ffffff"` otherwise.
     */
    fun toHex(
        withNumberSign: Boolean = true,
        renderAlpha: RenderCondition = RenderCondition.AUTO,
    ): String = toRGB().toHex(withNumberSign)

    /** Convert this color to Hue-Saturation-Luminosity */
    fun toHSL(): HSL = toRGB().toHSL()

    /** Convert this color to Hue-Saturation-Value */
    fun toHSV(): HSV = toRGB().toHSV()

    /** Convert this color to a 16-color ANSI code */
    fun toAnsi16(): Ansi16 = toRGB().toAnsi16()

    /** Convert this color to a 256-color ANSI code */
    fun toAnsi256(): Ansi256 = toRGB().toAnsi256()

    /** Convert this color to Cyan-Magenta-Yellow-Key */
    fun toCMYK(): CMYK = toRGB().toCMYK()

    /** Convert this color to CIE XYZ */
    fun toXYZ(): XYZ = toRGB().toXYZ()

    /** Convert this color to CIE LAB */
    fun toLAB(): LAB = toXYZ().toLAB()

    /** Convert this color to CIE LUV */
    fun toLUV(): LUV = toXYZ().toLUV()

    /** Convert this color to CIE LCh(uv) */
    fun toLCH(): LCH = toLUV().toLCH()

    /** Convert this color to HWB */
    fun toHWB(): HWB = toRGB().toHWB()

    /** Convert this color to Linear sRGB */
    fun toLinearRGB(): LinearRGB = toRGB().toLinearRGB()

    /** Convert an [other] color to this color's model */
    fun convertToThis(other: Color): Color

    /** Create a [FloatArray] containing all components of this color, with the [alpha] as the last component */
    fun components(): FloatArray

    /** The number of components, including [alpha], in this color. This is the the size of the array returned by [components] */
    fun componentCount(): Int

    /**
     * Create a  new instance of this color from an array of [components].
     *
     * The [components] array must have a size equal to either the [componentCount] of this color, or one less, in which
     * case [alpha] will default to 1.
     */
    fun fromComponents(components: FloatArray): Color

    companion object // enables extensions on the interface
}
