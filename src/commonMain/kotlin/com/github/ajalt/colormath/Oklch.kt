package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfo
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize

/**
 * Oklch color model, the cylindrical representation of [Oklab].
 *
 * | Component  | Description  | sRGB Gamut  |
 * | ---------- | ------------ | ----------- |
 * | [l]        | lightness    | `[0, 1]`     |
 * | [c]        | chroma       | `[0, 0.33]` |
 * | [h]        | hue, degrees | `[0, 360)`  |
 */
data class Oklch(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object {
        val model = object : ColorModel {
            override val name: String get() = "Oklch"
            override val components: List<ColorComponentInfo> = componentInfo(
                ColorComponentInfo("L", false, 0f, 1f),
                ColorComponentInfo("C", false, 0f, 0.32249096f),
                ColorComponentInfo("H", true, 0f, 360f),
            )
        }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val model: ColorModel get() = Oklch.model

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toOklab().toRGB()
    }

    override fun toXYZ(): XYZ = toOklab().toXYZ()
    override fun toOklab(): Oklab = fromPolarModel(c, h) { a, b -> return Oklab(l, a, b, alpha) }
    override fun toOklch(): Oklch = this

    override fun convertToThis(other: Color): Oklch = other.toOklch()
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun fromComponents(components: FloatArray): Oklch {
        requireComponentSize(components)
        return Oklch(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
