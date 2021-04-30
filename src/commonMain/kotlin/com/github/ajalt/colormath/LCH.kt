package com.github.ajalt.colormath

import kotlin.math.cos
import kotlin.math.sin

/**
 * CIE LCh(uv) (The cylindrical representation of CIE 1976 L*u*v*) color space.
 *
 * [l] is a percentage, typically in the range `[0, 100]`, but theoretically the maximum is unbounded.
 * [c] is typically in the range `[0, 230]`, but theoretically the maximum is unbounded.
 * [h] is an angle in degrees, in the range `[0, 360]`
 */
data class LCH(val l: Float, val c: Float, val h: Float, override val alpha: Float = 1f) : Color {
    init {
        require(l >= 0) { "l must not be negative in $this" }
        require(c >= 0) { "c must not be negative in $this" }
        require(h in 0.0..360.0) { "h must be in range [0, 360] in $this" }
        require(alpha in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double = 1.0)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0, 0, 0, alpha)
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
}
