package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.degToGrad
import com.github.ajalt.colormath.internal.degToRad
import com.github.ajalt.colormath.internal.degToTurns

interface HueColor: Color {
    /** The hue, as degrees in the range `[0, 360)` */
    val h: Float
}

/** Convert this color's hue to gradians (360° == 400 gradians) */
fun HueColor.hueAsGrad(): Float = h.degToGrad()

/** Convert this color's hue to radians (360° == 2π radians) */
fun HueColor.hueAsRad(): Float = h.degToRad()

/** Convert this color's hue to turns (360° == 1 turn) */
fun HueColor.hueAsTurns(): Float = h.degToTurns()
