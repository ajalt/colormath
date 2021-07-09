package com.github.ajalt.colormath.transform

/**
 * A function that takes two hue angle and returns the value that should be used two hues.
 *
 * Both angles are specified in degrees in `[0, 360)`. The returned angles can be outside that range.
 */
typealias HueAdjustment = (angle1: Float, angle2: Float) -> Pair<Float, Float>

object HueAdjustments {
    /** Angles are adjusted so that `angle2 - angle1` is in `[-180, 180]` */
    val shorter: HueAdjustment = { angle1, angle2 ->
        val diff = angle2 - angle1
        when {
            diff > 180 -> (angle1 + 360f) to angle2
            diff < -180 -> angle1 to (angle2 + 360f)
            else -> angle1 to angle2
        }
    }

    /** Angles are adjusted so that `angle2 - angle1` is 0 or is in `[180, 360)` */
    val longer: HueAdjustment = { angle1, angle2 ->
        val diff = angle2 - angle1
        when {
            0 < diff && diff < 180 -> (angle1 + 360) to angle2
            -180 < diff && diff < 0 -> angle1 to (angle2 + 360f)
            else -> angle1 to angle2
        }
    }

    /** Angles are adjusted so that `angle2 - angle1` is in `[0, 360)` */
    val increasing: HueAdjustment = { angle1, angle2 ->
        when {
            angle2 < angle1 -> angle1 to (angle2 + 360f)
            else -> angle1 to angle2
        }
    }

    /** Angles are adjusted so that `angle2 - angle1` is in `(-360, 0]` */
    val decreasing: HueAdjustment = { angle1, angle2 ->
        when {
            angle1 < angle2 -> (angle1 + 360) to angle2
            else -> angle1 to angle2
        }
    }

    /** Angles are unchanged */
    val specified: HueAdjustment = { angle1, angle2 -> angle1 to angle2 }
}
