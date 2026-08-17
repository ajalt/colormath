package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.HueColor
import com.github.ajalt.colormath.internal.CIE_E
import com.github.ajalt.colormath.internal.CIE_K
import com.github.ajalt.colormath.internal.clampTrailingHue
import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.degToRad
import com.github.ajalt.colormath.internal.normalizeDeg
import com.github.ajalt.colormath.internal.radToDeg
import com.github.ajalt.colormath.internal.sqrtSumSq
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The conditions under which a color modeled by [CAM16] is viewed.
 *
 * The default values describe a color on a screen viewed in a typically lit room (200 lux).
 *
 * @param whitePointX The X coordinate of the white point, scaled so that Y is 1. Default is the D65 value used by material-color-utilities.
 * @param whitePointY The Y coordinate of the white point, normally 1.
 * @param whitePointZ The Z coordinate of the white point, scaled so that Y is 1.
 * @param adaptingLuminance The luminance of the adapting field in cd/m² (lux times 0.0586). Default is 200 lux.
 * @param backgroundLstar The [LAB] lightness of the area surrounding the color. Default is 50, midgray.
 * @param surround The relative brightness of the area surrounding the color, from 0 (pitch dark) to 2 (as bright as the color itself). Default is 2.
 * @param discountingIlluminant Whether the viewer's eye discounts the tint of the ambient light. Default is false, which models self-luminous displays.
 */
data class CAM16ViewingConditions(
    val whitePointX: Double = 0.95047,
    val whitePointY: Double = 1.0,
    val whitePointZ: Double = 1.08883,
    val adaptingLuminance: Double = 200.0 / PI * yFromLstar(50.0) / 100.0,
    val backgroundLstar: Double = 50.0,
    val surround: Double = 2.0,
    val discountingIlluminant: Boolean = false,
) {
    companion object {
        /** The viewing conditions used by Android and material-color-utilities. */
        val DEFAULT: CAM16ViewingConditions = CAM16ViewingConditions()
    }

    internal val n: Double
    internal val aw: Double
    internal val nbb: Double
    internal val ncb: Double
    internal val c: Double
    internal val nc: Double
    internal val rgbD: DoubleArray
    internal val fl: Double
    internal val flRoot: Double
    internal val z: Double

    init {
        val bgLstar = max(0.1, backgroundLstar)
        val rW =
            whitePointX * 100 * 0.401288 + whitePointY * 100 * 0.650173 + whitePointZ * 100 * -0.051461
        val gW =
            whitePointX * 100 * -0.250268 + whitePointY * 100 * 1.204414 + whitePointZ * 100 * 0.045854
        val bW =
            whitePointX * 100 * -0.002079 + whitePointY * 100 * 0.048952 + whitePointZ * 100 * 0.953127
        val f = 0.8 + surround / 10.0
        c = when {
            f >= 0.9 -> lerp(0.59, 0.69, (f - 0.9) * 10.0)
            else -> lerp(0.525, 0.59, (f - 0.8) * 10.0)
        }
        val d = when {
            discountingIlluminant -> 1.0
            else -> (f * (1.0 - 1.0 / 3.6 * exp((-adaptingLuminance - 42.0) / 92.0))).coerceIn(
                0.0,
                1.0
            )
        }
        nc = f
        rgbD = doubleArrayOf(
            d * (100.0 / rW) + 1.0 - d,
            d * (100.0 / gW) + 1.0 - d,
            d * (100.0 / bW) + 1.0 - d,
        )
        val k = 1.0 / (5.0 * adaptingLuminance + 1.0)
        val k4 = k * k * k * k
        val k4F = 1.0 - k4
        fl = k4 * adaptingLuminance + 0.1 * k4F * k4F * cbrt(5.0 * adaptingLuminance)
        flRoot = fl.pow(0.25)
        n = yFromLstar(bgLstar) / (whitePointY * 100)
        z = 1.48 + sqrt(n)
        nbb = 0.725 / n.pow(0.2)
        ncb = nbb
        val rA = adapt(fl * rgbD[0] * rW / 100.0)
        val gA = adapt(fl * rgbD[1] * gW / 100.0)
        val bA = adapt(fl * rgbD[2] * bW / 100.0)
        aw = (2.0 * rA + gA + 0.05 * bA) * nbb
    }

    private fun adapt(component: Double): Double {
        val af = component.pow(0.42)
        return 400.0 * af / (af + 27.13)
    }

    private fun lerp(start: Double, stop: Double, amount: Double): Double {
        return start + (stop - start) * amount
    }
}

/**
 * The color space describing colors in the [CAM16] model.
 */
interface CAM16ColorSpace : ColorSpace<CAM16> {
    /** The conditions under which colors in this space are viewed. */
    val viewingConditions: CAM16ViewingConditions

    operator fun invoke(j: Float, c: Float, h: Float, alpha: Float = 1f): CAM16
    operator fun invoke(j: Number, c: Number, h: Number, alpha: Number = 1f): CAM16 =
        invoke(j.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())
}

/** Create a new [CAM16ColorSpace] that will be calculated relative to the given [viewingConditions] */
fun CAM16ColorSpace(viewingConditions: CAM16ViewingConditions): CAM16ColorSpace =
    when (viewingConditions) {
        CAM16ViewingConditions.DEFAULT -> CAM16ColorSpaces.CAM16Default
        else -> CAM16ColorSpaceImpl(viewingConditions)
    }

private data class CAM16ColorSpaceImpl(
    override val viewingConditions: CAM16ViewingConditions,
) : CAM16ColorSpace {
    override val name: String get() = "CAM16"
    override val components: List<ColorComponentInfo> = componentInfoList(
        ColorComponentInfo("J", false, 0f, 100f),
        ColorComponentInfo("C", false, 0f, 120f),
        ColorComponentInfo("H", true, 0f, 360f),
    )

    override fun convert(color: Color): CAM16 = when {
        color is CAM16 && color.space == this -> color
        else -> convertXYZ65ToCAM16(color.toXYZ().adaptTo(XYZColorSpaces.XYZ65), this)
    }

    override fun create(components: FloatArray): CAM16 = doCreate(components, ::invoke)
    override fun toString(): String = "CAM16ColorSpace($viewingConditions)"
    override operator fun invoke(j: Float, c: Float, h: Float, alpha: Float): CAM16 =
        CAM16(j, c, h, alpha, this)

    override fun hashCode(): Int = viewingConditions.hashCode()
    override fun equals(other: Any?): Boolean {
        return other is CAM16ColorSpace && viewingConditions == other.viewingConditions
    }
}

object CAM16ColorSpaces {
    /** A [CAM16] color space with the [default][CAM16ViewingConditions.DEFAULT] viewing conditions */
    val CAM16Default: CAM16ColorSpace = CAM16ColorSpaceImpl(CAM16ViewingConditions.DEFAULT)
}

/**
 * The CAM16 color appearance model, using the lightness ([j]), chroma ([c]), and hue ([h])
 * dimensions.
 *
 * [CAM16] describes how a color appears under a set of
 * [viewing conditions][CAM16ColorSpace.viewingConditions], which default to those used by Android
 * and material-color-utilities. Conversions treat XYZ values as relative to D65.
 *
 * The other CAM16 dimensions ([q], [m], [s]) and the CAM16-UCS coordinates ([jStar], [aStar],
 * [bStar]) are derived from the three components.
 *
 * | Component | Description | Range      |
 * | --------- | ----------- | ---------- |
 * | [j]       | lightness   | `[0, 100]` |
 * | [c]       | chroma      | `[0, 120]` |
 * | [h]       | hue         | `[0, 360)` |
 *
 * #### Reference
 * C. Li et al. "Comprehensive color solutions: CAM16, CAT16, and CAM16-UCS." Color Research &
 * Application 42.6 (2017)
 */
data class CAM16(
    val j: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float,
    override val space: CAM16ColorSpace,
) : HueColor {
    /** Default constructors for the [CAM16] color model: the [CAM16Default][CAM16ColorSpaces.CAM16Default] space. */
    companion object : CAM16ColorSpace by CAM16ColorSpaces.CAM16Default {
        override fun equals(other: Any?): Boolean = CAM16ColorSpaces.CAM16Default == other
        override fun hashCode(): Int = CAM16ColorSpaces.CAM16Default.hashCode()
    }

    /** Brightness: the absolute counterpart of the lightness [j], which depends on the adapting luminance */
    val q: Float
        get() {
            val vc = space.viewingConditions
            return (4.0 / vc.c * sqrt(j / 100.0) * (vc.aw + 4.0) * vc.flRoot).toFloat()
        }

    /** Colorfulness: the absolute counterpart of the chroma [c], which depends on the adapting luminance */
    val m: Float get() = (c * space.viewingConditions.flRoot).toFloat()

    /** Saturation: the colorfulness of the color in proportion to its own brightness */
    val s: Float
        get() {
            val vc = space.viewingConditions
            return (50.0 * sqrt(alphaCam * vc.c / (vc.aw + 4.0))).toFloat()
        }

    /** The lightness coordinate in the CAM16-UCS space */
    val jStar: Float get() = ((1.0 + 100.0 * 0.007) * j / (1.0 + 0.007 * j)).toFloat()

    /** The a coordinate in the CAM16-UCS space */
    val aStar: Float get() = (mStar * cos(h.toDouble().degToRad())).toFloat()

    /** The b coordinate in the CAM16-UCS space */
    val bStar: Float get() = (mStar * sin(h.toDouble().degToRad())).toFloat()

    private val alphaCam: Double get() = if (c == 0f || j == 0f) 0.0 else c / sqrt(j / 100.0)
    private val mStar: Double get() = ln(1.0 + 0.0228 * m) / 0.0228

    override fun toSRGB(): RGB = toXYZ().toSRGB()

    override fun toXYZ(): XYZ {
        val vc = space.viewingConditions
        val t = (alphaCam / (1.64 - 0.29.pow(vc.n)).pow(0.73)).pow(1.0 / 0.9)
        val hRad = h.toDouble().degToRad()

        val eHue = 0.25 * (cos(hRad + 2.0) + 3.8)
        val ac = vc.aw * (j / 100.0).pow(1.0 / vc.c / vc.z)
        val p1 = eHue * (50000.0 / 13.0) * vc.nc * vc.ncb
        val p2 = ac / vc.nbb

        val hSin = sin(hRad)
        val hCos = cos(hRad)

        val gamma = 23.0 * (p2 + 0.305) * t / (23.0 * p1 + 11.0 * t * hCos + 108.0 * t * hSin)
        val a = gamma * hCos
        val b = gamma * hSin
        val rA = (460.0 * p2 + 451.0 * a + 288.0 * b) / 1403.0
        val gA = (460.0 * p2 - 891.0 * a - 261.0 * b) / 1403.0
        val bA = (460.0 * p2 - 220.0 * a - 6300.0 * b) / 1403.0

        fun unadapt(adapted: Double): Double {
            val base = max(0.0, 27.13 * abs(adapted) / (400.0 - abs(adapted)))
            return sign(adapted) * (100.0 / vc.fl) * base.pow(1.0 / 0.42)
        }

        val rF = unadapt(rA) / vc.rgbD[0]
        val gF = unadapt(gA) / vc.rgbD[1]
        val bF = unadapt(bA) / vc.rgbD[2]

        return XYZColorSpaces.XYZ65(
            x = (1.8620678 * rF - 1.0112547 * gF + 0.14918678 * bF) / 100.0,
            y = (0.38752654 * rF + 0.62144744 * gF - 0.00897398 * bF) / 100.0,
            z = (-0.01584150 * rF - 0.03412294 * gF + 1.0499644 * bF) / 100.0,
            alpha = alpha,
        )
    }

    override fun toCAM16(): CAM16 = this
    override fun toArray(): FloatArray = floatArrayOf(j, c, h, alpha)
    override fun clamp(): CAM16 = clampTrailingHue(j, c, h, alpha, ::copy)
}

// Ported from material-color-utilities' Cam16.java, Copyright Google LLC, Apache License 2.0
// https://github.com/material-foundation/material-color-utilities
internal fun convertXYZ65ToCAM16(xyz: XYZ, space: CAM16ColorSpace): CAM16 {
    val vc = space.viewingConditions
    val x = xyz.x * 100.0
    val y = xyz.y * 100.0
    val z = xyz.z * 100.0

    val rT = x * 0.401288 + y * 0.650173 + z * -0.051461
    val gT = x * -0.250268 + y * 1.204414 + z * 0.045854
    val bT = x * -0.002079 + y * 0.048952 + z * 0.953127

    fun adapt(component: Double, i: Int): Double {
        val d = vc.rgbD[i] * component
        val af = (vc.fl * abs(d) / 100.0).pow(0.42)
        return sign(d) * 400.0 * af / (af + 27.13)
    }

    val rA = adapt(rT, 0)
    val gA = adapt(gT, 1)
    val bA = adapt(bT, 2)

    val a = (11.0 * rA + -12.0 * gA + bA) / 11.0
    val b = (rA + gA - 2.0 * bA) / 9.0
    val u = (20.0 * rA + 20.0 * gA + 21.0 * bA) / 20.0
    val p2 = (40.0 * rA + 20.0 * gA + bA) / 20.0

    val hue = atan2(b, a).radToDeg().normalizeDeg()

    val ac = p2 * vc.nbb
    val j = 100.0 * (ac / vc.aw).pow(vc.c * vc.z)

    val huePrime = if (hue < 20.14) hue + 360 else hue
    val eHue = 0.25 * (cos(huePrime.degToRad() + 2.0) + 3.8)
    val p1 = 50000.0 / 13.0 * eHue * vc.nc * vc.ncb
    val t = p1 * sqrtSumSq(a, b) / (u + 0.305)
    val alpha = (1.64 - 0.29.pow(vc.n)).pow(0.73) * t.pow(0.9)
    val c = alpha * sqrt(j / 100.0)

    return space(j.toFloat(), c.toFloat(), hue.toFloat(), xyz.alpha)
}

internal fun yFromLstar(lstar: Double): Double {
    val ft = (lstar + 16.0) / 116.0
    val ft3 = ft * ft * ft
    return 100.0 * if (ft3 > CIE_E) ft3 else (116.0 * ft - 16.0) / CIE_K
}

internal fun lstarFromY(y: Double): Double {
    val f = if (y > CIE_E) cbrt(y) else (y * CIE_K + 16.0) / 116.0
    return 116.0 * f - 16.0
}
