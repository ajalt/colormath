package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.*

/**
 * A `ChromaticAdapter` can be used to efficiently color balance multiple colors to the same white point.
 *
 * Create one with [createChromaticAdapter].
 */
interface ChromaticAdapter<T : Color> {
    fun adapt(color: T): T
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapter<RGB> {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Illuminant(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Illuminant): ChromaticAdapter<RGB> {
    val xyzTransform = Matrix(XYZ(Illuminant.D65).chromaticAdaptationMatrix(referenceWhite))
    return SRGBChromaticAdapter(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapter<RGBInt> {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Illuminant(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Illuminant): ChromaticAdapter<RGBInt> {
    val xyzTransform = Matrix(XYZ(Illuminant.D65).chromaticAdaptationMatrix(referenceWhite))
    return RGBIntChromaticAdapter(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

private abstract class BaseRGBChromaticAdapter<T: Color>(
    private val transform: Matrix
) : ChromaticAdapter<T> {
    protected val v = Vector(0f, 0f, 0f)
    protected fun doAdapt(r: Float, g: Float, b: Float) {
        v[0] = sRGBToLinear(r)
        v[1] = sRGBToLinear(g)
        v[2] = sRGBToLinear(b)
        transform.multiplyInPlace(v)
        v[0] = linearToSRGB(v[0])
        v[1] = linearToSRGB(v[1])
        v[2] = linearToSRGB(v[2])
    }
}
private class SRGBChromaticAdapter(transform: Matrix) : BaseRGBChromaticAdapter<RGB>(transform) {
    override fun adapt(color: RGB): RGB {
        doAdapt(color.r, color.g, color.b)
        return RGB(v[0], v[1], v[2], color.alpha)
    }
}

private class RGBIntChromaticAdapter(transform: Matrix) : BaseRGBChromaticAdapter<RGBInt>(transform) {
    override fun adapt(color: RGBInt): RGBInt {
        doAdapt(color.redFloat, color.greenFloat, color.blueFloat)
        return RGBInt(v[0], v[1], v[2], color.alpha)
    }
}

private val srgbToXYZ = Matrix(
    0.4124564f, 0.3575761f, 0.1804375f,
    0.2126729f, 0.7151522f, 0.0721750f,
    0.0193339f, 0.1191920f, 0.9503041f,
)

private val xyzToSrgb = srgbToXYZ.inverse()
