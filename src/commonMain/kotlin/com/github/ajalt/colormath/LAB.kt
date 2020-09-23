package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.pow

/**
 * CIE LAB color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [l] is in the range `[0, 100]`. [a] and [b] are in the range `[-128, 128]`
 */
data class LAB(val l: Double, val a: Double, val b: Double, override val alpha: Float = 1f) : Color {
    init {
        require(l in 0.0..100.0) { "l must be in interval [0, 100] in $this" }
        require(alpha in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override fun toRGB(): RGB = when (l) {
        0.0 -> RGB(0, 0, 0, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        if (l == 0.0) return XYZ(0.0, 0.0, 0.0)

        // Equations from http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
        val fy = (l + 16) / 116
        val fz = fy - b / 200
        val fx = a / 500 + fy

        fun f(t: Double) = t.pow(3).let {
            if (it > CIE_E) it
            else (116 * t - 16) / CIE_K
        }

        val yr = if (l > CIE_E_times_K) fy.pow(3) else l / CIE_K
        val zr = f(fz)
        val xr = f(fx)

        return XYZ(xr * D65.x, yr * D65.y, zr * D65.z, alpha)
    }

    override fun toLAB(): LAB = this
}
