@file:Suppress("FunctionName")

package com.github.ajalt.colormath

/**
 * A color represented in XYZ coordinates.
 *
 * A [Chromaticity] can also be constructed from xyY coordinates with [from_xy].
 */
class Chromaticity(val x: Float, val y: Float, val z: Float) {
    /**
     * Construct a chromaticity from XZ coordinates (i.e. CIEXYZ with `Y=1`).
     */
    constructor(x: Float, z: Float) : this(x, 1f, z)

    companion object {
        /**
         * Create a [Chromaticity] from relative xy coordinates (i.e. xyY with `Y=1`)
         */
        fun from_xy(x: Float, y: Float): Chromaticity {
            return Chromaticity(x / y, (1 - x - y) / y)
        }
    }
}
