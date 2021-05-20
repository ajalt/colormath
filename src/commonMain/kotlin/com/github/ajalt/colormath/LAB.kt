package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * CIE LAB color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [l] is a percentage, typically in the range `[0, 100]`, but can exceed 100 (e.g. for HDR systems).
 * [a] and [b] are unbounded, but are typically the range `[-160, 160]`.
 */
data class LAB(val l: Double, val a: Double, val b: Double, override val alpha: Float = 1f) : Color {
    init {
        require(l >= 0) { "l must not be negative in $this" }
        require(alpha in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override fun toRGB(): RGB = when (l) {
        0.0 -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Lab_to_XYZ.html
        if (l == 0.0) return XYZ(0.0, 0.0, 0.0)

        val fy = (l + 16) / 116
        val fz = fy - b / 200
        val fx = a / 500 + fy

        val yr = if (l > CIE_E_times_K) fy.pow(3) else l / CIE_K
        val zr = fz.pow(3).let { if (it > CIE_E) it else (116 * fz - 16) / CIE_K }
        val xr = fx.pow(3).let { if (it > CIE_E) it else (116 * fx - 16) / CIE_K }

        return XYZ(xr * D65.x, yr * D65.y, zr * D65.z, alpha)
    }

    override fun toLCH(): LCH {
        // https://www.w3.org/TR/css-color-4/#lab-to-lch
        val c = sqrt(a * a + b * b)
        val h = if (c < 1e-8) 0f else {
            atan2(b, a).toFloat().radToDeg()
        }.let { if (it < 0) it + 360 else it }
        return LCH(l.toFloat(), c.toFloat(), h)
    }

    override fun toLAB(): LAB = this
}
