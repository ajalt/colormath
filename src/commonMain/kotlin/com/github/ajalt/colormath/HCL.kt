package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.degToRad
import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex
import kotlin.math.cos
import kotlin.math.sin

/**
 * CIE LCh(uv) color model, the cylindrical representation of [LUV].
 *
 * [h], hue, is an angle in degrees, in the range `[0, 360]`
 * [c], chroma, is typically in the range `[0, 230]`, but theoretically the maximum is unbounded.
 * [l], lightness, is a percentage, typically in the range `[0, 100]`, but theoretically the maximum is unbounded.
 */
data class HCL(val h: Float, val c: Float, val l: Float, override val alpha: Float = 1f) : Color {
    constructor(h: Double, c: Double, l: Double, alpha: Double)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha.toFloat())

    constructor(h: Double, c: Double, l: Double, alpha: Float = 1.0f)
            : this(h.toFloat(), c.toFloat(), l.toFloat(), alpha)

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toLUV().toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ = toLUV().toXYZ()

    override fun toLUV(): LUV {
        // http://www.brucelindbloom.com/Eqn_LCH_to_Luv.html
        val hDegrees = h.degToRad()
        val u = c * cos(hDegrees)
        val v = c * sin(hDegrees)
        return LUV(l, u, v)
    }

    override fun toHCL(): HCL = this

    override fun convertToThis(other: Color): HCL = other.toHCL()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { i == 2 }
    override fun fromComponents(components: FloatArray): HCL {
        requireComponentSize(components)
        return HCL(components[2], components[1], components[0], components.getOrElse(3) { 1f })
    }
}
