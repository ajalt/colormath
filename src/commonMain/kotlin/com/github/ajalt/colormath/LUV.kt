package com.github.ajalt.colormath

import com.github.ajalt.colormath.Illuminant.Companion.D65
import com.github.ajalt.colormath.internal.*
import kotlin.math.pow

/**
 * The CIE LUV color space, also referred to as `CIE 1976 L*u*v*`.
 *
 * [LUV] is calculated relative to the D65 standard illuminant.
 *
 * | Component  | Description  | sRGB Range    |
 * | ---------- | ------------ | ------------- |
 * | [l]        | lightness    | `[0, 100]`    |
 * | [u]        |              | `[-100, 100]` |
 * | [v]        |              | `[-100, 100]` |
 */
data class LUV(val l: Float, val u: Float, val v: Float, override val alpha: Float = 1f) : Color {
    companion object : ColorModel<LUV> {
        override val name: String get() = "LUV"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("L", false),
            ColorComponentInfo("U", false),
            ColorComponentInfo("V", false),
        )

        override fun convert(color: Color): LUV = color.toLUV()
        override fun create(components: FloatArray): LUV = withValidComps(components) {
            LUV(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor(l: Double, u: Double, v: Double, alpha: Double)
            : this(l.toFloat(), u.toFloat(), v.toFloat(), alpha.toFloat())

    constructor(l: Double, u: Double, v: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), u.toFloat(), v.toFloat(), alpha)

    override val model: ColorModel<LUV> get() = LUV

    override fun toRGB(): RGB = when (l) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    override fun toXYZ(): XYZ {
        // http://www.brucelindbloom.com/Eqn_Luv_to_XYZ.html
        if (l == 0f) return XYZ(0.0f, 0.0f, 0.0f)

        val denominator0 = D65.x + 15 * D65.y + 3 * D65.z
        val u0 = 4 * D65.x / denominator0
        val v0 = 9 * D65.y / denominator0

        val y = if (l > CIE_E_times_K) ((l + 16) / 116f).pow(3) else l / CIE_K

        val a = (52 * l / (u + 13 * l * u0) - 1) / 3
        val b = -5 * y
        val c = -1f / 3
        val d = y * ((39 * l) / (v + 13 * l * v0) - 5)

        val x = (d - b) / (a - c)
        val z = x * a + b

        return XYZ(x, y, z, alpha)
    }

    override fun toHCL(): HCL = toPolarModel(u, v) { c, h -> HCL(h, c, l, alpha) }
    override fun toLUV(): LUV = this
    override fun toArray(): FloatArray = floatArrayOf(l, u, v, alpha)
}
