package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Color

internal fun Color.requireComponentSize(components: FloatArray) {
    require(components.size in (componentCount() - 1)..componentCount()) {
        "Invalid component array length: ${components.size}, expected ${componentCount() - 1} or ${componentCount()}"
    }
}

internal fun Color.validateComponentIndex(i: Int) {
    require(i in 0..componentCount()) { "Invalid component index $i for color with ${componentCount()} components" }
}

internal inline fun <T> Color.withValidCIndex(i: Int, block: () -> T): T {
    validateComponentIndex(i)
    return block()
}
