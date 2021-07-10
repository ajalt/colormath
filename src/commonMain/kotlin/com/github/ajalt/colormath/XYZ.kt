package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import kotlin.math.pow

/**
 * The CIEXYZ color space.
 *
 * [XYZ] is calculated relative to the D65 standard illuminant.
 *
 * | Component  | sRGB Range  |
 * | ---------- | ----------- |
 * | [x]        | `[0, 0.95]` |
 * | [y]        | `[0, 1]`    |
 * | [z]        | `[0, 1.09]` |
 */
data class XYZ(val x: Float, val y: Float, val z: Float, override val alpha: Float = 1f) : Color {
    companion object : ColorModel<XYZ> {
        override val name: String get() = "XYZ"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("X", false),
            ColorComponentInfo("Y", false),
            ColorComponentInfo("Z", false),
        )

        override fun convert(color: Color): XYZ = color.toXYZ()
        override fun create(components: FloatArray): XYZ = withValidComps(components) {
            XYZ(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor(x: Double, y: Double, z: Double, alpha: Double)
            : this(x.toFloat(), y.toFloat(), z.toFloat(), alpha.toFloat())

    constructor(x: Double, y: Double, z: Double, alpha: Float = 1f)
            : this(x.toFloat(), y.toFloat(), z.toFloat(), alpha)

    override val model: ColorModel<XYZ> get() = XYZ

    // Matrix from http://www.brucelindbloom.com/Eqn_XYZ_to_RGB.html
    private fun r() = 3.2404542f * x - 1.5371385f * y - 0.4985314f * z
    private fun g() = -0.9692660f * x + 1.8760108f * y + 0.0415560f * z
    private fun b() = 0.0556434f * x - 0.2040259f * y + 1.0572252f * z

    override fun toRGB(): RGB = RGB(linearToSRGB(r()), linearToSRGB(g()), linearToSRGB(b()), alpha)
    override fun toLinearRGB(): LinearRGB = LinearRGB(r(), g(), b(), alpha)

    // http://www.brucelindbloom.com/Eqn_XYZ_to_Lab.html
    override fun toLAB(): LAB {
        fun f(t: Float) = when {
            t > CIE_E -> cbrt(t)
            else -> (t * CIE_K + 16) / 116
        }

        val fx = f(x / D65.x)
        val fy = f(y / D65.y)
        val fz = f(z / D65.z)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LAB(l, a, b, alpha)
    }

    // http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Luv.html
    override fun toLUV(): LUV {
        val denominator = x + 15 * y + 3 * z
        val uPrime = if (denominator == 0f) 0f else (4 * x) / denominator
        val vPrime = if (denominator == 0f) 0f else (9 * y) / denominator

        val denominatorReference = D65.x + 15 * D65.y + 3 * D65.z
        val uPrimeReference = (4 * D65.x) / denominatorReference
        val vPrimeReference = (9 * D65.y) / denominatorReference

        val yr = y / D65.y
        val l = when {
            yr > CIE_E -> 116 * cbrt(yr) - 16
            else -> CIE_K * yr
        }
        val u = 13 * l * (uPrime - uPrimeReference)
        val v = 13 * l * (vPrime - vPrimeReference)

        return LUV(l.coerceIn(0f, 100f), u, v, alpha)
    }

    // https://bottosson.github.io/posts/oklab/#converting-from-xyz-to-oklab
    override fun toOklab(): Oklab {
        val l = +0.8189330101 * x + 0.3618667424 * y - 0.1288597137 * z
        val m = +0.0329845436 * x + 0.9293118715 * y + 0.0361456387 * z
        val s = +0.0482003018 * x + 0.2643662691 * y + 0.6338517070 * z

        val ll = cbrt(l)
        val mm = cbrt(m)
        val ss = cbrt(s)

        return Oklab(
            l = +0.2104542553 * ll + 0.7936177850 * mm - 0.0040720468 * ss,
            a = +1.9779984951 * ll - 2.4285922050 * mm + 0.4505937099 * ss,
            b = +0.0259040371 * ll + 0.7827717662 * mm - 0.8086757660 * ss,
            alpha = alpha
        )
    }

    override fun toJzAzBz(): JzAzBz {
        fun pq(x: Double): Double {
            val xx = (x * 1e-4).pow(0.1593017578125)
            return ((0.8359375 + 18.8515625 * xx) / (1 + 18.6875 * xx)).pow(134.034375)
        }

        val lp = pq(0.674207838 * x + 0.382799340 * y - 0.047570458 * z)
        val mp = pq(0.149284160 * x + 0.739628340 * y + 0.083327300 * z)
        val sp = pq(0.070941080 * x + 0.174768000 * y + 0.670970020 * z)
        val iz = 0.5 * (lp + mp)
        return JzAzBz(
            j = (0.44 * iz) / (1 - 0.56 * iz) - JzAzBz.d0,
            a = 3.524000 * lp - 4.066708 * mp + 0.542708 * sp,
            b = 0.199076 * lp + 1.096799 * mp - 1.295875 * sp,
            alpha = alpha
        )
    }

    override fun toXYZ(): XYZ = this
    override fun toArray(): FloatArray = floatArrayOf(x, y, z, alpha)
}
