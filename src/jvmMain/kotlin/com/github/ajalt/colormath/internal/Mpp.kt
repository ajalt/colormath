package com.github.ajalt.colormath.internal

actual fun cbrt(float: Float): Float = Math.cbrt(float.toDouble()).toFloat()
actual fun cbrt(float: Double): Double = Math.cbrt(float)
