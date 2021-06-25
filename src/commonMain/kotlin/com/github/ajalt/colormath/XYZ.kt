package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import com.github.ajalt.colormath.internal.Illuminant.D65

/**
 * CIE XYZ color space.
 *
 * Conversions use D65/2° illuminant, and sRGB profile.
 *
 * | Component  | Gamut     |
 * | ---------- | --------- |
 * | [x]        | `[-2, 2]` |
 * | [y]        | `[-2, 2]` |
 * | [z]        | `[-2, 2]` |
 */
data class XYZ(val x: Float, val y: Float, val z: Float, val a: Float = 1f) : Color {
    constructor(x: Double, y: Double, z: Double, a: Float = 1f)
            : this(x.toFloat(), y.toFloat(), z.toFloat(), a)

    override val alpha: Float get() = a

    // Matrix from http://www.brucelindbloom.com/Eqn_XYZ_to_RGB.html
    private fun r() = 3.2404542f * x - 1.5371385f * y - 0.4985314f * z
    private fun g() = -0.9692660f * x + 1.8760108f * y + 0.0415560f * z
    private fun b() = 0.0556434f * x - 0.2040259f * y + 1.0572252f * z

    override fun toRGB(): RGB = RGB(linearToSRGB(r()), linearToSRGB(g()), linearToSRGB(b()), alpha)
    override fun toLinearRGB(): LinearRGB = LinearRGB(r(), g(), b(), alpha)

    override fun toLAB(): LAB {
        // http://www.brucelindbloom.com/Eqn_XYZ_to_Lab.html
        fun f(t: Float) = when {
            t > CIE_E -> cbrt(t)
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
            yr > CIE_E -> 116 * cbrt(yr) - 16
            else -> CIE_K * yr
        }
        val u = 13 * l * (uPrime - uPrimeReference)
        val v = 13 * l * (vPrime - vPrimeReference)

        return LUV(l.coerceIn(0f, 100f), u, v, alpha)
    }

    // https://bottosson.github.io/posts/oklab/#converting-from-xyz-to-oklab
    override fun toOklab(): Oklab {
        val l = +0.8189330101 * x + 0.3618667424 * y - 0.1288597137 * z
        val m = +0.0329845436 * x + 0.9293118715 * y + 0.0361456387 * z
        val s = +0.0482003018 * x + 0.2643662691 * y + 0.6338517070 * z

        val ll = cbrt(l)
        val mm = cbrt(m)
        val ss = cbrt(s)

        return Oklab(
            l = +0.2104542553 * ll + 0.7936177850 * mm - 0.0040720468 * ss,
            a = +1.9779984951 * ll - 2.4285922050 * mm + 0.4505937099 * ss,
            b = +0.0259040371 * ll + 0.7827717662 * mm - 0.8086757660 * ss,
            alpha = alpha
        )
    }

    override fun convertToThis(other: Color): XYZ = other.toXYZ()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(x, y, z, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { false }
    override fun fromComponents(components: FloatArray): XYZ {
        requireComponentSize(components)
        return XYZ(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
