package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.rectangularComponentInfo
import com.github.ajalt.colormath.internal.toPolarModel
import kotlin.math.pow

/**
 *  The JzAzBz color space: a perceptually uniform space where euclidean distance predicts perceptual difference.
 *
 *  This color space is always calculated relative to [Illuminant.D65].
 *
 * | Component  | Description | sRGB Range        |
 * | ---------- | ----------- | ----------------- |
 * | [j]        | lightness   | `[0, 0.017]`      |
 * | [a]        | green/red   | `[-0.016, 0.017]` |
 * | [b]        | blue/yellow | `[-0.025, 0.021]` |
 *
 * #### Reference
 *
 * M. Safdar, G. Cui, Y. Kim, and M. Luo, "Perceptually uniform color space for image signals including high dynamic
 * range and wide gamut," Opt. Express  25, 15131-15151 (2017).
 */
data class JzAzBz(val j: Float, val a: Float, val b: Float, override val alpha: Float = 1f) : Color {
    companion object : ColorModel<JzAzBz> {
        override val name: String get() = "JzAzBz"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("JAB")
        override fun convert(color: Color): JzAzBz = color.toJzAzBz()
        override fun create(components: FloatArray): JzAzBz = doCreate(components, ::JzAzBz)

        internal const val d0 = 1.6295499532821566e-11
    }

    constructor (j: Double, a: Double, b: Double, alpha: Double)
            : this(j.toFloat(), a.toFloat(), b.toFloat(), alpha.toFloat())

    constructor (j: Double, a: Double, b: Double, alpha: Float = 1f)
            : this(j.toFloat(), a.toFloat(), b.toFloat(), alpha)

    override val model: ColorModel<JzAzBz> get() = JzAzBz

    override fun toRGB(): RGB = when (j) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toXYZ().toRGB()
    }

    // Combined matrix values from https://observablehq.com/@jrus/jzazbz, which seems to be the values that most
    // implementations (such as ImageMagik) use.
    override fun toXYZ(): XYZ {
        fun pqInv(x: Double): Double {
            val xx = x.pow(7.460772656268214e-03)
            return 1e4 * ((0.8359375 - xx) / (18.6875 * xx - 18.8515625)).pow(6.277394636015326)
        }

        val jz = j + d0
        val iz = jz / (0.44 + 0.56 * jz)
        val l = pqInv(iz + 1.386050432715393e-1 * a + 5.804731615611869e-2 * b)
        val m = pqInv(iz - 1.386050432715393e-1 * a - 5.804731615611891e-2 * b)
        val s = pqInv(iz - 9.601924202631895e-2 * a - 8.118918960560390e-1 * b)
        return XYZ(
            x = +1.661373055774069e+00 * l - 9.145230923250668e-01 * m + 2.313620767186147e-01 * s,
            y = -3.250758740427037e-01 * l + 1.571847038366936e+00 * m - 2.182538318672940e-01 * s,
            z = -9.098281098284756e-02 * l - 3.127282905230740e-01 * m + 1.522766561305260e+00 * s,
            alpha = alpha
        )
    }

    override fun toJzCzHz(): JzCzHz = toPolarModel(a, b) { c, h -> JzCzHz(j, c, h, alpha) }
    override fun toJzAzBz(): JzAzBz = this
    override fun toArray(): FloatArray = floatArrayOf(j, a, b, alpha)
}
