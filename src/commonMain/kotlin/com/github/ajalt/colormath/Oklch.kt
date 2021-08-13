package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.polarComponentInfo

/**
 * Oklch color model, the cylindrical representation of [Oklab].
 *
 * | Component  | Description  | sRGB Range  |
 * | ---------- | ------------ | ----------- |
 * | [l]        | lightness    | `[0, 1]`     |
 * | [c]        | chroma       | `[0, 0.33]` |
 * | [h]        | hue, degrees | `[0, 360)`  |
 */
data class Oklch(val l: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorSpace<Oklch> {
        override val name: String get() = "Oklch"
        override val components: List<ColorComponentInfo> = polarComponentInfo("LCH")
        override fun convert(color: Color): Oklch = color.toOklch()
        override fun create(components: FloatArray): Oklch = doCreate(components, ::Oklch)
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val space: ColorSpace<Oklch> get() = Oklch

    override fun toSRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toOklab().toSRGB()
    }

    override fun toXYZ(): XYZ = toOklab().toXYZ()
    override fun toOklab(): Oklab = fromPolarModel(c, h) { a, b -> return Oklab(l, a, b, alpha) }
    override fun toOklch(): Oklch = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
}
