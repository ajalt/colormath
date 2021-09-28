package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.Illuminant
import com.github.ajalt.colormath.internal.Matrix
import com.github.ajalt.colormath.internal.dot
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.RGBColorSpaces.SRGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ65
import kotlin.native.concurrent.SharedImmutable

/**
 * Create a chromatic adapter that will adapt colors from a given [sourceWhite] to this color space's
 * [reference white][RGBColorSpace.whitePoint]
 */
fun RGBColorSpace.createChromaticAdapter(sourceWhite: Color): ChromaticAdapterRGB {
    return createChromaticAdapter(sourceWhite.toXYZ().toCIExyY())
}

/**
 * Create a chromatic adapter that will adapt colors from a given [sourceWhite] to this color space's
 * [reference white][RGBColorSpace.whitePoint]
 */
fun RGBColorSpace.createChromaticAdapter(sourceWhite: xyY): ChromaticAdapterRGB {
    val xyzTransform = XYZColorSpace(whitePoint).chromaticAdaptationMatrix(sourceWhite)
    return ChromaticAdapterRGB(this, xyzToSrgb.dot(xyzTransform).dot(srgbToXYZ))
}

/** Create a chromatic adapter that will adapt [RGBInt] colors from a given [sourceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(sourceWhite: Color): ChromaticAdapterRGBInt {
    return createChromaticAdapter(sourceWhite.toXYZ().toCIExyY())
}

/** Create a chromatic adapter that will adapt [RGBInt] colors from a given [sourceWhite] to [D65][Illuminant.D65] */
fun RGBInt.Companion.createChromaticAdapter(sourceWhite: xyY): ChromaticAdapterRGBInt {
    val xyzTransform = XYZ65.chromaticAdaptationMatrix(sourceWhite)
    return ChromaticAdapterRGBInt(xyzToSrgb.dot(xyzTransform).dot(srgbToXYZ))
}

class ChromaticAdapterRGB internal constructor(private val space: RGBColorSpace, private val transform: Matrix) {
    /** Adapt an sRGB [color] to this white point */
    fun adapt(color: RGB): RGB {
        return doAdapt(transform, color.r, color.g, color.b) { r, g, b ->
            space(r, g, b, color.alpha)
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
    return transform.dot(f.eotf(r), f.eotf(g), f.eotf(b)) { rr, gg, bb ->
        block(f.oetf(rr), f.oetf(gg), f.oetf(bb))
    }
}

@SharedImmutable
private val xyzToSrgb = Matrix(SRGB.matrixFromXyz)

@SharedImmutable
private val srgbToXYZ = Matrix(SRGB.matrixToXyz)
