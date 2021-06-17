package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.CIE_E
import com.github.ajalt.colormath.internal.CIE_E_times_K
import com.github.ajalt.colormath.internal.CIE_K
import com.github.ajalt.colormath.internal.Illuminant.D65
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.radToDeg
import com.github.ajalt.colormath.internal.requireComponentSize
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * CIE LAB color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [l] is a percentage, typically in the range `[0, 100]`, but can exceed 100 (e.g. for HDR systems).
 * [a] and [b] are unbounded, but are typically the range `[-128, 127]`.
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

    override fun toLCH(): LCH {
        // https://www.w3.org/TR/css-color-4/#lab-to-lch
        val c = sqrt(a * a + b * b)
        val h = if (c < 1e-8) 0f else atan2(b, a).radToDeg()
        return LCH(l, c, h.normalizeDeg())
    }

    override fun toLAB(): LAB = this

    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, a, b, alpha)
    override fun fromComponents(components: FloatArray): LAB {
        requireComponentSize(components)
        return LAB(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
