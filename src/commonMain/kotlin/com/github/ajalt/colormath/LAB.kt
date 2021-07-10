package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.*
import kotlin.math.pow

/**
 * CIE LAB color space, also referred to as `CIE 1976 L*a*b*`.
 *
 * The cylindrical representation of this space is [LCH].
 *
 * [LAB] is calculated relative to the D65 standard illuminant.
 *
 * | Component  | Description | sRGB Range         |
 * | ---------- | ----------- | ------------------ |
 * | [l]        | lightness   | `[0, 100]`         |
 * | [a]        | green/red   | `[-86.1, 98.23]`   |
 * | [b]        | blue/yellow | `[-107.86, 94.48]` |
 */
data class LAB(val l: Float, val a: Float, val b: Float, override val alpha: Float = 1f) : Color {
    companion object : ColorModel<LAB> {
        override val name: String get() = "LAB"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("L", false),
            ColorComponentInfo("A", false),
            ColorComponentInfo("B", false),
        )

        override fun convert(color: Color): LAB = color.toLAB()
        override fun create(components: FloatArray): LAB = withValidComps(components) {
            LAB(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor (l: Double, a: Double, b: Double, alpha: Double)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    constructor (l: Double, a: Double, b: Double, alpha: Float = 1f)
            : this(l.toFloat(), a.toFloat(), b.toFloat(), alpha)

    override val model: ColorModel<LAB> get() = LAB

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Lab_to_XYZ.html
        if (l == 0f) return XYZ(0.0, 0.0, 0.0)

        val fy = (l + 16) / 116f
        val fz = fy - b / 200f
        val fx = a / 500f + fy

        val yr = if (l > CIE_E_times_K) fy.pow(3) else l / CIE_K
        val zr = fz.pow(3).let { if (it > CIE_E) it else (116 * fz - 16) / CIE_K }
        val xr = fx.pow(3).let { if (it > CIE_E) it else (116 * fx - 16) / CIE_K }

        return XYZ(xr * D65.x, yr * D65.y, zr * D65.z, alpha)
    }

    override fun toLCH(): LCH = toPolarModel(a, b) { c, h -> LCH(l, c, h, alpha) }
    override fun toLAB(): LAB = this

    override fun toArray(): FloatArray = floatArrayOf(l, a, b, alpha)
}
