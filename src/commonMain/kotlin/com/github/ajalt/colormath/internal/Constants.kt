package com.github.ajalt.colormath.internal

// Constants defined in the CIE standard.
// See http://www.brucelindbloom.com/index.html?LContinuity.html

/** ϵ = (6/29)^3 */
internal const val CIE_E = 216f / 24389f

/** κ = (29/3)^3 */
internal const val CIE_K = 24389f / 27f

/** ϵ × κ */
internal const val CIE_E_times_K = 8f

/** The CIECAM02 transform matrix for XYZ -> LMS */
// https://en.wikipedia.org/wiki/CIECAM02#CAT02
internal val CAT02_XYZ_TO_LMS = Matrix(
    +0.7328f, +0.4296f, -0.1624f,
    -0.7036f, +1.6975f, +0.0061f,
    +0.0030f, +0.0136f, +0.9834f,
)

internal val CAT02_LMS_TO_XYZ = CAT02_XYZ_TO_LMS.inverse()

// http://www.brucelindbloom.com/WorkingSpaceInfo.html
private const val rx = 0.6400f
private const val ry = 0.3300f
//private const val rY = 0.212656f

private const val gx = 0.3000f
private const val gy = 0.6000f
//private const val gY = 0.715158f

private const val bx = 0.1500f
private const val by = 0.0600f
//private const val bY = 0.072186f

internal const val sRGB_Xr = rx / ry
internal const val sRGB_Zr = (1 - rx - ry) / ry

internal const val sRGB_Xg = gx / gy
internal const val sRGB_Zg = (1 - gx - gy) / gy

internal const val sRGB_Xb = bx / by
internal const val sRGB_Zb = (1 - bx - by) / by
