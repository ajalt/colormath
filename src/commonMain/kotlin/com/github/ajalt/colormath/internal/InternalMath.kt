package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.Illuminant
import kotlin.math.*

internal fun Float.degToRad(): Float = (this * PI / 180f).toFloat()
internal fun Float.radToDeg(): Float = (this * 180f / PI).toFloat()
internal fun Float.gradToDeg(): Float = this * .9f
internal fun Float.turnToDeg(): Float = this * 360f
internal fun Float.degToGrad(): Float = this * 200f / 180f
internal fun Float.degToTurns(): Float = this / 360f

/** Return this value shifted to lie in [0, 360] */
// formula from https://www.w3.org/TR/css-color-4/#hue-interpolation
internal fun Float.normalizeDeg(): Float = ((this % 360) + 360) % 360

// Used for LAB <-> LCH, Oklab <-> Oklch, LUV -> HCL
// https://www.w3.org/TR/css-color-4/#lab-to-lch
// https://bottosson.github.io/posts/oklab/#the-oklab-color-space
// https://en.wikipedia.org/wiki/CIELUV#Cylindrical_representation_.28CIELCH.29
internal inline fun <T> toPolarModel(a: Float, b: Float, block: (c: Float, h: Float) -> T): T {
    val c = sqrt(a * a + b * b)
    val h = if (c < 1e-7) 0f else atan2(b, a).radToDeg()
    return block(c, h.normalizeDeg())
}

internal inline fun <T> fromPolarModel(c: Float, h: Float, block: (a: Float, b: Float) -> T): T {
    val hDegrees = h.degToRad()
    val a = c * cos(hDegrees)
    val b = c * sin(hDegrees)
    return block(a, b)
}


// http://www.brucelindbloom.com/Eqn_XYZ_to_RGB.html
internal fun srgbToXyzMatrix(whitePoint: Illuminant): Matrix {
    val s = Matrix(
        sRGB_Xr, sRGB_Xg, sRGB_Xb,
        1f, 1f, 1f,
        sRGB_Zr, sRGB_Zg, sRGB_Zb,
    ).inverse(inPlace = true).times(whitePoint.x, whitePoint.y, whitePoint.z)

    return Matrix(
        s.r * sRGB_Xr, s.g * sRGB_Xg, s.b * sRGB_Xb,
        s.r * 1.0000f, s.g * 1.0000f, s.b * 1.0000f,
        s.r * sRGB_Zr, s.g * sRGB_Zg, s.b * sRGB_Zb,
    )
}
