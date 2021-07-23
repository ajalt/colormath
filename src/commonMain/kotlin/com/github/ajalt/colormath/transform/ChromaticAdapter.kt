package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.Matrix
import com.github.ajalt.colormath.internal.inverse
import com.github.ajalt.colormath.internal.times

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapterRGB {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Illuminant(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Illuminant): ChromaticAdapterRGB {
    val xyzTransform = Matrix(XYZ(Illuminant.D65).chromaticAdaptationMatrix(referenceWhite))
    return ChromaticAdapterRGB(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapterRGBInt {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Illuminant(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Illuminant): ChromaticAdapterRGBInt {
    val xyzTransform = Matrix(XYZ(Illuminant.D65).chromaticAdaptationMatrix(referenceWhite))
    return ChromaticAdapterRGBInt(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

class ChromaticAdapterRGB internal constructor(private val transform: Matrix) {
    /** Adapt a [color] to this white point */
    fun adapt(color: RGB): RGB {
        return doAdapt(transform, color.r, color.g, color.b) { r, g, b ->
            RGB(r, g, b, color.alpha)
        }
    }
}

class ChromaticAdapterRGBInt internal constructor(private val transform: Matrix) {
    /** Adapt a [color] to this white point */
    fun adapt(color: RGBInt): RGBInt {
        return doAdapt(transform, color.redFloat, color.greenFloat, color.blueFloat) { r, g, b ->
            RGBInt(r, g, b, color.alpha)
        }
    }

    /** Apply this adaptation in-place to all `argb` integers in an array of [colors] */
    fun adaptAll(colors: IntArray) {
        for (i in colors.indices) {
            colors[i] = adapt(RGBInt(colors[i].toUInt())).argb.toInt()
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
