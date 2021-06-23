package com.github.ajalt.colormath.internal

actual fun cbrt(float: Float): Float {
    return Math.cbrt(float.toDouble()).toFloat()
}
