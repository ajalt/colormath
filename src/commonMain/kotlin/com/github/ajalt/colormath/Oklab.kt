package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.requireComponentSize
import com.github.ajalt.colormath.internal.toPolarModel
import com.github.ajalt.colormath.internal.withValidCIndex

/**
 * Oklab color space.
 *
 * Learn more: https://bottosson.github.io/posts/oklab/
 */
data class Oklab(val l: Float, val a: Float, val b: Float, override val alpha: Float = 1f) : Color {
    constructor (l: Double, a: Double, b: Double, alpha: Double = 1.0)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    constructor (l: Double, a: Double, b: Double, alpha: Float)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha)

    override fun toRGB(): RGB = toLinearRGB().toRGB()

    // https://bottosson.github.io/posts/oklab/#converting-from-xyz-to-oklab
    // Note that Ottosson doesn't provide values for M₂⁻¹, so they were calculated with `numpy.linalg.inv`
    // and truncated to the same precision as used by Ottosson
    override fun toXYZ(): XYZ = calculateConeResponse { l, m, s ->
        return XYZ(
            x = +1.2270138511 * l - 0.5577999807 * m + 0.2812561490 * s,
            y = -0.0405801784 * l + 1.1122568696 * m - 0.0716766787 * s,
            z = -0.0763812845 * l - 0.4214819784 * m + 1.5861632204 * s,
            a = alpha
        )
    }

    // https://bottosson.github.io/posts/oklab/#converting-from-linear-srgb-to-oklab
    override fun toLinearRGB(): LinearRGB = calculateConeResponse { l, m, s ->
        return LinearRGB(
            r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
            g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
            b = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
            a = alpha
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
    override fun convertToThis(other: Color): Oklab = other.toOklab()
    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(l, a, b, alpha)
    override fun componentIsPolar(i: Int): Boolean = withValidCIndex(i) { false }
    override fun fromComponents(components: FloatArray): Oklab {
        requireComponentSize(components)
        return Oklab(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}
