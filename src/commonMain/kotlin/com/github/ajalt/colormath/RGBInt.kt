package com.github.ajalt.colormath

import com.github.ajalt.colormath.RenderCondition.AUTO
import com.github.ajalt.colormath.internal.componentInfo
import com.github.ajalt.colormath.internal.requireComponentSize
import kotlin.jvm.JvmInline

/**
 * A color in the sRGB color space, which uses the D65 illuminant.
 *
 * This is an inline value class stores the color as a packed [argb] integer, such as those returned from
 * `android.graphics.Color.argb` or `java.awt.image.BufferedImage.getRGB`.
 *
 * You can destructure this class into [r], [g], [b], and [a] components: `val (r, g, b, a) = RGBInt(0xff112233u)`
 *
 * | Component  | Description | sRGB Gamut |
 * | ---------- | ----------- | ---------- |
 * | [r]        | red         | `[0, 255]` |
 * | [g]        | green       | `[0, 255]` |
 * | [b]        | blue        | `[0, 255]` |
 */
@JvmInline
value class RGBInt(val argb: UInt) : Color {
    companion object {
        val model = object : ColorModel {
            override val name: String get() = "RGBInt"
            override val components: List<ColorComponentInfo> = componentInfo(
                ColorComponentInfo("R", false, 0f, 255f),
                ColorComponentInfo("G", false, 0f, 255f),
                ColorComponentInfo("B", false, 0f, 255f),
            )
        }
    }

    constructor(r: UByte, g: UByte, b: UByte, a: UByte = 0xff.toUByte()) : this(
        (a.toUInt() shl 24) or (r.toUInt() shl 16) or (g.toUInt() shl 8) or b.toUInt()
    )

    override val alpha: Float get() = (a.toFloat() / 255f)
    override val model: ColorModel get() = RGBInt.model

    val a: UByte get() = (argb shr 24).toUByte()
    val r: UByte get() = (argb shr 16).toUByte()
    val g: UByte get() = (argb shr 8).toUByte()
    val b: UByte get() = (argb shr 0).toUByte()

    override fun toRGB(): RGB = RGB(
        r = r.toInt(),
        g = g.toInt(),
        b = b.toInt(),
        a = a.toInt() / 255f,
    )

    /**
     * Convert this color to an RGB hex string.
     *
     * If [renderAlpha] is `ALWAYS`, the [alpha] value will be added e.g. the `aa` in `#ffffffaa`.
     * If it's `NEVER`, the [alpha] will be omitted. If it's `AUTO`, then the [alpha] will be added
     * if it's less than 1.
     *
     * @return A string in the form `"#ffffff"` if [withNumberSign] is true,
     *     or in the form `"ffffff"` otherwise.
     */
    fun toHex(withNumberSign: Boolean = true, renderAlpha: RenderCondition = AUTO): String = buildString(9) {
        if (withNumberSign) append('#')
        append(r.renderHex()).append(g.renderHex()).append(b.renderHex())
        if (renderAlpha == RenderCondition.ALWAYS || renderAlpha == AUTO && a < 255u) {
            append(a.renderHex())
        }
    }

    operator fun component1() = r
    operator fun component2() = g
    operator fun component3() = b
    operator fun component4() = a

    private fun UByte.renderHex() = toString(16).padStart(2, '0')

    override fun convertToThis(other: Color): RGBInt = other.toRGB().toRGBInt()
    override fun components(): FloatArray = floatArrayOf(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
    override fun fromComponents(components: FloatArray): RGBInt {
        requireComponentSize(components)
        return RGBInt(
            r = components[0].toInt().toUByte(),
            g = components[1].toInt().toUByte(),
            b = components[2].toInt().toUByte(),
            a = components.getOrElse(3) { 1f }.toInt().toUByte()
        )
    }
}
