package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.internal.Matrix
import com.github.ajalt.colormath.internal.dot
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBColorSpace
import com.github.ajalt.colormath.model.XYZColorSpace
import com.github.ajalt.colormath.model.chromaticAdaptationMatrix


/**
 * A converter that transforms [RGB] colors from one [RGBColorSpace] to another.
 *
 * You can also convert a color directly with [RGB.convertTo], but using a converter is more efficient
 * if you need to convert more than one color at a time.
 */
interface RGBToRGBConverter {
    fun convert(rgb: RGB): RGB
}

/**
 * Create an [RGBToRGBConverter] that transforms [RGB] colors from this [RGBColorSpace] to the [destination] space.
 */
fun RGBColorSpace.converterTo(destination: RGBColorSpace): RGBToRGBConverter {
    return RGBToRGBConverterImpl(this, destination, rgbToRgbMatrix(this, destination))
}

private class RGBToRGBConverterImpl(
    private val src: RGBColorSpace,
    private val dst: RGBColorSpace,
    private val transform: Matrix,
) : RGBToRGBConverter {
    override fun convert(rgb: RGB): RGB {
        require(rgb.space == src) { "invalid rgb space: ${rgb.space}, expected $src" }
        val fsrc = src.transferFunctions
        val fdst = dst.transferFunctions
        return transform.dot(fsrc.eotf(rgb.r), fsrc.eotf(rgb.g), fsrc.eotf(rgb.b)) { rr, gg, bb ->
            dst(fdst.oetf(rr), fdst.oetf(gg), fdst.oetf(bb))
        }
    }
}

internal fun rgbToRgbMatrix(src: RGBColorSpace, dst: RGBColorSpace): Matrix {
    return if (src.whitePoint == dst.whitePoint) {
        Matrix(dst.matrixFromXyz).dot(Matrix(src.matrixToXyz))
    } else {
        val adaptation =
            XYZColorSpace(dst.whitePoint).chromaticAdaptationMatrix(src.whitePoint.chromaticity)
        Matrix(dst.matrixFromXyz).dot(adaptation).dot(Matrix(src.matrixToXyz))
    }
}
