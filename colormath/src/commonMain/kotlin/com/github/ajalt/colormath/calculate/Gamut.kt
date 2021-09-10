package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.Color

/**
 * Return `true` if all channels of this color, when converted to sRGB, lie in the range `[0, 1]`
 */
fun Color.isInSRGBGamut(): Boolean = toSRGB().let {
    it.r in 0f..1f && it.g in 0f..1f && it.b in 0f..1f
}
