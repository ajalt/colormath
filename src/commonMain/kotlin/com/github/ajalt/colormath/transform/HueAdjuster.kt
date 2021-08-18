package com.github.ajalt.colormath.transform

import kotlin.math.absoluteValue
import kotlin.math.withSign

/**
 * A function that takes the difference between two hue angles and returns the new difference that should be used.
 *
 * The delta and return value are specified in degrees in the range `(-360, 360)`.
 */
typealias HueAdjustment = (delta: Float) -> Float

object HueAdjustments {
    /** Angles are adjusted so that their difference is in `[-180, 180]` */
    val shorter: HueAdjustment = {
        if (it.absoluteValue <= 180) it else it - 360f.withSign(it)
    }

    /** Angles are adjusted so that their difference is 0 or is in `[180, 360)` */
    val longer: HueAdjustment = {
        if (it == 0f || it.absoluteValue >= 180) it else it - 360f.withSign(it)
    }

    /** Angles are adjusted so that their difference is in `[0, 360)` */
    val increasing: HueAdjustment = {
        if (it >= 0) it else it + 360f
    }

    /** Angles are adjusted so that their difference is in `(-360, 0]` */
    val decreasing: HueAdjustment = {
        if (it <= 0) it else it - 360f
    }
}
