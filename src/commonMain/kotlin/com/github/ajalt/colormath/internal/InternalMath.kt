package com.github.ajalt.colormath.internal

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

// Used for LAB <-> LCH, Oklab <-> Oklch, LUV <-> HCL, JAB <-> JCH
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

/**
 * return `sign(a) * |a|^p`, which avoids NaN when `this` is negative
 */
internal fun Double.spow(p: Double) = absoluteValue.pow(p).withSign(this)
internal fun Float.spow(p: Double) = toDouble().let { it.absoluteValue.pow(p).withSign(it) }
