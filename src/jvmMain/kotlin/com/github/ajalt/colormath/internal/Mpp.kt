package com.github.ajalt.colormath.internal

internal actual fun cbrt(float: Float): Float = Math.cbrt(float.toDouble()).toFloat()
internal actual fun cbrt(float: Double): Double = Math.cbrt(float)
