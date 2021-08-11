@file:Suppress("FunctionName")

package com.github.ajalt.colormath

import kotlin.jvm.JvmName

/**
 * The chromaticity coordinates of a color, represented in `xy` or `xyY`.
 *
 * [x], [y], and [z] and relative values. [X], [Y], and [Z] are absolute.
 */
data class Chromaticity(
    val x: Float,
    val y: Float,
    @get:JvmName("getAbsoluteY")
    val Y: Float=1f,
) {
    constructor(x: Double, y: Double, Y: Double = 1.0) : this(x.toFloat(), y.toFloat(), Y.toFloat())

    val z: Float get() = 1 - x - y

    @get:JvmName("getAbsoluteX")
    val X: Float get() = x * Y / y

    @get:JvmName("getAbsoluteZ")
    val Z: Float get() = (1 - x - y) * Y / y
}
