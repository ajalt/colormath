package com.github.ajalt.colormath

/**
 * Reference XYZ illuminant tristimulus values.
 *
 * See http://www.brucelindbloom.com/index.html?Eqn_ChromAdapt.html
 */
internal object Illuminant {
    data class WhitePoint(val x: Float, val y: Float, val z: Float)

    /**
     * CIE Standard Illuminant D65, using the standard 2° observer.
     *
     * `x`, `y`, and `z` are normalized for relative luminance (i.e. set `Y = 100`).
     */
    val D65 = WhitePoint(95.047f, 100.0f, 108.883f)
}

// Constants defined in the CIE standard.
// See http://www.brucelindbloom.com/index.html?LContinuity.html

/** ϵ = (6/29)^3 */
internal const val CIE_E = 216f / 24389f

/** κ = (29/3)^3 */
internal const val CIE_K = 24389f / 27f

/** ϵ × κ */
internal const val CIE_E_times_K = 8f
