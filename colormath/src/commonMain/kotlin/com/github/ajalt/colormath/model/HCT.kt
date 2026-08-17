package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.HueColor
import com.github.ajalt.colormath.internal.clampLeadingHue
import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.solveHctToLinearSrgb
import com.github.ajalt.colormath.model.RGBColorSpaces.SRGB

/**
 * The HCT color model, created by Google for Material You.
 *
 * HCT combines the hue and chroma of [CAM16], in the
 * [default viewing conditions][CAM16ViewingConditions.DEFAULT], with the
 * [tone][t] defined as the [LAB] lightness. It is defined in terms of the sRGB gamut: converting
 * an [HCT] color to another model produces an sRGB color with the requested hue and tone, and the
 * chroma reduced if necessary to the maximum available in the gamut for that hue and tone.
 *
 * Values can differ from Android's implementation by a fraction of a percent, since colormath
 * derives its sRGB matrices at full precision rather than using the rounded constants published in
 * the sRGB standard.
 *
 * | Component | Description | Range      |
 * | --------- | ----------- | ---------- |
 * | [h]       | hue         | `[0, 360)` |
 * | [c]       | chroma      | `[0, 120]` |
 * | [t]       | tone        | `[0, 100]` |
 *
 * #### Reference
 * [The Science of Color & Design](https://material.io/blog/science-of-color-design)
 */
data class HCT(
    override val h: Float,
    val c: Float,
    val t: Float,
    override val alpha: Float = 1f,
) : HueColor {
    /** Default constructors for the [HCT] color model. */
    companion object : ColorSpace<HCT> {
        override val name: String get() = "HCT"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("H", true, 0f, 360f),
            ColorComponentInfo("C", false, 0f, 120f),
            ColorComponentInfo("T", false, 0f, 100f),
        )

        override fun convert(color: Color): HCT = color.toHCT()
        override fun create(components: FloatArray): HCT = doCreate(components, ::HCT)
    }

    constructor (h: Number, c: Number, t: Number, alpha: Number = 1f)
            : this(h.toFloat(), c.toFloat(), t.toFloat(), alpha.toFloat())

    override val space: ColorSpace<HCT> get() = HCT

    override fun toSRGB(): RGB {
        val (r, g, b) = solveHctToLinearSrgb(h.toDouble(), c.toDouble(), t.toDouble())
        val f = SRGB.transferFunctions
        return SRGB(
            f.oetf((r / 100).toFloat()),
            f.oetf((g / 100).toFloat()),
            f.oetf((b / 100).toFloat()),
            alpha,
        )
    }

    override fun toHCT(): HCT = this
    override fun toArray(): FloatArray = floatArrayOf(h, c, t, alpha)
    override fun clamp(): HCT = clampLeadingHue(h, c, t, alpha, ::copy)
}
