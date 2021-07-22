package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorModel


internal inline fun <T : Color> ColorModel<T>.withValidComps(components: FloatArray, block: (FloatArray) -> T): T {
    val size = this.components.size
    require(components.size in (size - 1)..size) {
        "Invalid component array length: ${components.size}, expected ${size - 1} or $size"
    }
    return block(components)
}

internal inline fun <T : Color> ColorModel<T>.doCreate(
    components: FloatArray,
    init: (Float, Float, Float, Float) -> T,
): T {
    return withValidComps(components) {
        init(components[0], components[1], components[2], components.getOrElse(3) { 1f })
    }
}

internal fun componentInfoList(vararg c: ColorComponentInfo) = listOf(*c, ColorComponentInfo("alpha", false))
internal fun rectangularComponentInfo(name: String) = listOf(
    ColorComponentInfo(name[0].toString(), false),
    ColorComponentInfo(name[1].toString(), false),
    ColorComponentInfo(name[2].toString(), false),
    ColorComponentInfo("alpha", false),
)
internal fun polarComponentInfo(name: String) = listOf(
    ColorComponentInfo(name[0].toString(), name[0] == 'H'),
    ColorComponentInfo(name[1].toString(), name[1] == 'H'),
    ColorComponentInfo(name[2].toString(), name[2] == 'H'),
    ColorComponentInfo("alpha", false),
)
