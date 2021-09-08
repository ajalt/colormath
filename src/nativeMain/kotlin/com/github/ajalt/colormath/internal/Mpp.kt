package com.github.ajalt.colormath.internal

import platform.posix.cbrtf

internal actual fun cbrt(float: Float): Float = cbrtf(float)
internal actual fun cbrt(float: Double): Double = platform.posix.cbrt(float)
