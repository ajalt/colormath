package com.github.ajalt.colormath.internal

private external val Math: dynamic
actual fun cbrt(float: Float): Float = (Math.cbrt(float) as Number).toFloat()
