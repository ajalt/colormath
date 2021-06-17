package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color

internal fun Color.requireComponentSize(components: FloatArray) {
    require(components.size in (componentCount() - 1)..componentCount()) {
        "Invalid component array length: ${components.size}, expected ${componentCount() - 1} or ${componentCount()}"
    }
}
