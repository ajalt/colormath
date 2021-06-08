package com.github.ajalt.colormath

import kotlin.math.PI

interface HueColor {
    /** The hue, as degrees in the range `[0, 360]` */
    val h: Float
}

/** Convert this color's hue to gradians (360° == 400 gradians) */
fun HueColor.hueAsGrad(): Float = h.degToGrad()

/** Convert this color's hue to radians (360° == 2π radians) */
fun HueColor.hueAsRad(): Float = h.degToRad()

/** Convert this color's hue to turns (360° == 1 turn) */
fun HueColor.hueAsTurns(): Float = h.degToTurns()

internal fun Float.degToRad(): Float = (this * PI / 180f).toFloat()
internal fun Float.radToDeg(): Float = (this * 180f / PI).toFloat()
internal fun Float.gradToDeg(): Float = this * .9f
internal fun Float.turnToDeg(): Float = this * 360f
internal fun Float.degToGrad(): Float = this * 200f / 180f
internal fun Float.degToTurns(): Float = this / 360f

/** Return this value shifted to lie in [0, 360] */
// formula from https://www.w3.org/TR/css-color-4/#hue-interpolation
internal fun Float.normalizeDeg(): Float = ((this % 360) + 360) % 360 //
