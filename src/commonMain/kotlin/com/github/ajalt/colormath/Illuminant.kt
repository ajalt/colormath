package com.github.ajalt.colormath

/** A white point specified in CIE XYZ coordinates */
data class Illuminant(val x: Float, val y: Float, val z: Float) {
    companion object {
        fun from_xyY(x: Double, y: Double) = Illuminant((x / y).toFloat(), 1f, ((1 - x - y) / y).toFloat())

        /** CIE 1931 2° Standard Illuminant A */
        val A = from_xyY(0.44757, 0.40745)
        /** CIE 1931 2° Standard Illuminant B */
        val B = from_xyY(0.34842, 0.35161)
        /** CIE 1931 2° Standard Illuminant C */
        val C = from_xyY(0.31006, 0.31616)
        /** CIE 1931 2° Standard Illuminant D50 */
        val D50 = from_xyY(0.34567, 0.35850)
        /** CIE 1931 2° Standard Illuminant D65 */
        val D65 = from_xyY(0.31271, 0.32902)
    }
}

