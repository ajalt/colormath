package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*

object RGBColorSpaces {
    /**
     * The sRGB color space defined in [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val SRGB: RGBColorSpace = com.github.ajalt.colormath.SRGB

    /**
     * The Linear sRGB color space defined in [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val LINEAR_SRGB: RGBColorSpace = RGBColorSpace(
        "Linear sRGB",
        WhitePoint.D65,
        RGBColorSpace.LinearTransferFunctions,
        SRGB_R,
        SRGB_G,
        SRGB_B,
    )
}

/**
 * Create a new [RGBColorSpace] implementation with the given [name], [whitePoint], [transferFunctions], and [r][r]
 * [g][g] [b][b] primaries.
 */
fun RGBColorSpace(
    name: String,
    whitePoint: WhitePoint,
    transferFunctions: RGBColorSpace.TransferFunctions,
    r: Chromaticity,
    g: Chromaticity,
    b: Chromaticity,
): RGBColorSpace = RGBColorSpaceImpl(name, whitePoint, transferFunctions, r, g, b)

/**
 * The sRGB color space defined in [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
 */
object SRGB : RGBColorSpace {
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("RGB")
    override operator fun invoke(r: Float, g: Float, b: Float, alpha: Float): RGB = RGB(r, g, b, alpha, this)
    override fun convert(color: Color): RGB = color.toSRGB()
    override fun create(components: FloatArray): RGB = doCreate(components, ::invoke)

    override val name: String = "sRGB"
    override val whitePoint: WhitePoint = WhitePoint.D65
    override val transferFunctions: RGBColorSpace.TransferFunctions = SRGB_TRANSFER_FUNCTIONS
    override val matrixToXyz: FloatArray = rgbToXyzMatrix(whitePoint, SRGB_R, SRGB_G, SRGB_B).rowMajor
    override val matrixFromXyz: FloatArray = Matrix(matrixToXyz).inverse().rowMajor
    override fun toString(): String = name
}

private data class RGBColorSpaceImpl(
    override val name: String,
    override val whitePoint: WhitePoint,
    override val transferFunctions: RGBColorSpace.TransferFunctions,
    private val r: Chromaticity,
    private val g: Chromaticity,
    private val b: Chromaticity,
) : RGBColorSpace {
    override val components: List<ColorComponentInfo> = rectangularComponentInfo("RGB")
    override operator fun invoke(r: Float, g: Float, b: Float, alpha: Float): RGB = RGB(r, g, b, alpha, this)
    override fun convert(color: Color): RGB = if (color is RGB) color.convertTo(this) else color.toXYZ().toRGB(this)
    override fun create(components: FloatArray): RGB = doCreate(components, ::invoke)

    override val matrixToXyz: FloatArray = rgbToXyzMatrix(whitePoint, r, g, b).rowMajor
    override val matrixFromXyz: FloatArray = Matrix(matrixToXyz).inverse().rowMajor
    override fun toString(): String = name
}

private val SRGB_R = Chromaticity.from_xy(0.640f, 0.330f)
private val SRGB_G = Chromaticity.from_xy(0.300f, 0.600f)
private val SRGB_B = Chromaticity.from_xy(0.150f, 0.060f)
private val SRGB_TRANSFER_FUNCTIONS =
    RGBColorSpace.StandardTransferFunctions(1 / 1.055f, 0.055f / 1.055f, 1 / 12.92f, 0.04045f, 0f, 0f, 2.4f)


// http://www.brucelindbloom.com/Eqn_RGB_XYZ_Matrix.html
private fun rgbToXyzMatrix(whitePoint: WhitePoint, r: Chromaticity, g: Chromaticity, b: Chromaticity): Matrix {
    val m = Matrix(
        r.x, g.x, b.x,
        r.y, g.y, b.y,
        r.z, g.z, b.z,
    ).inverse(inPlace = true)
    m.times(whitePoint.chromaticity.x, whitePoint.chromaticity.y, whitePoint.chromaticity.z) { Sr, Sg, Sb ->
        m[0, 0] = Sr * r.x
        m[1, 0] = Sg * g.x
        m[2, 0] = Sb * b.x

        m[0, 1] = Sr * r.y
        m[1, 1] = Sg * g.y
        m[2, 1] = Sb * b.y

        m[0, 2] = Sr * r.z
        m[1, 2] = Sg * g.z
        m[2, 2] = Sb * b.z
    }
    return m
}
