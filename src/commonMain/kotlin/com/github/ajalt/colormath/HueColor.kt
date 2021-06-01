package com.github.ajalt.colormath

import kotlin.math.PI
import kotlin.math.floor

interface HueColor {
    /** The hue, as degrees in the range `[0, 360]` */
    val h: Float
}

/** Convert this color's hue to gradians (360° == 400 gradians) */
fun HueColor.hueAsGrad(): Float = h * 200f / 180f

/** Convert this color's hue to radians (360° == 2π radians) */
fun HueColor.hueAsRad(): Float = (h * PI / 180).toFloat()

/** Convert this color's hue to turns (360° == 1 turn) */
fun HueColor.hueAsTurns(): Float = h / 360f

internal fun Float.degToRad(): Float = (this * PI / 180f).toFloat()
internal fun Float.radToDeg(): Float = (this * 180f / PI).toFloat()
internal fun Float.gradToDeg(): Float = this * .9f
internal fun Float.turnToDeg(): Float = this * 360f

/** Return this value shifted to lie in [0, 360]*/
internal fun Float.normalizeDeg(): Float = this - (floor(this / 360f) * 360f)
