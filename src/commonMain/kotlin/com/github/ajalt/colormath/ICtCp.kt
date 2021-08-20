package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.XYZColorSpaces.XYZ65
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
    override val alpha: Float = Float.NaN,
) : Color {
    companion object : ColorSpace<ICtCp> {
        override val name: String get() = "ICtCp"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("I", "Ct", "Cp")
        override fun convert(color: Color): ICtCp = color.toICtCp()
        override fun create(components: FloatArray): ICtCp = doCreate(components, ::ICtCp)
    }

    constructor (i: Double, ct: Double, cp: Double, alpha: Double = Double.NaN)
            : this(i.toFloat(), ct.toFloat(), cp.toFloat(), alpha.toFloat())

    constructor (i: Double, ct: Double, cp: Double, alpha: Float)
            : this(i.toFloat(), ct.toFloat(), cp.toFloat(), alpha)

    override val space: ColorSpace<ICtCp> get() = ICtCp

    /** Convert this color to [BT.2020 RGB][RGBColorSpaces.BT_2020] */
    fun toBT2020(): RGB {
        val fo = BT_2020.transferFunctions
        val fe = PqNonlinearity
        return ICTCP_ICTCP_to_LMS.dot(i, ct, cp) { l, m, s ->
            ICTCP_LMS_to_RGB.dot(fe.eotf(l), fe.eotf(m), fe.eotf(s)) { r, g, b ->
                BT_2020(fo.oetf(r), fo.oetf(g), fo.oetf(b), alpha)
            }
        }
    }

    override fun toXYZ(): XYZ {
        val fe = PqNonlinearity
        return ICTCP_ICTCP_to_LMS.dot(i, ct, cp) { l, m, s ->
            ICTCP_LMS_TO_XYZ.dot(fe.eotf(l), fe.eotf(m), fe.eotf(s)) { x, y, z ->
                XYZ65(x, y, z, alpha)
            }
        }
    }

    override fun toSRGB(): RGB = toXYZ().toSRGB()
    override fun toICtCp(): ICtCp = this
    override fun toArray(): FloatArray = floatArrayOf(i, ct, cp, alpha)
}

internal fun convertBT2020ToICtCp(rgb: RGB): ICtCp {
    val fe = BT_2020.transferFunctions
    val fo = PqNonlinearity
    return ICTCP_RGB_TO_LMS.dot(fe.eotf(rgb.r), fe.eotf(rgb.g), fe.eotf(rgb.b)) { l, m, s ->
        ICTCP_LMS_TO_ICTCP.dot(fo.oetf(l), fo.oetf(m), fo.oetf(s)) { i, ct, cp ->
            ICtCp(i, ct, cp, rgb.alpha)
        }
    }
}

internal fun convertXYZToICtCp(xyz: XYZ): ICtCp {
    val f = PqNonlinearity
    return ICTCP_XYZ_TO_LMS.dot(xyz.x, xyz.y, xyz.z) { l, m, s ->
        ICTCP_LMS_TO_ICTCP.dot(f.oetf(l), f.oetf(m), f.oetf(s)) { i, ct, cp ->
            ICtCp(i, ct, cp, xyz.alpha)
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

private val ICTCP_RGB_TO_LMS = Matrix(
    1688f, 2146f, 262f,
    683f, 2951f, 462f,
    99f, 309f, 3688f,
).scalarDiv(4096f, inPlace = true)

private val ICTCP_LMS_TO_ICTCP = Matrix(
    2048f, 2048f, 0f,
    6610f, -13613f, 7003f,
    17933f, -17390f, -543f,
).scalarDiv(4096f, inPlace = true)

private val ICTCP_LMS_to_RGB = ICTCP_RGB_TO_LMS.inverse()
private val ICTCP_ICTCP_to_LMS = ICTCP_LMS_TO_ICTCP.inverse()

// ICtCp defines the XYZ to LMS matrix by multiplying a crosstalk matrix with the old
// Hunt-Pointer-Estevez transform. It's not clear why they use HPE rather than one of the newer
// transforms.
private val ICTCP_CROSSTALK = Matrix(
    0.92f, 0.04f, 0.04f,
    0.04f, 0.92f, 0.04f,
    0.04f, 0.04f, 0.92f,
)
private val HPE_XYZ_TO_LMS = Matrix(
    0.4002f, 0.7076f, -0.0808f,
    -0.2263f, 1.1653f, 0.0457f,
    0f, 0f, 0.9182f,
)

private val ICTCP_XYZ_TO_LMS = ICTCP_CROSSTALK.dot(HPE_XYZ_TO_LMS)
private val ICTCP_LMS_TO_XYZ = ICTCP_XYZ_TO_LMS.inverse()
