@file:Suppress("FunctionName")

package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.*

/**
 * The color space describing colors in the [LCHab] model.
 */
interface LCHabColorSpace : WhitePointColorSpace<LCHab> {
    operator fun invoke(l: Float, c: Float, h: Float, alpha: Float = 1f): LCHab
    operator fun invoke(l: Number, c: Number, h: Number, alpha: Number = 1f): LCHab =
        invoke(l.toFloat(), c.toFloat(), h.toFloat(), alpha.toFloat())
}

/** Create a new [LCHabColorSpace] that will be calculated relative to the given [whitePoint] */
fun LCHabColorSpace(whitePoint: WhitePoint): LCHabColorSpace = when (whitePoint) {
    Illuminant.D65 -> LCHabColorSpaces.LCHab65
    Illuminant.D50 -> LCHabColorSpaces.LCHab50
    else -> LCHabColorSpaceImpl(whitePoint)
}

private data class LCHabColorSpaceImpl(override val whitePoint: WhitePoint) : LCHabColorSpace {
    override val name: String get() = "LCHab"
    override val components: List<ColorComponentInfo> = componentInfoList(
        ColorComponentInfo("L", false, 0f, 100f),
        ColorComponentInfo("C", false, 0f, 150f),
        ColorComponentInfo("H", true, 0f, 360f),
    )

    override fun convert(color: Color): LCHab = adaptToThis(color) { it.toLCHab() }
    override fun create(components: FloatArray): LCHab = doCreate(components, ::invoke)
    override fun toString(): String = "LCHabColorSpace($whitePoint)"
    override operator fun invoke(l: Float, c: Float, h: Float, alpha: Float): LCHab =
        LCHab(l, c, h, alpha, this)

    override fun hashCode(): Int = whitePoint.hashCode()
    override fun equals(other: Any?): Boolean {
        return other is LCHabColorSpace && whitePoint == other.whitePoint
    }
}

object LCHabColorSpaces {
    /** An [LCHab] color space calculated relative to [Illuminant.D65] */
    val LCHab65: LCHabColorSpace = LCHabColorSpaceImpl(Illuminant.D65)

    /** An [LCHab] color space calculated relative to [Illuminant.D50] */
    val LCHab50: LCHabColorSpace = LCHabColorSpaceImpl(Illuminant.D50)
}

/**
 * `CIE LCh(ab)` color model, a.k.a. `LCH`, the cylindrical representation of [LAB].
 *
 * | Component | Description                               | Range      |
 * |-----------|-------------------------------------------|------------|
 * | L         | lightness                                 | `[0, 100]` |
 * | c         | chroma                                    | `[0, 150]` |
 * | h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
 */
data class LCHab internal constructor(
    val l: Float,
    val c: Float,
    override val h: Float,
    override val alpha: Float,
    override val space: LCHabColorSpace,
) : HueColor {
    /** Default constructors for the [LCHab] color model: the [LCHab65][LCHabColorSpaces.LCHab65] space. */
    companion object : LCHabColorSpace by LCHabColorSpaces.LCHab65 {
        override fun equals(other: Any?): Boolean = LCHabColorSpaces.LCHab65 == other
        override fun hashCode(): Int = LCHabColorSpaces.LCHab65.hashCode()
    }

    override fun toSRGB(): RGB = toLAB().toSRGB()
    override fun toXYZ(): XYZ = toLAB().toXYZ()
    override fun toLAB(): LAB =
        fromPolarModel(c, h) { a, b -> LABColorSpace(space.whitePoint)(l, a, b, alpha) }

    override fun toLCHab(): LCHab = this
    override fun toArray(): FloatArray = floatArrayOf(l, c, h, alpha)
    override fun clamp(): LCHab = clamp3(l, c, h, alpha, ::copy)
}
