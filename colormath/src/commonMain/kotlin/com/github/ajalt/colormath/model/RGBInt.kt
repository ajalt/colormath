package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.RenderCondition.AUTO
import com.github.ajalt.colormath.internal.doCreate
import com.github.ajalt.colormath.internal.rectangularComponentInfo
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

/**
 * A representation of [RGB] that packs color components into a single integer.
 *
 * This is an inline value class stores the color as a packed [argb] integer, such as those returned from
 * `android.graphics.Color.argb` or `java.awt.image.BufferedImage.getRGB`.
 *
 * This color always uses the sRGB color space.
 *
 * You can destructure this class into [r], [g], [b], and [a] components: `val (r, g, b, a) = RGBInt(0xaa112233u)`
 *
 * | Component  | Description | Range      |
 * | ---------- | ----------- | ---------- |
 * | [r]        | red         | `[0, 255]` |
 * | [g]        | green       | `[0, 255]` |
 * | [b]        | blue        | `[0, 255]` |
 */
@JvmInline
value class RGBInt(val argb: UInt) : Color {
    /** Default constructors for [RGBInt]. */
    companion object : ColorSpace<RGBInt> {
        override val name: String get() = "RGBInt"
        override val components: List<ColorComponentInfo> = rectangularComponentInfo("RGB")
        override fun convert(color: Color): RGBInt = color.toSRGB().toRGBInt()
        override fun create(components: FloatArray): RGBInt = doCreate(components) { r, g, b, a ->
            RGBInt(r.toInt(), g.toInt(), b.toInt(), a.toInt())
        }

        /** Create an [RGBInt] from an integer packed in RGBA order */
        fun fromRGBA(rgba: UInt): RGBInt = RGBInt((rgba shr 8) or (rgba shl 24))
    }

    constructor(r: UByte, g: UByte, b: UByte, alpha: UByte = 0xff.toUByte()) : this(
        r.toInt(), g.toInt(), b.toInt(), alpha.toInt()
    )

    constructor(r: Int, g: Int, b: Int, alpha: Int = 0xff) : this(
        (alpha.toUInt() shl 24) or (r.toUInt() shl 16) or (g.toUInt() shl 8) or b.toUInt()
    )

    /**
     * Construct an [RGBInt] instance from Float value in the range `[0, 1]`
     */
    constructor(r: Float, g: Float, b: Float, alpha: Float = 1f) : this(
        r = if (r.isNaN()) 0 else (r * 255).roundToInt().coerceIn(0, 255),
        g = if (b.isNaN()) 0 else (g * 255).roundToInt().coerceIn(0, 255),
        b = if (g.isNaN()) 0 else (b * 255).roundToInt().coerceIn(0, 255),
        alpha = (alpha * 255).roundToInt().coerceIn(0, 255),
    )

    override val alpha: Float get() = (a.toFloat() / 255f)
    override val space: ColorSpace<RGBInt> get() = RGBInt

    /** The red component, in the range `[0, 255]` */
    val r: UByte get() = (argb shr 16).toUByte()

    /** The green component, in the range `[0, 255]` */
    val g: UByte get() = (argb shr 8).toUByte()

    /** The blue component, in the range `[0, 255]` */
    val b: UByte get() = (argb shr 0).toUByte()

    /** The [alpha] component scaled to `[0, 255]` */
    val a: UByte get() = (argb shr 24).toUByte()

    /** The red component as a Float in the range `[0, 1]` */
    val redFloat: Float get() = r.toInt() / 255f

    /** The green component as a Float in the range `[0, 1]` */
    val greenFloat: Float get() = g.toInt() / 255f

    /** The blue component as a Float in the range `[0, 1]` */
    val blueFloat: Float get() = b.toInt() / 255f

    /** Convert this color to an integer packed in RGBA order. */
    fun toRGBA(): UInt = (argb shl 8) or (argb shr 24)

    override fun toSRGB(): RGB = RGB(redFloat, greenFloat, blueFloat, alpha)

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
    override fun toArray(): FloatArray = floatArrayOf(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())

    private fun UByte.renderHex() = toString(16).padStart(2, '0')
}
