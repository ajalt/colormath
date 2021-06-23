package com.github.ajalt.colormath.internal

import platform.posix.cbrtf

actual fun cbrt(float: Float): Float {
    return cbrtf(float)
}
