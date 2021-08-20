package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import kotlin.math.*


/**
 * HSLuv color space, a human friendly alternative to [HSL].
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 * | [s]        | saturation                                | `[0, 100]` |
 * | [l]        | lightness                                 | `[0, 100]` |
 *
 * ### References
 * - [HSLuv homepage](https://www.hsluv.org/)
 */
data class HSLuv(override val h: Float, val s: Float, val l: Float, override val alpha: Float = Float.NaN) : HueColor {
    companion object : ColorSpace<HSLuv> {
        override val name: String get() = "HSLuv"
        override val components: List<ColorComponentInfo> = polarComponentInfo("HSL")
        override fun convert(color: Color): HSLuv = color.toHSLuv()
        override fun create(components: FloatArray): HSLuv = doCreate(components, ::HSLuv)
    }

    constructor (h: Double, s: Double, l: Double, alpha: Double = Double.NaN)
            : this(h.toFloat(), s.toFloat(), l.toFloat(), alpha.toFloat())

    constructor (h: Double, s: Double, l: Double, alpha: Float)
            : this(h.toFloat(), s.toFloat(), l.toFloat(), alpha)

    override val space: ColorSpace<HSLuv> get() = HSLuv

    override fun toLCHuv(): LCHuv {
        if (l > 99.9999) return LCHuv(100f, 0f, h, alpha)
        if (l < 0.00001) return LCHuv(0f, 0f, h, alpha)
        val max = HUSLColorConverter.maxChromaForLH(l.toDouble(), h.toDouble())
        val c = max / 100 * s
        return LCHuv(l, c.toFloat(), h, alpha)
    }

    override fun toSRGB(): RGB = toXYZ().toSRGB()
    override fun toLUV(): LUV = toLCHuv().toLUV()
    override fun toXYZ(): XYZ = toLCHuv().toXYZ()
    override fun toHSLuv(): HSLuv = this
    override fun toArray(): FloatArray = floatArrayOf(h, s, l, alpha)
}


internal object HUSLColorConverter {
    fun maxSafeChromaForL(L: Double): Double {
        return getBounds(L).minOf { (m1, b1) ->
            val x = intersectLineLine(m1, b1, -1 / m1, 0.0)
            distanceFromPole(x, b1 + x * m1)
        }
    }

    fun maxChromaForLH(L: Double, H: Double): Double {
        val hrad: Double = H / 360 * PI * 2
        return getBounds(L).minOf { (mi, hi) ->
            lengthOfRayUntilIntersect(hrad, mi, hi).let { if (it < 0) Double.MAX_VALUE else it }
        }
    }

    private fun getBounds(L: Double): List<Pair<Double, Double>> {
        val result = ArrayList<Pair<Double, Double>>(6)
        val sub1: Double = (L + 16).pow(3) / 1560896
        val sub2 = if (sub1 > CIE_E) sub1 else L / CIE_K
        for (c in 0..2) {
            val m1 = Matrix(SRGB.matrixFromXyz)[0, c]
            val m2 = Matrix(SRGB.matrixFromXyz)[1, c]
            val m3 = Matrix(SRGB.matrixFromXyz)[2, c]
            for (t in 0..1) {
                val top1 = (284517 * m1 - 94839 * m3) * sub2
                val top2 = (838422 * m3 + 769860 * m2 + 731718 * m1) * L * sub2 - 769860 * t * L
                val bottom = (632260 * m3 - 126452 * m2) * sub2 + 126452 * t
                result.add(top1 / bottom to top2 / bottom)
            }
        }
        return result
    }

    private fun intersectLineLine(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return (y1 - y2) / (x2 - x1)
    }

    private fun distanceFromPole(x: Double, y: Double): Double {
        return sqrt(x.pow(2) + y.pow(2))
    }

    private fun lengthOfRayUntilIntersect(theta: Double, a: Double, b: Double): Double {
        return b / (sin(theta) - a * cos(theta))
    }
}
