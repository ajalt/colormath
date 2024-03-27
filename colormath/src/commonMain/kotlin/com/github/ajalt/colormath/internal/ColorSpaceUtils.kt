package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.Illuminant.D65
import com.github.ajalt.colormath.WhitePointColorSpace
import com.github.ajalt.colormath.model.XYZColorSpace


internal inline fun <T : Color> ColorSpace<T>.withValidComps(
    components: FloatArray,
    block: (FloatArray) -> T,
): T {
    val size = this.components.size
    require(components.size in (size - 1)..size) {
        "Invalid component array length: ${components.size}, expected ${size - 1} or $size"
    }
    return block(components)
}

internal inline fun <T : Color> ColorSpace<T>.doCreate(
    components: FloatArray,
    init: (Float, Float, Float, Float) -> T,
): T {
    return withValidComps(components) {
        init(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}

internal inline fun <T : Color> WhitePointColorSpace<T>.adaptToThis(
    color: Color,
    convert: (Color) -> T,
): T {
    return if (((color.space as? WhitePointColorSpace<*>)?.whitePoint
            ?: D65) == whitePoint
    ) convert(color)
    else convert(color.toXYZ().adaptTo(XYZColorSpace(whitePoint)))
}

private val alphaInfo = ColorComponentInfo("alpha", false, 0f, 1f)

internal fun componentInfoList(vararg c: ColorComponentInfo): List<ColorComponentInfo> {
    return listOf(*c, alphaInfo)
}

internal fun threeComponentInfo(
    n1: String, l1: Float, r1: Float,
    n2: String, l2: Float, r2: Float,
    n3: String, l3: Float, r3: Float,
): List<ColorComponentInfo> {
    return componentInfoList(
        ColorComponentInfo(n1, false, l1, r1),
        ColorComponentInfo(n2, false, l2, r2),
        ColorComponentInfo(n3, false, l3, r3),
    )
}

internal fun zeroOneComponentInfo(name: String): List<ColorComponentInfo> {
    return buildList {
        name.mapTo(this) { ColorComponentInfo(it.toString(), false, 0f, 1f) }
        add(alphaInfo)
    }
}

internal fun polarComponentInfo(
    name: String, l: Float, r: Float,
): List<ColorComponentInfo> {
    return buildList {
        name.mapTo(this) {
            ColorComponentInfo(
                name = it.toString(),
                isPolar = it == 'H',
                max = if (it == 'H') 0f else l,
                min = if (it == 'H') 1f else r
            )
        }
        add(alphaInfo)
    }
}
