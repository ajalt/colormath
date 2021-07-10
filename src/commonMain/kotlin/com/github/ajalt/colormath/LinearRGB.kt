package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.withValidComps
import kotlin.math.pow

/**
 * Linear-light sRGB color space.
 *
 * | Component  | Description | Range    |
 * | ---------- | ----------- | -------- |
 * | [r]        | red         | `[0, 1]` |
 * | [g]        | green       | `[0, 1]` |
 * | [b]        | blue        | `[0, 1]` |
 */
data class LinearRGB(val r: Float, val g: Float, val b: Float, override val alpha: Float = 1f) : Color {
    companion object : ColorModel<LinearRGB> {
        override val name: String get() = "LinearRGB"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("R", false),
            ColorComponentInfo("G", false),
            ColorComponentInfo("B", false),
        )

        override fun convert(color: Color): LinearRGB = color.toLinearRGB()
        override fun create(components: FloatArray): LinearRGB = withValidComps(components) {
            LinearRGB(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor(r: Double, g: Double, b: Double, alpha: Double)
            : this(r.toFloat(), g.toFloat(), b.toFloat(), alpha.toFloat())

    constructor(r: Double, g: Double, b: Double, alpha: Float = 1f)
            : this(r.toFloat(), g.toFloat(), b.toFloat(), alpha)

    override val model: ColorModel<LinearRGB> get() = LinearRGB

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
            alpha = alpha
        )
    }

    override fun toXYZ(): XYZ = linearRGBToXYZ(r, g, b, alpha)
    override fun toRGB(): RGB = RGB(linearToSRGB(r), linearToSRGB(g), linearToSRGB(b), alpha)
    override fun toLinearRGB(): LinearRGB = this
    override fun toArray(): FloatArray = floatArrayOf(r, g, b, alpha)
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
