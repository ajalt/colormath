package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.internal.normalizeDeg
import kotlin.math.absoluteValue
import kotlin.math.withSign

object HueAdjustments {
    /** Angles are adjusted so that their difference is in `[-180, 180]` */
    val shorter: ComponentAdjustment = deltaAdjustment {
        if (it.absoluteValue <= 180) it else it - 360f.withSign(it)
    }

    /** Angles are adjusted so that their difference is 0 or is in `[180, 360)` */
    val longer: ComponentAdjustment = deltaAdjustment {
        if (it == 0f || it.absoluteValue >= 180) it else it - 360f.withSign(it)
    }

    /** Angles are adjusted so that their difference is in `[0, 360)` */
    val increasing: ComponentAdjustment = deltaAdjustment {
        if (it >= 0) it else it + 360f
    }

    /** Angles are adjusted so that their difference is in `(-360, 0]` */
    val decreasing: ComponentAdjustment = deltaAdjustment {
        if (it <= 0) it else it - 360f
    }

    /**
     * Leave all angles unchanged
     */
    val specified: ComponentAdjustment = { it }
}

private inline fun deltaAdjustment(crossinline adj: (delta: Float) -> Float): ComponentAdjustment =
    { hues ->
        hues.toMutableList().also { h ->
            h[0] = h[0].normalizeDeg()
            for (i in 1..h.lastIndex) {
                val hue = h[i]
                val prev = h[i - 1]
                if (hue.isNaN() || prev.isNaN()) continue
                h[i] = prev + adj(hue.normalizeDeg() - prev.normalizeDeg())
            }
        }
    }
