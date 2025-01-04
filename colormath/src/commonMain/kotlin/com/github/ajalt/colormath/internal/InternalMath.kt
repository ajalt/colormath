package com.github.ajalt.colormath.internal

import kotlin.math.*

internal fun Float.degToRad(): Float = toDouble().degToRad().toFloat()
internal fun Float.radToDeg(): Float = toDouble().radToDeg().toFloat()
internal fun Float.gradToDeg(): Float = this * .9f
internal fun Float.turnToDeg(): Float = this * 360f
internal fun Float.degToGrad(): Float = this * 200f / 180f
internal fun Float.degToTurns(): Float = this / 360f

internal fun Double.radToDeg(): Double = (this * 180.0 / PI)
internal fun Double.degToRad(): Double = (this * PI / 180.0)

internal fun cosDeg(deg: Double) = cos(deg.degToRad())
internal fun sinDeg(deg: Double) = sin(deg.degToRad())


// formula from https://www.w3.org/TR/css-color-4/#hue-interpolation
/** Return this value shifted to lie in [0, 360] */
internal fun Float.normalizeDeg(): Float = ((this % 360f) + 360f) % 360f
internal fun Double.normalizeDeg(): Double = ((this % 360.0) + 360.0) % 360.0

internal fun Float.nanToOne(): Float = if (isNaN()) 1f else this

// Used for LAB <-> LCHab, LUV <-> LCHuv, Oklab <-> Oklch, JAB <-> JCH
// https://www.w3.org/TR/css-color-4/#lab-to-lch
// https://bottosson.github.io/posts/oklab/#the-oklab-color-space
// https://en.wikipedia.org/wiki/CIELUV#Cylindrical_representation_.28CIELCH.29
internal inline fun <T> toPolarModel(a: Float, b: Float, block: (c: Float, h: Float) -> T): T {
    val c = sqrt(a * a + b * b)
    val h = if (c > -1e-7 && c < 1e-7) Float.NaN else atan2(b, a).radToDeg()
    return block(c, h.normalizeDeg())
}

internal inline fun <T> fromPolarModel(c: Float, h: Float, block: (a: Float, b: Float) -> T): T {
    val hDegrees = if (h.isNaN()) 0f else h.degToRad()
    val a = c * cos(hDegrees)
    val b = c * sin(hDegrees)
    return block(a, b)
}

/**
 * return `sign(a) * |a|^p`, which avoids NaN when `this` is negative
 */
internal fun Double.spow(p: Double): Double = absoluteValue.pow(p).withSign(this)
internal fun Float.spow(p: Double): Double = toDouble().spow(p)

internal fun sqrtSumSq(a: Float, b: Float, c: Float): Float = sqrt(a.pow(2) + b.pow(2) + c.pow(2))
internal fun sqrtSumSq(a: Double, b: Double): Double = sqrt(a.pow(2) + b.pow(2))
internal fun sqrtSumSq(a: Double, b: Double, c: Double): Double =
    sqrt(a.pow(2) + b.pow(2) + c.pow(2))

internal fun scaleRange(l1: Float, r1: Float, l2: Float, r2: Float, t: Float): Float {
    return if (r1 == l1) t else (r2 - l2) * (t - l1) / (r1 - l1) + l2
}
