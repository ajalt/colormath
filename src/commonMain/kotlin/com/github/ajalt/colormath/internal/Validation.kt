package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo

internal fun Color.requireComponentSize(components: FloatArray) {
    val size = model.components.size
    require(components.size in (size - 1)..size) {
        "Invalid component array length: ${components.size}, expected ${size - 1} or $size"
    }
}

internal fun componentInfo(vararg c: ColorComponentInfo) = listOf(*c, ColorComponentInfo("alpha", false, 0f, 1f))
