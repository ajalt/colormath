package com.github.ajalt.colormath.internal

import kotlin.native.concurrent.SharedImmutable

// Constants used in LAB and LUV conversions.
// http://www.brucelindbloom.com/index.html?LContinuity.html
/** ϵ = (6/29)^3 */
internal const val CIE_E = 216.0 / 24389.0

/** κ = (29/3)^3 */
internal const val CIE_K = 24389.0 / 27.0

/** ϵ × κ */
internal const val CIE_E_times_K = 8.0

/** The CIECAM02 transform matrix for XYZ -> LMS */
// https://en.wikipedia.org/wiki/CIECAM02#CAT02
@SharedImmutable
internal val CAT02_XYZ_TO_LMS = Matrix(
    +0.7328f, +0.4296f, -0.1624f,
    -0.7036f, +1.6975f, +0.0061f,
    +0.0030f, +0.0136f, +0.9834f,
)

@SharedImmutable
internal val CAT02_LMS_TO_XYZ = CAT02_XYZ_TO_LMS.inverse()
