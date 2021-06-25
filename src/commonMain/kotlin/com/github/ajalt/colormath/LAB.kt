package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import com.github.ajalt.colormath.internal.Illuminant.D65
import kotlin.math.pow

/**
 * CIE LAB color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * | Component  | Description | Gamut         |
 * | ---------- | ----------- | ------------- |
 * | [l]        | lightness   | `[0, 100]`    |
 * | [a]        | green/red   | `[-160, 160]` |
 * | [b]        | blue/yellow | `[-160, 160]` |
 */
data class LAB(val l: Float, val a: Float, val b: Float, override val alpha: Float = 1f) : Color {
    constructor (l: Double, a: Double, b: Double, alpha: Float = 1f)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha)

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Lab_to_XYZ.html
        if (l == 0f) return XYZ(0.0, 0.0, 0.0)

        val fy = (l + 16) / 116f
        val fz = fy - b / 200f
        val fx = a / 500f + fy

        val yr = if (l > CIE_E_times_K) fy.pow(3) else l / CIE_K
        val zr = fz.pow(3).let { if (it > CIE_E) it else (116 * fz - 16) / CIE_K }
        val xr = fx.pow(3).let { if (it > CIE_E) it else (116 * fx - 16) / CIE_K }

        return XYZ(xr * D65.x / 100f, yr * D65.y / 100f, zr * D65.z / 100f, alpha)
    }

    override fun toLCH(): LCH = toPolarModel(a, b) { c, h -> LCH(h, c, l, alpha) }

    override fun toLAB(): LAB = this

    override fun convertToThis(other: Color): LAB = other.toLAB()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, a, b, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { false }
    override fun fromComponents(components: FloatArray): LAB {
        requireComponentSize(components)
        return LAB(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
