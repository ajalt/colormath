package com.github.ajalt.colormath

import kotlin.math.PI

interface HueColor {
    /** The hue, as degrees in the range `[0, 360]` */
    val h: Int
}

/** Convert this color's hue to gradians (360° == 400 gradians) */
fun HueColor.hueAsGrad(): Float = h * 200 / 180f

/** Convert this color's hue to radians (360° == 2π radians) */
fun HueColor.hueAsRad(): Float = (h * PI / 180).toFloat()

/** Convert this color's hue to turns (360° == 1 turn) */
fun HueColor.hueAsTurns(): Float = h / 360f

internal fun Double.degToRad(): Double = this * PI / 180
internal fun Double.radToDeg(): Double = this * 180 / PI
internal fun Double.gradToDeg(): Double = this * .9
internal fun Double.turnToDeg(): Double = this * 360
