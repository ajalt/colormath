package com.github.ajalt.colormath

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
 * [r], [g], [b], and [a] all have a range `[0, 255]`
 */
@JvmInline
value class RGBInt(val argb: UInt) : Color {
    constructor(a: UByte, r: UByte, g: UByte, b: UByte) : this(
        (a.toUInt() shl 24) or (r.toUInt() shl 16) or (g.toUInt() shl 8) or b.toUInt()
    )

    override val alpha: Float get() = (a.toFloat() / 255f)

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

    override fun toHex(withNumberSign: Boolean, renderAlpha: RenderCondition): String = buildString(9) {
        if (withNumberSign) append('#')
        append(r.renderHex()).append(g.renderHex()).append(b.renderHex())
        if (renderAlpha == RenderCondition.ALWAYS || renderAlpha == RenderCondition.AUTO && a < 255u) {
            append(a.renderHex())
        }
    }

    operator fun component1() = r
    operator fun component2() = g
    operator fun component3() = b
    operator fun component4() = a

    private fun UByte.renderHex() = toString(16).padStart(2, '0')


    override fun componentCount(): Int = 4
    override fun components(): FloatArray = floatArrayOf(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
    override fun fromComponents(components: FloatArray): RGBInt {
        requireComponentSize(components)
        return RGBInt(components[0].toInt().toUByte(),
            components[1].toInt().toUByte(),
            components[2].toInt().toUByte(),
            components.getOrElse(3) { 1f }.toInt().toUByte())
    }
}
