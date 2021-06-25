package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.withValidCIndex
import kotlin.math.pow

/**
 * Linear sRGB color space.
 *
 * This color space removes the nonlinearity present in [RGB].
 *
 * | Component  | Description | Gamut    |
 * | ---------- | ----------- | -------- |
 * | [r]        | red         | `[0, 1]` |
 * | [g]        | green       | `[0, 1]` |
 * | [b]        | blue        | `[0, 1]` |
 */
data class LinearRGB(val r: Float, val g: Float, val b: Float, val a: Float = 1f) : Color {
    constructor(r: Double, g: Double, b: Double, a: Float = 1f)
            : this(r.toFloat(), g.toFloat(), b.toFloat(), a)

    override val alpha: Float get() = a

    // https://bottosson.github.io/posts/oklab/#converting-from-linear-srgb-to-oklab
    override fun toOklab(): Oklab {
        val l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b
        val m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b
        val s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b

        val ll = l.pow(1.0 / 3.0)
        val mm = m.pow(1.0 / 3.0)
        val ss = s.pow(1.0 / 3.0)

        return Oklab(
            l = 0.2104542553f * ll + 0.7936177850f * mm - 0.0040720468f * ss,
            a = 1.9779984951f * ll - 2.4285922050f * mm + 0.4505937099f * ss,
            b = 0.0259040371f * ll + 0.7827717662f * mm - 0.8086757660f * ss,
            alpha = a
        )
    }

    override fun toXYZ(): XYZ = linearRGBToXYZ(r, g, b, alpha)
    override fun toRGB(): RGB = RGB(linearToSRGB(r), linearToSRGB(g), linearToSRGB(b), a)
    override fun toLinearRGB(): LinearRGB = this

    override fun convertToThis(other: Color): LinearRGB = other.toLinearRGB()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(r, g, b, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { false }
    override fun fromComponents(components: FloatArray): LinearRGB {
        requireComponentSize(components)
        return LinearRGB(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}

internal fun linearRGBToXYZ(r: Float, g: Float, b: Float, alpha: Float): XYZ {
    // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
    val x = 0.4124564 * r + 0.3575761 * g + 0.1804375 * b
    val y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b
    val z = 0.0193339 * r + 0.1191920 * g + 0.9503041 * b
    return XYZ(x, y, z, alpha)
}

// http://entropymine.com/imageworsener/srgbformula/
internal fun linearToSRGB(v: Float): Float {
    return when {
        v <= 0.0031308 -> v * 12.92f
        else -> 1.055f * v.pow(1 / 2.4f) - 0.055f
    }
}

internal fun sRGBToLinear(v: Float): Float {
    return when {
        v <= 0.04045f -> v / 12.92f
        else -> ((v + 0.055f) / 1.055f).pow(2.4f)
    }
}
