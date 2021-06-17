package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.requireComponentSize
import kotlin.math.pow

/**
 * Linear sRGB color space.
 *
 * This color space removes the nonlinearity present in [RGB].
 */
data class LinearRGB(val r: Float, val g: Float, val b: Float, val a: Float = 1f) : Color {
    constructor(r: Double, g: Double, b: Double, a: Float = 1f)
            : this(r.toFloat(), g.toFloat(), b.toFloat(), a)

    override val alpha: Float get() = a

    override fun toXYZ(): XYZ = linearRGBToXYZ(r, g, b, alpha)
    override fun toRGB(): RGB = RGB(linearToSRGB(r), linearToSRGB(g), linearToSRGB(b), a)
    override fun toLinearRGB(): LinearRGB = this

    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(r, g, b, alpha)
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
