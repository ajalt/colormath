package com.github.ajalt.colormath.internal

import platform.posix.cbrtf

actual fun cbrt(float: Float): Float = cbrtf(float)
actual fun cbrt(float: Double): Double = platform.posix.cbrt(float)
