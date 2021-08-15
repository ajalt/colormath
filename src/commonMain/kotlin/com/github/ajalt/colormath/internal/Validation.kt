package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace


internal inline fun <T : Color> ColorSpace<T>.withValidComps(components: FloatArray, block: (FloatArray) -> T): T {
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

internal fun componentInfoList(vararg c: ColorComponentInfo) = listOf(*c, ColorComponentInfo("alpha", false))
internal fun rectangularComponentInfo(vararg names: String): List<ColorComponentInfo> {
    return (names.asList() + "alpha").map { ColorComponentInfo(it, false) }
}

internal fun rectangularComponentInfo(name: String): List<ColorComponentInfo> {
    return rectangularComponentInfo(*name.map { it.toString() }.toTypedArray())
}

internal fun polarComponentInfo(name: String) = name.map {
    ColorComponentInfo(it.toString(), it == 'H')
} + ColorComponentInfo("alpha", false)
