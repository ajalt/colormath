package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.internal.*

/**
 * The ICtCp color space, designed for high dynamic range and wide color gamut imagery.
 *
 * | Component  | Description          | Range         |
 * | ---------- | -------------------- | ------------- |
 * | [i]        | intensity            | `[0, 1]`      |
 * | [ct]       | Tritan (blue-yellow) | `[-0.5, 0.5]` |
 * | [cp]       | Protan (red-green)   | `[-0.5, 0.5]` |
 *
 * ### References
 * - [Rec. ITU-R BT.2100-2](https://www.itu.int/rec/R-REC-BT.2100-2-201807-I/en)
 * - [Dolby ICtCp whitepaper](https://professional.dolby.com/siteassets/pdfs/ictcp_dolbywhitepaper_v071.pdf)
 */
data class ICtCp(
    val i: Float,
    val ct: Float,
    val cp: Float,
    override val alpha: Float = 1f,
) : Color {
    companion object : ColorSpace<ICtCp> {
        override val name: String get() = "ICtCp"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("I", "Ct", "Cp")
        override fun convert(color: Color): ICtCp = color.toICtCp()
        override fun create(components: FloatArray): ICtCp = doCreate(components, ::ICtCp)
    }

    constructor (i: Double, ct: Double, cp: Double, alpha: Double = 1.0)
            : this(i.toFloat(), ct.toFloat(), cp.toFloat(), alpha.toFloat())

    constructor (i: Double, ct: Double, cp: Double, alpha: Float)
            : this(i.toFloat(), ct.toFloat(), cp.toFloat(), alpha)

    override val space: ColorSpace<ICtCp> get() = ICtCp

    /** Convert this color to [BT.2020 RGB][RGBColorSpaces.BT_2020] */
    fun toBT2020(): RGB {
        val f = PqNonlinearity
        return MATRIX_ICTCP_ICTCP_to_LMS.dot(i, ct, cp) { l, m, s ->
            MATRIX_ICTCP_LMS_to_RGB.dot(f.eotf(l), f.eotf(m), f.eotf(s)) { r, g, b ->
                BT_2020(r, g, b, alpha)
            }
        }
    }

    override fun toXYZ(): XYZ = toBT2020().toXYZ()
    override fun toSRGB(): RGB = toXYZ().toSRGB()
    override fun toICtCp(): ICtCp = this
    override fun toArray(): FloatArray = floatArrayOf(i, ct, cp, alpha)
}

internal fun convertBT2020ToICtCp(rgb: RGB): ICtCp {
    val f = PqNonlinearity
    return MATRIX_ICTCP_RGB_TO_LMS.dot(rgb.r, rgb.g, rgb.b) { l, m, s ->
        MATRIX_ICTCP_LMS_TO_ICTCP.dot(f.oetf(l), f.oetf(m), f.oetf(s)) { i, ct, cp ->
            ICtCp(i, ct, cp, rgb.alpha)
        }
    }
}

/** The SMPTE ST 2084 EOTF as defined in the ICtCp whitepaper cited above */
private object PqNonlinearity : RGBColorSpace.TransferFunctions {
    private const val m1 = 2610.0 / 16384.0
    private const val m2 = 2523.0 / 4096.0 * 128.0
    private const val c1 = 3424.0 / 4096.0
    private const val c2 = 2413.0 / 4096.0 * 32.0
    private const val c3 = 2392.0 / 4096.0 * 32.0
    private const val lp = 10000.0
    private const val m1d = 1 / m1
    private const val m2d = 1 / m2

    override fun eotf(x: Float): Float {
        val vp = x.spow(m2d)
        val n = (vp - c1).coerceAtLeast(0.0)
        val l = (n / (c2 - c3 * vp)).spow(m1d)
        return (lp * l).toFloat()
    }

    override fun oetf(x: Float): Float {
        val yp = (x / lp).spow(m1)
        return ((c1 + c2 * yp) / (1.0 + c3 * yp)).spow(m2).toFloat()
    }
}

private val MATRIX_ICTCP_RGB_TO_LMS = Matrix(
    1688f, 2146f, 262f,
    683f, 2951f, 462f,
    99f, 309f, 3688f,
).scalarDiv(4096f, inPlace = true)

private val MATRIX_ICTCP_LMS_TO_ICTCP = Matrix(
    2048f, 2048f, 0f,
    6610f, -13613f, 7003f,
    17933f, -17390f, -543f,
).scalarDiv(4096f, inPlace = true)

private val MATRIX_ICTCP_LMS_to_RGB = MATRIX_ICTCP_RGB_TO_LMS.inverse()
private val MATRIX_ICTCP_ICTCP_to_LMS = MATRIX_ICTCP_LMS_TO_ICTCP.inverse()
