package com.github.ajalt.colormath

import kotlin.math.round

internal fun Double.roundToInt() = round(this).toInt()
internal fun Double.percentToInt() = (this * 100).roundToInt()
