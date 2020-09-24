package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * CIE XYZ color space.
 *
 * Conversions use D65/2° illuminant, and sRGB profile.
 *
 * [x], [y], and [z] are generally in the interval `[0, 100]`, but may be larger
 */
data class XYZ(val x: Double, val y: Double, val z: Double, val a: Float = 1f) : Color {
    init {
        require(x >= 0) { "x must be >= 0 in $this" }
        require(y >= 0) { "y must be >= 0 in $this" }
        require(z >= 0) { "z must be >= 0 in $this" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val x = this.x / 100
        val y = this.y / 100
        val z = this.z / 100

        // linearize sRGB values
        fun adj(c: Double): Int {
            val adj = when {
                c < 0.0031308 -> 12.92 * c
                else -> 1.055 * c.pow(1.0 / 2.4) - 0.055
            }
            return (255 * adj.coerceIn(0.0, 1.0)).roundToInt()
        }

        // Matrix from http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
        val r = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z
        val g = -0.9692660 * x + 1.8760108 * y + 0.0415560 * z
        val b = 0.0556434 * x - 0.2040259 * y + 1.0572252 * z
        return RGB(adj(r), adj(g), adj(b), alpha)
    }

    override fun toLAB(): LAB {
        // Equations from http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
        fun f(t: Double) = when {
            t > CIE_E -> t.pow(1.0 / 3)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(x / D65.x)
        val fy = f(y / D65.y)
        val fz = f(z / D65.z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LAB(l.coerceIn(0.0, 100.0), a, b, alpha)
    }

    override fun toLUV(): LUV {
        // Equations from http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
        val yr = y / D65.y

        val denominator = x + 15 * y + 3 * z
        val uPrime = if (denominator == 0.0) 0.0 else (4 * x) / denominator
        val vPrime = if (denominator == 0.0) 0.0 else (9 * y) / denominator

        val denominatorr = D65.x + 15 * D65.y + 3 * D65.z
        val urPrime = (4 * D65.x) / denominatorr
        val vrPrime = (9 * D65.y) / denominatorr

        val l = (CIE_K * yr).let {
            if (it > CIE_E_times_K) 116 * yr.pow(1.0 / 3) - 16
            else it
        }
        val u = 13 * l * (uPrime - urPrime)
        val v = 13 * l * (vPrime - vrPrime)

        return LUV(l.coerceIn(0.0, 100.0), u, v, alpha)
    }
}
