package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.Illuminant
import com.github.ajalt.colormath.WhitePoint
import com.github.ajalt.colormath.internal.*
import kotlin.math.log2
import kotlin.math.pow

private val SRGB_R = xyY(0.6400, 0.3300)
private val SRGB_G = xyY(0.3000, 0.6000)
private val SRGB_B = xyY(0.1500, 0.0600)

object RGBColorSpaces {
    /**
     * sRGB color space
     *
     * ### References
     * - [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val SRGB: RGBColorSpace = RGBColorSpaceImpl(
        "sRGB",
        Illuminant.D65,
        SRGBTransferFunctions,
        SRGB_R,
        SRGB_G,
        SRGB_B,
    ) { it.toSRGB() }


    /**
     * Linear sRGB color space
     *
     * ### References
     * - [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
     */
    val LinearSRGB: RGBColorSpace = RGBColorSpace(
        "Linear sRGB",
        Illuminant.D65,
        RGBColorSpace.LinearTransferFunctions,
        SRGB_R,
        SRGB_G,
        SRGB_B,
    )

    /**
     * ACES2065-1, a digital color image encoding appropriate for both photographed and computer-generated images.
     *
     * ### References
     * - [SMPTE ST 2065-1](https://ieeexplore.ieee.org/document/7289895)
     * - [Academy TB-2014-004](https://github.com/ampas/aces-docs)
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
     * - [Academy S-2014-003](https://github.com/ampas/aces-docs)
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
     * - [Academy S-2016-001](https://github.com/ampas/aces-docs)
     */
    val ACEScct: RGBColorSpace = RGBColorSpace(
        "ACEScct",
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
     * - [Academy S-2014-004](https://github.com/ampas/aces-docs)
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
     * The CSS Color Module 4 calls this space `a98-rgb`.
     *
     * ### References
     * - [Adobe RGB (1998) Color Image Encoding](https://www.adobe.com/digitalimag/pdfs/AdobeRGB1998.pdf)
     */
    val AdobeRGB: RGBColorSpace = RGBColorSpace(
        "Adobe RGB",
        Illuminant.D65,
        RGBColorSpace.GammaTransferFunctions(2.19921875),
        xyY(0.64, 0.33),
        xyY(0.21, 0.71),
        xyY(0.15, 0.06),
    )

    /**
     * ITU-R Recommendation BT.2020 color space, also known as BT.2020 or REC.2020
     *
     * The transfer functions in this implementation use the constants for 12-bit systems given in the standard.
     *
     * The CSS Color Module 4 calls this space `rec2020`.
     *
     * ### References
     * - [ITU-R BT.2020-2](https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2020-2-201510-I!!PDF-E.pdf)
     */
    val BT2020: RGBColorSpace = RGBColorSpaceImpl(
        "BT.2020",
        Illuminant.D65,
        BT2020TransferFunctions,
        xyY(0.708, 0.292),
        xyY(0.170, 0.797),
        xyY(0.131, 0.046),
    ) { color ->
        when (color) {
            is RGB -> color.convertTo(this)
            is ICtCp -> color.toBT2020()
            else -> color.toXYZ().toRGB(this)
        }
    }

    /**
     * ITU-R Recommendation BT.709, also known as BT.709 or REC.709
     *
     * ### References
     * - [ITU-R BT.709-9](https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf)
     */
    val BT709: RGBColorSpace = RGBColorSpace(
        "BT.709",
        Illuminant.D65,
        BT709TransferFunctions,
        xyY(0.6400, 0.3300),
        xyY(0.3000, 0.6000),
        xyY(0.1500, 0.0600),
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
        WhitePoint("DCI P3", xyY(0.314, 0.351)),
        RGBColorSpace.GammaTransferFunctions(2.6),
        xyY(0.680, 0.320),
        xyY(0.265, 0.690),
        xyY(0.150, 0.060),
    )

    /**
     * Display P3 color space
     *
     * The CSS Color Module 4 calls this space `display-p3`.
     *
     * ### References
     * - [Apple](https://developer.apple.com/documentation/coregraphics/cgcolorspace/1408916-displayp3)
     * - [RP 431-2:2011](https://ieeexplore.ieee.org/document/7290729)
     * - [Digital Cinema System Specification - Version 1.1](https://www.dcimovies.com/archives/spec_v1_1/DCI_DCinema_System_Spec_v1_1.pdf)
     */
    val DisplayP3: RGBColorSpace = RGBColorSpace(
        "Display P3",
        Illuminant.D65,
        SRGBTransferFunctions,
        xyY(0.680, 0.320),
        xyY(0.265, 0.690),
        xyY(0.150, 0.060),
    )

    /**
     * ROMM RGB color space, also known as ProPhoto RGB
     *
     * The CSS Color Module 4 calls this space `prophoto-rgb`.
     *
     * ### References
     * - [ANSI/I3A IT10.7666:2003](https://www.color.org/ROMMRGB.pdf)
     */
    val ROMM_RGB: RGBColorSpace = RGBColorSpace(
        "ROMM RGB",
        Illuminant.D50,
        ROMMTransferFunctions,
        xyY(0.7347, 0.2653),
        xyY(0.1596, 0.8404),
        xyY(0.0366, 0.0001),
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
    r: xyY,
    g: xyY,
    b: xyY,
): RGBColorSpace = RGBColorSpaceImpl(name, whitePoint, transferFunctions, r, g, b) { color ->
    if (color is RGB) color.convertTo(this) else color.toXYZ().toRGB(this)
}

/**
 * The sRGB color space defined in [IEC 61966-2-1](https://webstore.iec.ch/publication/6169)
 */
object SRGB : RGBColorSpace by RGBColorSpaces.SRGB {
    override fun equals(other: Any?): Boolean = RGBColorSpaces.SRGB == other
    override fun hashCode(): Int = RGBColorSpaces.SRGB.hashCode()
    override fun toString(): String = "sRGB"
}

private data class RGBColorSpaceImpl(
    override val name: String,
    override val whitePoint: WhitePoint,
    override val transferFunctions: RGBColorSpace.TransferFunctions,
    private val r: xyY,
    private val g: xyY,
    private val b: xyY,
    private val convertImpl: RGBColorSpaceImpl.(Color) -> RGB,
) : RGBColorSpace {
    override val components: List<ColorComponentInfo> = zeroOneComponentInfo("RGB")
    override fun convert(color: Color): RGB = convertImpl(color)
    override fun create(components: FloatArray): RGB = doCreate(components, ::invoke)
    override val matrixToXyz: FloatArray = rgbToXyzMatrix(whitePoint, r, g, b).rowMajor
    override val matrixFromXyz: FloatArray = Matrix(matrixToXyz).inverse().rowMajor
    override fun toString(): String = name
    override operator fun invoke(r: Float, g: Float, b: Float, alpha: Float): RGB =
        RGB(r, g, b, alpha, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RGBColorSpace) return false
        if (name != other.name) return false
        if (whitePoint != other.whitePoint) return false
        if (transferFunctions != other.transferFunctions) return false
        if (other is RGBColorSpaceImpl) {
            if (r != other.r) return false
            if (g != other.g) return false
            if (b != other.b) return false
        } else {
            if (!matrixToXyz.contentEquals(other.matrixToXyz)) return false
            if (!matrixFromXyz.contentEquals(other.matrixFromXyz)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + whitePoint.hashCode()
        result = 31 * result + transferFunctions.hashCode()
        result = 31 * result + r.hashCode()
        result = 31 * result + g.hashCode()
        result = 31 * result + b.hashCode()
        return result
    }

}

private object SRGBTransferFunctions : RGBColorSpace.TransferFunctions {
    override fun oetf(x: Float): Float {
        return when {
            x <= 0.0031308 -> x * 12.92
            else -> 1.055 * x.spow(1 / 2.4) - 0.055
        }.toFloat()
    }

    override fun eotf(x: Float): Float {
        return when {
            x <= 0.04045 -> x / 12.92
            else -> ((x + 0.055) / 1.055).spow(2.4)
        }.toFloat()
    }
}

private val ACES_WHITE_POINT = WhitePoint("ACES", xyY(0.32168, 0.33767))

// values from [Academy TB-2014-004]
private val ACES_AP0_R = xyY(0.73470, 0.26530)

private val ACES_AP0_G = xyY(0.00000, 1.00000)

private val ACES_AP0_B = xyY(0.00010, -0.0770)

// values from [Academy S-2014-004]
private val ACES_AP1_R = xyY(0.713, 0.293)

private val ACES_AP1_G = xyY(0.165, 0.830)

private val ACES_AP1_B = xyY(0.128, 0.044)

// from [Academy S-2014-003]
private object ACESccTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val twoN15 = 1 / 32768.0 // == 2.pow(-15)
    private const val twoN16 = 1 / 65536.0 // == 2.pow(-16)
    private const val eotfC1 = (9.72 - 15) / 17.52
    private val eotfC2 = (log2(65504.0) + 9.72) / 17.52
    override fun eotf(x: Float): Float {
        return when {
            x <= eotfC1 -> (2.0.spow(x * 17.52 - 9.72) - twoN16) * 2.0
            x < eotfC2 -> 2.0.pow(x * 17.52 - 9.72)
            else -> 65504.0
        }.toFloat()
    }

    override fun oetf(x: Float): Float {
        return when {
            x < twoN15 -> (log2(twoN16 + x.coerceAtLeast(0f) / 2.0) + 9.72) / 17.52
            else -> (log2(x) + 9.72) / 17.52
        }.toFloat()
    }
}

// from [Academy S-2016-001]
private object ACEScctTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val a = 10.5402377416545
    private const val b = 0.0729055341958355
    private val eotfC2 = (log2(65504.0) + 9.72) / 17.52
    override fun eotf(x: Float): Float {
        return when {
            x <= 0.155251141552511 -> (x - b) / a
            x < eotfC2 -> 2.0.pow(x * 17.52 - 9.72)
            else -> 65504.0
        }.toFloat()
    }

    override fun oetf(x: Float): Float {
        return when {
            x < 0.0078125 -> a * x + b
            else -> (log2(x) + 9.72) / 17.52
        }.toFloat()
    }
}

private object BT709TransferFunctions : RGBColorSpace.TransferFunctions {
    private val eotfC = 1.099 * 0.018.spow(0.45) - 0.099
    override fun eotf(x: Float): Float = when {
        x < eotfC -> x / 4.5f
        else -> ((x + 0.099) / 1.099).spow(1 / 0.45)
    }.toFloat()

    override fun oetf(x: Float): Float = when {
        x < 0.018 -> 4.5 * x
        else -> 1.099 * x.spow(0.45) - 0.099
    }.toFloat()
}

private object BT2020TransferFunctions : RGBColorSpace.TransferFunctions {
    // The standard defines the constants with different precision for 10 and 12 bit systems.
    // We use the 12 bit constants.
    private const val a = 1.0993
    private const val b = 0.0181
    private val eotfCutoff = a * b.pow(0.45) - (a - 1) // == oetf(b)
    override fun eotf(x: Float): Float = when {
        x < eotfCutoff -> x / 4.5f
        else -> ((x + (a - 1)) / a).spow(1 / 0.45)
    }.toFloat()

    override fun oetf(x: Float): Float = when {
        x < b -> 4.5 * x
        else -> a * x.spow(0.45) - (a - 1)
    }.toFloat()
}

private object ROMMTransferFunctions : RGBColorSpace.TransferFunctions {
    private const val c = 0.001953
    override fun eotf(x: Float): Float = when {
        x < 16 * c -> x / 16.0
        else -> x.spow(1.8)
    }.toFloat()

    override fun oetf(x: Float): Float = when {
        x < c -> x * 16.0
        else -> x.spow(1.0 / 1.8)
    }.toFloat()
}

// [SMPTE RP 177-1993](http://car.france3.mars.free.fr/Formation%20INA%20HD/HDTV/HDTV%20%202007%20v35/SMPTE%20normes%20et%20confs/rp177.pdf)
private fun rgbToXyzMatrix(whitePoint: WhitePoint, r: xyY, g: xyY, b: xyY): Matrix {
    val primaries = Matrix(
        r.x, g.x, b.x,
        r.y, g.y, b.y,
        r.z, g.z, b.z,
    )
    val wp = whitePoint.chromaticity
    return primaries.inverse().dot(wp.X, wp.Y, wp.Z) { x, y, z ->
        primaries.dotDiagonal(x, y, z)
    }
}
