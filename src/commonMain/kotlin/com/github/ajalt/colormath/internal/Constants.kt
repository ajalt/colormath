package com.github.ajalt.colormath.internal

internal class Illuminant(val x: Float, val y: Float, val z: Float)

/** XYZ coordinates of the D65 standard illuminant */
internal val D65 = Illuminant(.95047f, 1.000f, 1.08883f)

// Constants defined in the CIE standard.
// See http://www.brucelindbloom.com/index.html?LContinuity.html

/** ϵ = (6/29)^3 */
internal const val CIE_E = 216f / 24389f

/** κ = (29/3)^3 */
internal const val CIE_K = 24389f / 27f

/** ϵ × κ */
internal const val CIE_E_times_K = 8f
