package com.github.ajalt.colorconvert

/**
 * CIE XYZ color space.
 *
 * Conversions use D65 reference white, and sRGB profile.
 *
 * [x], [y], and [z] are generally in the interval [0, 1], but may be as large as 1.5
 */
data class XYZ(val x: Double, val y: Double, val z: Double) : ConvertibleColor {
    init {
        require(x in 0.0..1.5) { "x must be in interval [0, 1.5] in $this" }
        require(y in 0.0..1.5) { "y must be in interval [0, 1.5] in $this" }
        require(z in 0.0..1.5) { "z must be in interval [0, 1.5] in $this" }
    }

    override fun toRGB(): RGB {
        // linearize sRGB values
        fun adj(c: Double): Int {
            val adj = when {
                c < 0.0031308 -> 12.92 * c
                else -> 1.055 * Math.pow(c, 0.41666) - 0.055
            }
            return (255 * adj.coerceIn(0.0, 1.0)).roundToInt()
        }
        // matrix based on D50 reference white sRGB
        val r = 3.2404542 * x - 1.5371385 * y - 0.4985314 * z
        val g = -0.9692660 * x + 1.8760108 * y + 0.0415560 * z
        val b = 0.0556434 * x - 0.2040259 * y + 1.0572252 * z
        return RGB(adj(r), adj(g), adj(b))
    }
}
