package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import kotlin.math.log2
import kotlin.math.pow

object RGBColorSpaces {
    /**
     * sRGB color space
     *
     * ### References
     * - [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val SRGB: RGBColorSpace = com.github.ajalt.colormath.SRGB

    /**
     * Linear sRGB color space
     *
     * ### References
     * - [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val LINEAR_SRGB: RGBColorSpace = RGBColorSpace(
        "Linear sRGB",
        WhitePoint.D65,
        RGBColorSpace.LinearTransferFunctions,
        SRGB_R,
        SRGB_G,
        SRGB_B,
    )

    /**
     * ACES2065-1, a digital color image encoding appropriate for both photographed and computer-generated images.
     *
     * ### References
     * - [SMPTE ST 2065-1][https://ieeexplore.ieee.org/document/7289895]
     * - [Academy TB-2014-004][https://github.com/ampas/aces-docs]
     */
    val ACES: RGBColorSpace = RGBColorSpace(
        "ACES2065-1",
        ACES_WHITE_POINT,
        RGBColorSpace.LinearTransferFunctions,
        ACES_AP0_R,
        ACES_AP0_G,
        ACES_AP0_B,
    )

    /**
     * ACEScc, a logarithmic encoding of [ACES] data intended for use in color grading systems whose controls
     * expect a log relationship to relative scene exposures for proper operation.
     *
     * ### References
     * - [Academy S-2014-003][https://github.com/ampas/aces-docs]
     */
    val ACEScc: RGBColorSpace = RGBColorSpace(
        "ACEScc",
        ACES_WHITE_POINT,
        ACESccTransferFunctions,
        ACES_AP1_R,
        ACES_AP1_G,
        ACES_AP1_B,
    )

    /**
     * ACEScct, a quasi-logarithmic encoding of [ACES] data intended for use in color grading systems whose
     * controls expect a log relationship to relative scene exposures for proper operation.
     *
     * ### References
     * - [Academy S-2016-001][https://github.com/ampas/aces-docs]
     */
    val ACEScct: RGBColorSpace = RGBColorSpace(
        "ACEScc",
        ACES_WHITE_POINT,
        ACEScctTransferFunctions,
        ACES_AP1_R,
        ACES_AP1_G,
        ACES_AP1_B,
    )

    /**
     * ACEScg, a working space for CGI render and compositing to be used in conjunction with the [ACES] system.
     *
     * ### References
     * - [Academy S-2014-004][https://github.com/ampas/aces-docs]
     */
    val ACEScg: RGBColorSpace = RGBColorSpace(
        "ACEScg",
        ACES_WHITE_POINT,
        RGBColorSpace.LinearTransferFunctions,
        ACES_AP1_R,
        ACES_AP1_G,
        ACES_AP1_B,
    )

    /**
     * Adobe RGB 1998 color space
     *
     * ### References
     * - [Adobe RGB (1998) Color Image Encoding](https://www.adobe.com/digitalimag/pdfs/AdobeRGB1998.pdf)
     */
    val ADOBE_RGB: RGBColorSpace = RGBColorSpace(
        "Adobe RGB",
        WhitePoint.D65,
        RGBColorSpace.GammaTransferFunctions(2.19921875),
        Chromaticity.from_xy(0.64, 0.33),
        Chromaticity.from_xy(0.21, 0.71),
        Chromaticity.from_xy(0.15, 0.06),
    )

    /**
     * ITU-R Recommendation BT.2020 color space, also known as BT.2020 or REC.2020
     *
     * The transfer functions in this implementation use the constants for 12-bit systems given in the standard.
     *
     * ### References
     * - [ITU-R BT.2020-2](https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2020-2-201510-I!!PDF-E.pdf)
     */
    val BT_2020: RGBColorSpace = RGBColorSpace(
        "BT.2020",
        WhitePoint.D65,
        BT2020TransferFunctions,
        Chromaticity.from_xy(0.708, 0.292),
        Chromaticity.from_xy(0.170, 0.797),
        Chromaticity.from_xy(0.131, 0.046),
    )

    /**
     * ITU-R Recommendation BT.709, also known as BT.709 or REC.709
     *
     * ### References
     * - [ITU-R BT.709-9](https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf)
     */
    val BT_709: RGBColorSpace = RGBColorSpace(
        "BT.709",
        WhitePoint.D65,
        BT709TransferFunctions,
        Chromaticity.from_xy(0.6400, 0.3300),
        Chromaticity.from_xy(0.3000, 0.6000),
        Chromaticity.from_xy(0.1500, 0.0600),
    )

    /**
     * DCI P3 color space
     *
     * ### References
     * - [RP 431-2:2011](https://ieeexplore.ieee.org/document/7290729)
     * - [Digital Cinema System Specification - Version 1.1](https://www.dcimovies.com/archives/spec_v1_1/DCI_DCinema_System_Spec_v1_1.pdf)
     */
    val DCI_P3: RGBColorSpace = RGBColorSpace(
        "DCI P3",
        WhitePoint("DCI P3", Chromaticity(0.314f, 0.351f)),
        RGBColorSpace.GammaTransferFunctions(2.6),
        Chromaticity.from_xy(0.680, 0.320),
        Chromaticity.from_xy(0.265, 0.690),
        Chromaticity.from_xy(0.150, 0.060),
    )

    /**
     * Display P3 color space
     *
     * ### References
     * - [Apple](https://developer.apple.com/documentation/coregraphics/cgcolorspace/1408916-displayp3)
     * - [RP 431-2:2011](https://ieeexplore.ieee.org/document/7290729)
     * - [Digital Cinema System Specification - Version 1.1](https://www.dcimovies.com/archives/spec_v1_1/DCI_DCinema_System_Spec_v1_1.pdf)
     */
    val DISPLAY_P3: RGBColorSpace = RGBColorSpace(
        "Display P3",
        WhitePoint.D65,
        SRGBTransferFunctions,
        Chromaticity.from_xy(0.680, 0.320),
        Chromaticity.from_xy(0.265, 0.690),
        Chromaticity.from_xy(0.150, 0.060),
    )

    /**
     * ROMM RGB color space, also known as ProPhoto RGB
     *
     * ### References
     * - [ANSI/I3A IT10.7666:2003](https://www.color.org/ROMMRGB.pdf)
     */
    val ROMM_RGB: RGBColorSpace = RGBColorSpace(
        "ROMM RGB",
        WhitePoint.D50,
        ROMMTransferFunctions,
        Chromaticity.from_xy(0.7347, 0.2653),
        Chromaticity.from_xy(0.1596, 0.8404),
        Chromaticity.from_xy(0.0366, 0.0001),
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
    override val transferFunctions: RGBColorSpace.TransferFunctions = SRGBTransferFunctions
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

private object SRGBTransferFunctions : RGBColorSpace.TransferFunctions {
    override fun oetf(x: Double): Double {
        return when {
            x <= 0.0031308 -> x * 12.92
            else -> 1.055 * x.spow(1 / 2.4) - 0.055
        }
    }

    override fun eotf(x: Double): Double {
        return when {
            x <= 0.04045 -> x / 12.92
            else -> ((x + 0.055) / 1.055).spow(2.4)
        }
    }
}

private val ACES_WHITE_POINT = WhitePoint("ACES", Chromaticity(0.95265f, 1.00883f))

// values from [Academy TB-2014-004]
private val ACES_AP0_R = Chromaticity.from_xy(0.73470, 0.26530)
private val ACES_AP0_G = Chromaticity.from_xy(0.00000, 1.00000)
private val ACES_AP0_B = Chromaticity.from_xy(0.00010, -0.0770)

// values from [Academy S-2014-004]
private val ACES_AP1_R = Chromaticity.from_xy(0.713, 0.293)
private val ACES_AP1_G = Chromaticity.from_xy(0.165, 0.830)
private val ACES_AP1_B = Chromaticity.from_xy(0.128, 0.044)

// from [Academy S-2014-003]
private object ACESccTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val twoN15 = 1 / 32768.0 // == 2.pow(-15)
    private const val twoN16 = 1 / 65536.0 // == 2.pow(-16)
    private const val eotfC1 = (9.72 - 15) / 17.52
    private val eotfC2 = (log2(65504.0) + 9.72) / 17.52
    override fun eotf(x: Double): Double {
        return when {
            x <= eotfC1 -> (2.0.spow(x * 17.52 - 9.72) - twoN16) * 2.0
            x < eotfC2 -> 2.0.pow(x * 17.52 - 9.72)
            else -> 65504.0
        }
    }

    override fun oetf(x: Double): Double {
        return when {
            x < twoN15 -> (log2(twoN16 + x.coerceAtLeast(0.0) / 2) + 9.72) / 17.52
            else -> (log2(x) + 9.72) / 17.52
        }
    }
}

// from [Academy S-2016-001]
private object ACEScctTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val a = 10.5402377416545
    private const val b = 0.0729055341958355
    private val eotfC2 = (log2(65504.0) + 9.72) / 17.52
    override fun eotf(x: Double): Double {
        return when {
            x <= 0.155251141552511 -> (x - b) / a
            x < eotfC2 -> 2.0.pow(x * 17.52 - 9.72)
            else -> 65504.0
        }
    }

    override fun oetf(x: Double): Double {
        return when {
            x < 0.0078125 -> a * x + b
            else -> (log2(x) + 9.72) / 17.52
        }
    }
}

private object BT709TransferFunctions : RGBColorSpace.TransferFunctions {
    private val eotfC = 1.099 * 0.018.spow(0.45) - 0.099
    override fun eotf(x: Double): Double = when {
        x < eotfC -> x / 4.5f
        else ->  ((x + 0.099) / 1.099).spow(1 / 0.45)
    }

    override fun oetf(x: Double): Double = when {
        x < 0.018 -> 4.5 * x
        else -> 1.099 * x.spow(0.45) - 0.099
    }
}

private object BT2020TransferFunctions : RGBColorSpace.TransferFunctions {
    // The standard defines the constants with different precision for 10 and 12 bit systems.
    // We use the 12 bit constants.
    private const val a = 1.0993
    private const val b = 0.0181
    private val eotfCutoff = a * b.pow(0.45) - (a - 1) // == oetf(b)
    override fun eotf(x: Double): Double = when {
        x < eotfCutoff -> x / 4.5f
        else -> ((x + (a - 1)) / a).spow(1 / 0.45)
    }

    override fun oetf(x: Double): Double = when {
        x < b -> 4.5 * x
        else -> a * x.spow(0.45) - (a - 1)
    }
}

private object ROMMTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val c = 0.001953
    override fun eotf(x: Double): Double = when {
        x < 16 * c -> x / 16.0
        else -> x.spow(1.8)
    }

    override fun oetf(x: Double): Double = when {
        x < c -> x * 16.0
        else -> x.spow(1.0 / 1.8)
    }
}

// [SMPTE RP 177-1993](http://car.france3.mars.free.fr/Formation%20INA%20HD/HDTV/HDTV%20%202007%20v35/SMPTE%20normes%20et%20confs/rp177.pdf)
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