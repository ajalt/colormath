package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.withValidComps

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
        val model = object : ColorModel<Oklch> {
            override val name: String get() = "Oklch"
            override val components: List<ColorComponentInfo> = componentInfoList(
                ColorComponentInfo("L", false, 0f, 1f),
                ColorComponentInfo("C", false, 0f, 0.32249096f),
                ColorComponentInfo("H", true, 0f, 360f),
            )

            override fun convert(color: Color): Oklch = color.toOklch()
            override fun create(components: FloatArray): Oklch = withValidComps(components) {
                Oklch(it[0], it[1], it[2], it.getOrElse(3) { 1f })
            }
        }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val model: ColorModel<Oklch> get() = Oklch.model

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toOklab().toRGB()
    }

    override fun toXYZ(): XYZ = toOklab().toXYZ()
    override fun toOklab(): Oklab = fromPolarModel(c, h) { a, b -> return Oklab(l, a, b, alpha) }
    override fun toOklch(): Oklch = this
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
}
