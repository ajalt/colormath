package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.RGBColorSpaces.SRGB
import com.github.ajalt.colormath.internal.Matrix
import com.github.ajalt.colormath.internal.times

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][WhitePoint.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapterRGB {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Chromaticity(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][WhitePoint.D65] */
fun RGB.Companion.createChromaticAdapter(referenceWhite: Chromaticity): ChromaticAdapterRGB {
    val xyzTransform = Matrix(XYZ(WhitePoint.D65).chromaticAdaptationMatrix(referenceWhite))
    return ChromaticAdapterRGB(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][WhitePoint.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Color): ChromaticAdapterRGBInt {
    val (x, y, z) = referenceWhite.toXYZ()
    return createChromaticAdapter(Chromaticity(x, y, z))
}

/** Create a chromatic adapter that will adapt colors with a given [referenceWhite] to [D65][WhitePoint.D65] */
fun RGBInt.Companion.createChromaticAdapter(referenceWhite: Chromaticity): ChromaticAdapterRGBInt {
    val xyzTransform = Matrix(XYZ(WhitePoint.D65).chromaticAdaptationMatrix(referenceWhite))
    return ChromaticAdapterRGBInt(xyzToSrgb.times(xyzTransform).times(srgbToXYZ))
}

class ChromaticAdapterRGB internal constructor(private val transform: Matrix) {
    /** Adapt an sRGB [color] to this white point */
    fun adapt(color: RGB): RGB {
        return doAdapt(transform, color.r, color.g, color.b) { r, g, b ->
            RGB(r, g, b, color.alpha)
        }
    }
}

class ChromaticAdapterRGBInt internal constructor(private val transform: Matrix) {
    /** Adapt an sRGB [color] to this white point */
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
    val f = SRGB.transferFunctions
    return transform.times(f.eotf(r), f.eotf(g), f.eotf(b)) { rr, gg, bb ->
        block(f.oetf(rr), f.oetf(gg), f.oetf(bb))
    }
}

private val xyzToSrgb = Matrix(SRGB.matrixFromXyz)
private val srgbToXYZ = Matrix(SRGB.matrixToXyz)
