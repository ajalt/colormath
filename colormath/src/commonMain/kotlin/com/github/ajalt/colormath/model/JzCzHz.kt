package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.HueColor
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.threeComponentInfo

/**
 * The JzCzHz color model, the cylindrical representation of [JzAzBz].
 *
 * | Component  | Description                               | Range      |
 * | ---------- | ----------------------------------------- | ---------- |
 * | [j]        | lightness                                 | `[0, 1]`   |
 * | [c]        | chroma                                    | `[-1, 1]`  |
 * | [h]        | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 *
 *  #### Reference
 * M. Safdar, G. Cui, Y. Kim, and M. Luo, "Perceptually uniform color space for image signals including high dynamic
 * range and wide gamut," Opt. Express  25, 15131-15151 (2017).
 */
data class JzCzHz(
    val j: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float = 1f,
) : Color,
    HueColor {
    /** Default constructors for the [JzCzHz] color model. */
    companion object : ColorSpace<JzCzHz> {
        override val name: String get() = "JzCzHz"
        override val components: List<ColorComponentInfo> = threeComponentInfo(
            "J", 0f, 1f, "C", -1f, 1f, "H", 0f, 360f,
        )

        override fun convert(color: Color): JzCzHz = color.toJzCzHz()
        override fun create(components: FloatArray): JzCzHz = doCreate(components, ::JzCzHz)
    }

    constructor(l: Number, c: Number, h: Number, alpha: Number = 1f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    override val space: ColorSpace<JzCzHz> get() = JzCzHz

    override fun toSRGB(): RGB = when (j) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toJzAzBz().toSRGB()
    }

    override fun toXYZ(): XYZ = toJzAzBz().toXYZ()
    override fun toJzAzBz(): JzAzBz = fromPolarModel(c, h) { a, b -> return JzAzBz(j, a, b, alpha) }
    override fun toJzCzHz(): JzCzHz = this
    override fun toArray(): FloatArray = floatArrayOf(j, c, h, alpha)
}
