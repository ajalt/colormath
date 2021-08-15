package com.github.ajalt.colormath

/**
 * A color that can be converted to other representations.
 *
 * The conversion functions can return the object they're called on if it is already in the
 * correct format.
 *
 * Note that there is not a direct conversion between every pair of representations. In those cases,
 * the values may be converted through one or more intermediate representations.
 *
 * All colors have an [alpha] value, which is the opacity of the color as a fraction between 0 and 1.
 * If a model doesn't support an alpha channel, the value 1 (fully opaque) is used.
 */
interface Color {
    /** The opacity of this color, in the range `[0, 1]` */
    val alpha: Float

    /** The color space describing this color */
    val space: ColorSpace<*>

    /** Convert this color to [sRGB][RGBColorSpaces.SRGB] */
    fun toSRGB(): RGB

    /** Convert this color to HSL */
    fun toHSL(): HSL = toSRGB().toHSL()

    /** Convert this color to HSV */
    fun toHSV(): HSV = toSRGB().toHSV()

    /** Convert this color to a 16-color ANSI code */
    fun toAnsi16(): Ansi16 = toSRGB().toAnsi16()

    /** Convert this color to a 256-color ANSI code */
    fun toAnsi256(): Ansi256 = toSRGB().toAnsi256()

    /** Convert this color to device-independent CMYK */
    fun toCMYK(): CMYK = toSRGB().toCMYK()

    /** Convert this color to CIE XYZ */
    fun toXYZ(): XYZ = toSRGB().toXYZ()

    /** Convert this color to CIE LAB */
    fun toLAB(): LAB = toXYZ().toLAB()

    /** Convert this color to CIE LCh(ab) */
    fun toLCHab(): LCHab = toLAB().toLCHab()

    /** Convert this color to CIE LUV */
    fun toLUV(): LUV = toXYZ().toLUV()

    /** Convert this color to CIE LCh(uv) */
    fun toLCHuv(): LCHuv = toLUV().toLCHuv()

    /** Convert this color to HWB */
    fun toHWB(): HWB = toSRGB().toHWB()

    /** Convert this color to Oklab */
    fun toOklab(): Oklab = toXYZ().toOklab()

    /** Convert this color to Oklch */
    fun toOklch(): Oklch = toOklab().toOklch()

    /** Convert this color to JzAzBz */
    fun toJzAzBz(): JzAzBz = toXYZ().toJzAzBz()

    /** Convert this color to JzCzHz */
    fun toJzCzHz(): JzCzHz = toJzAzBz().toJzCzHz()

    /** Convert this color to ICtCp */
    fun toICtCp(): ICtCp = convertTo(RGBColorSpaces.BT_2020).toICtCp()

    /** Create a [FloatArray] containing all components of this color, with the [alpha] as the last component */
    fun toArray(): FloatArray

    companion object // enables extensions on the interface
}

/**
 * Convert this color to a given [space].
 */
fun <T : Color> Color.convertTo(space: ColorSpace<T>): T = space.convert(this)
