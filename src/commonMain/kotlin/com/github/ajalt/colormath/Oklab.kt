package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.rectangularComponentInfo
import com.github.ajalt.colormath.internal.toPolarModel

/**
 * The Oklab color space: a perceptual color space for image processing.
 *
 * This color space is always calculated relative to [Illuminant.D65].
 *
 * | Component  | Description | Range     |
 * | ---------- | ----------- | --------- |
 * | [l]        | lightness   | `[0, 1]`  |
 * | [a]        | green-red   | `[-1, 1]` |
 * | [b]        | blue-yellow | `[-1, 1]` |
 */
data class Oklab(val l: Float, val a: Float, val b: Float, override val alpha: Float = Float.NaN) : Color {
    /** Default constructors for the [Oklab] color model. */
    companion object : ColorSpace<Oklab> {
        override val name: String get() = "Oklab"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("LAB")
        override fun convert(color: Color): Oklab = color.toOklab()
        override fun create(components: FloatArray): Oklab = doCreate(components, ::Oklab)
    }

    constructor (l: Number, a: Number, b: Number, alpha: Number = Float.NaN)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    override val space: ColorSpace<Oklab> get() = Oklab

    // https://bottosson.github.io/posts/oklab/#converting-from-linear-srgb-to-oklab
    override fun toSRGB(): RGB = calculateConeResponse { l, m, s ->
        val r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s
        val g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s
        val b = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
        val f = RGB.transferFunctions
        return RGB(f.oetf(r.toFloat()), f.oetf(g.toFloat()), f.oetf(b.toFloat()), alpha)
    }

    // https://bottosson.github.io/posts/oklab/#converting-from-xyz-to-oklab
    // Note that Ottosson doesn't provide values for M₂⁻¹, so they were calculated with `numpy.linalg.inv`
    // and truncated to the same precision as used by Ottosson
    override fun toXYZ(): XYZ = calculateConeResponse { l, m, s ->
        return XYZ(
            x = +1.2270138511 * l - 0.5577999807 * m + 0.2812561490 * s,
            y = -0.0405801784 * l + 1.1122568696 * m - 0.0716766787 * s,
            z = -0.0763812845 * l - 0.4214819784 * m + 1.5861632204 * s,
            alpha = alpha
        )
    }

    private inline fun <T> calculateConeResponse(block: (l: Double, m: Double, s: Double) -> T): T {
        val ll = l + 0.3963377774 * a + 0.2158037573 * b
        val mm = l - 0.1055613458 * a - 0.0638541728 * b
        val ss = l - 0.0894841775 * a - 1.2914855480 * b

        val l = ll * ll * ll
        val m = mm * mm * mm
        val s = ss * ss * ss

        return block(l, m, s)
    }

    override fun toOklch(): Oklch = toPolarModel(a, b) { c, h -> Oklch(l, c, h, alpha) }
    override fun toOklab(): Oklab = this
    override fun toArray(): FloatArray = floatArrayOf(l, a, b, alpha)
}
