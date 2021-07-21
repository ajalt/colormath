package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.Matrix
import com.github.ajalt.colormath.internal.inverse
import com.github.ajalt.colormath.internal.times

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

/** Apply this adaptation in-place to all `argb` ints in an array of [colors] */
fun ChromaticAdapter<RGBInt>.adaptAll(colors: IntArray) {
    for (i in colors.indices) {
        colors[i] = adapt(RGBInt(colors[i].toUInt())).argb.toInt()
    }
}

private class SRGBChromaticAdapter(private val transform: Matrix) : ChromaticAdapter<RGB> {
    override fun adapt(color: RGB): RGB {
        return doAdapt(transform, color.r, color.g, color.b) { r, g, b ->
            RGB(r, g, b, color.alpha)
        }
    }
}

private class RGBIntChromaticAdapter(private val transform: Matrix) : ChromaticAdapter<RGBInt> {
    override fun adapt(color: RGBInt): RGBInt {
        return doAdapt(transform, color.redFloat, color.greenFloat, color.blueFloat) { r, g, b ->
            RGBInt(r, g, b, color.alpha)
        }
    }
}

private inline fun <T> doAdapt(transform: Matrix, r: Float, g: Float, b: Float, block: (Float, Float, Float) -> T): T {
    return transform.times(
        sRGBToLinear(r),
        sRGBToLinear(g),
        sRGBToLinear(b)
    ) { rr, gg, bb ->
        block(linearToSRGB(rr), linearToSRGB(gg), linearToSRGB(bb))
    }
}

private val srgbToXYZ = Matrix(
    0.4124564f, 0.3575761f, 0.1804375f,
    0.2126729f, 0.7151522f, 0.0721750f,
    0.0193339f, 0.1191920f, 0.9503041f,
)

private val xyzToSrgb = srgbToXYZ.inverse()
