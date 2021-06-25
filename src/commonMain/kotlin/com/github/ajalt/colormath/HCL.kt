package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex

/**
 * CIE LCh(uv) color model, the cylindrical representation of [LUV].
 *
 * [h], hue, is an angle in degrees, in the range `[0, 360]`
 * [c], chroma, is typically in the range `[0, 230]`, but theoretically the maximum is unbounded.
 * [l], luminance, is a percentage, typically in the range `[0, 100]`, but theoretically the maximum is unbounded.
 */
data class HCL(val h: Float, val c: Float, val l: Float, override val alpha: Float = 1f) : Color {
    constructor(h: Double, c: Double, l: Double, alpha: Double)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha.toFloat())

    constructor(h: Double, c: Double, l: Double, alpha: Float = 1.0f)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha)

    override fun toRGB(): RGB = toLUV().toRGB()
    override fun toXYZ(): XYZ = toLUV().toXYZ()
    override fun toLUV(): LUV = fromPolarModel(c, h) { u, v -> LUV(l, u, v, alpha) }
    override fun toHCL(): HCL = this

    override fun convertToThis(other: Color): HCL = other.toHCL()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(h, c, l, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { i == 0 }
    override fun fromComponents(components: FloatArray): HCL {
        requireComponentSize(components)
        return HCL(components[2], components[1], components[0], components.getOrElse(3) { 1f })
    }
}
