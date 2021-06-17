package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.degToRad
import com.github.ajalt.colormath.internal.requireComponentSize
import kotlin.math.cos
import kotlin.math.sin

/**
 * CIE LCh(uv) (The cylindrical representation of CIE 1976 L*u*v*) color space.
 *
 * [l], lightness, is a percentage, typically in the range `[0, 100]`, but theoretically the maximum is unbounded.
 * [c], chroma, is typically in the range `[0, 230]`, but theoretically the maximum is unbounded.
 * [h], hue, is an angle in degrees, in the range `[0, 360]`
 */
data class LCH(val l: Float, val c: Float, val h: Float, override val alpha: Float = 1f) : Color {
    constructor(l: Double, c: Double, h: Double, alpha: Double = 1.0)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toLUV().toXYZ().toRGB()
    }


    override fun toLAB(): LAB {
        // https://www.w3.org/TR/css-color-4/#lch-to-lab
        val hDegrees = h.degToRad()
        val a = c * cos(hDegrees)
        val b = c * sin(hDegrees)
        return LAB(l.toDouble(), a.toDouble(), b.toDouble())
    }

    override fun toLUV(): LUV {
        // http://www.brucelindbloom.com/Eqn_LCH_to_Luv.html
        val hDegrees = h.degToRad()
        val u = c * cos(hDegrees)
        val v = c * sin(hDegrees)
        return LUV(l, u, v)
    }

    override fun toLCH(): LCH = this

    override fun convertToThis(other: Color): LCH = other.toLCH()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun fromComponents(components: FloatArray): LCH {
        requireComponentSize(components)
        return LCH(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
