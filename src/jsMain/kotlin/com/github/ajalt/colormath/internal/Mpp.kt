package com.github.ajalt.colormath.internal

private external val Math: dynamic
internal actual fun cbrt(float: Float): Float = (Math.cbrt(float) as Number).toFloat()
internal actual fun cbrt(float: Double): Double = (Math.cbrt(float) as Number).toDouble()
