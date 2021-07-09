package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.componentInfoList
import com.github.ajalt.colormath.internal.fromPolarModel
import com.github.ajalt.colormath.internal.withValidComps

/**
 * The JzCzHz color model, the cylindrical representation of [JzAzBz].
 *
 * | Component  | Description  | sRGB Range   |
 * | ---------- | ------------ | ------------ |
 * | [j]        | lightness    | `[0, 0.017]` |
 * | [c]        | chroma       | `[0, 0.025]` |
 * | [h]        | hue, degrees | `[0, 360)`   |
 */
data class JzCzHz(val j: Float, val c: Float, override val h: Float, override val alpha: Float = 1f) : Color, HueColor {
    companion object : ColorModel<JzCzHz> {
        override val name: String get() = "JzCzHz"
        override val components: List<ColorComponentInfo> = componentInfoList(
            ColorComponentInfo("J", false, 0f, 0.01758005f),
            ColorComponentInfo("C", false, 0f, 0.024975063f),
            ColorComponentInfo("H", true, 0f, 360f),
        )

        override fun convert(color: Color): JzCzHz = color.toJzCzHz()
        override fun create(components: FloatArray): JzCzHz = withValidComps(components) {
            JzCzHz(it[0], it[1], it[2], it.getOrElse(3) { 1f })
        }
    }

    constructor(l: Double, c: Double, h: Double, alpha: Double)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())

    constructor(l: Double, c: Double, h: Double, alpha: Float = 1.0f)
            : this(l.toFloat(), c.toFloat(), h.toFloat(), alpha)

    override val model: ColorModel<JzCzHz> get() = JzCzHz

    override fun toRGB(): RGB = when (j) {
        0f -> RGB(0f, 0f, 0f, alpha)
        else -> toJzAzBz().toRGB()
    }

    override fun toXYZ(): XYZ = toJzAzBz().toXYZ()
    override fun toJzAzBz(): JzAzBz = fromPolarModel(c, h) { a, b -> return JzAzBz(j, a, b, alpha) }
    override fun toJzCzHz(): JzCzHz = this
    override fun toArray(): FloatArray = floatArrayOf(j, c, h, alpha)
}
