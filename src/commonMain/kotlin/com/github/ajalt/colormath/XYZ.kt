package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.D65
import kotlin.math.pow

/**
 * CIE XYZ color space.
 *
 * Conversions use D65/2° illuminant, and sRGB profile.
 *
 * [x], [y], and [z] are generally in the interval `[-2, 2]`, but may be larger
 */
data class XYZ(val x: Float, val y: Float, val z: Float, val a: Float = 1f) : Color {
    constructor(x: Double, y: Double, z: Double, a: Float = 1f)
            : this(x.toFloat(), y.toFloat(), z.toFloat(), a)

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        // linearize sRGB values
        fun adj(c: Double): Float = when {
            c < 0.0031308 -> 12.92 * c
            else -> 1.055 * c.pow(1.0 / 2.4) - 0.055
        }.toFloat()

        // Matrix from http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
        val r = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z
        val g = -0.9692660 * x + 1.8760108 * y + 0.0415560 * z
        val b = 0.0556434 * x - 0.2040259 * y + 1.0572252 * z
        return RGB(adj(r), adj(g), adj(b), alpha)
    }

    override fun toLAB(): LAB {
        // Equations from http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
        fun f(t: Float) = when {
            t > CIE_E -> t.pow(1f / 3)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(100f * x / D65.x)
        val fy = f(100f * y / D65.y)
        val fz = f(100f * z / D65.z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LAB(l, a, b, alpha)
    }

    override fun toLUV(): LUV {
        // Equations from http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
        val x = this.x * 100f
        val y = this.y * 100f
        val z = this.z * 100f
        val denominator = x + 15 * y + 3 * z
        val uPrime = if (denominator == 0f) 0f else (4 * x) / denominator
        val vPrime = if (denominator == 0f) 0f else (9 * y) / denominator

        val denominatorReference = D65.x + 15 * D65.y + 3 * D65.z
        val uPrimeReference = (4 * D65.x) / denominatorReference
        val vPrimeReference = (9 * D65.y) / denominatorReference

        val yr = y / D65.y
        val l = when {
            yr > CIE_E -> 116 * yr.pow(1f / 3) - 16
            else -> CIE_K * yr
        }
        val u = 13 * l * (uPrime - uPrimeReference)
        val v = 13 * l * (vPrime - vPrimeReference)

        return LUV(l.coerceIn(0f, 100f), u, v, alpha)
    }
}
