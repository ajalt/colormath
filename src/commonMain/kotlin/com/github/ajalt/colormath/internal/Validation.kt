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

internal inline fun <T : Color> Color.withValidComps(components: FloatArray, block: (FloatArray) -> T): T {
    val size = model.components.size
    require(components.size in (size - 1)..size) {
        "Invalid component array length: ${components.size}, expected ${size - 1} or $size"
    }
    return block(components)
}

internal fun componentInfoList(vararg c: ColorComponentInfo) = listOf(*c, ColorComponentInfo("alpha", false, 0f, 1f))
