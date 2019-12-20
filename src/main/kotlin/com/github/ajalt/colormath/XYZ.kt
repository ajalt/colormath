package com.github.ajalt.colormath

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * CIE XYZ color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [x], [y], and [z] are generally in the interval `[0, 100]`, but may be larger
 */
data class XYZ(val x: Double, val y: Double, val z: Double, val a: Float = 1f) : ConvertibleColor {
    init {
        require(x >= 0) { "x must be >= 0 in $this" }
        require(y >= 0) { "y must be >= 0 in $this" }
        require(z >= 0) { "z must be >= 0 in $this" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val x = this.x / 100
        val y = this.y / 100
        val z = this.z / 100

        // linearize sRGB values
        fun adj(c: Double): Int {
            val adj = when {
                c < 0.0031308 -> 12.92 * c
                else -> 1.055 * c.pow(1.0 / 2.4) - 0.055
            }
            return (255 * adj.coerceIn(0.0, 1.0)).roundToInt()
        }

        val r = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z
        val g = -0.9692660 * x + 1.8760108 * y + 0.0415560 * z
        val b = 0.0556434 * x - 0.2040259 * y + 1.0572252 * z
        return RGB(adj(r), adj(g), adj(b), alpha)
    }

    override fun toLAB(): LAB {
        fun f(t: Double) = when {
            t > 0.008856 -> t.pow(1 / 3.0)
            else -> (t * 7.787037) + (4 / 29.0)
        }


        val fx = f(x / 95.047)
        val fy = f(y / 100.0)
        val fz = f(z / 108.883)

        val l = (116 * fy) - 16
        val a = 500 * (fx - fy)
        val b = 200 * (fy - fz)

        return LAB(l, a, b, alpha)
    }
}
