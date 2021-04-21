package com.github.ajalt.colormath

/**
 * Reference XYZ illuminant tristimulus values.
 *
 * See http://www.brucelindbloom.com/index.html?Eqn_ChromAdapt.html
 */
internal object Illuminant {
    data class WhitePoint(val x: Double, val y: Double, val z: Double)

    /**
     * CIE Standard Illuminant D65, using the standard 2° observer.
     *
     * `x`, `y`, and `z` are normalized for relative luminance (i.e. set `Y = 100`).
     */
    val D65 = WhitePoint(95.047, 100.0, 108.883)
}

// Constants defined in the CIE standard.
// See http://www.brucelindbloom.com/index.html?LContinuity.html

/** ϵ = (6/29)^3 */
internal const val CIE_E = 216.0 / 24389.0

/** κ = (29/3)^3 */
internal const val CIE_K = 24389.0 / 27.0

/** ϵ × κ */
internal const val CIE_E_times_K = 8.0
