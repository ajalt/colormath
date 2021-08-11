package com.github.ajalt.colormath

/** A named chromaticity */
data class WhitePoint(val name: String, val chromaticity: xyY) {
    override fun toString(): String = name
}

/**
 * Standard CIE Illuminants under the 2° observer
 */
object Illuminant {
    /** CIE 1931 2° Standard Illuminant A */
    val A = WhitePoint("A", xyY(0.44758, 0.40745))

    /** CIE 1931 2° Standard Illuminant B */
    val B = WhitePoint("B", xyY(0.34842, 0.35161))

    /** CIE 1931 2° Standard Illuminant C */
    val C = WhitePoint("C", xyY(0.31006, 0.31616))

    /** CIE 1931 2° Standard Illuminant D50 */
    val D50 = WhitePoint("D50", xyY(0.34570, 0.35850))

    /** CIE 1931 2° Standard Illuminant D55 */
    val D55 = WhitePoint("D55", xyY(0.33243, 0.34744))

    /** CIE 1931 2° Standard Illuminant D65 */
    val D65 = WhitePoint("D65", xyY(0.31270, 0.32900))

    /** CIE 1931 2° Standard Illuminant D75 */
    val D75 = WhitePoint("D75", xyY(0.29903, 0.31488))

    /** CIE 1931 2° Standard Illuminant E */
    val E = WhitePoint("E", xyY(1.0 / 3.0, 1.0 / 3.0))
}
