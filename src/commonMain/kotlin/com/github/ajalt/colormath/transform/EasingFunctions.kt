package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.internal.scaleRange
import kotlin.math.absoluteValue

/**
 * A function for use with interpolator [easing][InterpolatorBuilder.easing].
 */
fun interface EasingFunction {
    /**
     * Apply an easing function to a parameter [t] in the range `[0, 1]`.
     *
     * The return value should be 0 when [t] is 0, and 1 when [t] is 1.
     */
    fun ease(t: Double): Double
}

/**
 * Built-in easing functions for use with interpolator [easing][InterpolatorBuilder.easing].
 */
object EasingFunctions {

    /**
     * A cubic Bézier easing function
     *
     * The function is defined by two points, ([x1], [y1]), and ([x2], [y2]) that define the P₁ and
     * P₂ control points of a cubic Bézier curve. The curve's endpoints P₀ and P₃ are fixed at `(0,
     * 0)` and `(1, 1)` respectively.
     *
     * [x1] and [x2] must be in the range `[0, 1]`.
     */
    fun cubicBezier(x1: Number, y1: Number, x2: Number, y2: Number): EasingFunction {
        return CubicBezierEasing(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
    }

    /**
     * The default linear easing function
     */
    fun linear(): EasingFunction = EasingFunction { it }

    /**
     * A linear easing function that sets the midpoint at a given [position].
     *
     * @param position A value in `[0, 1]` indicating where the midpoint should be. A value of 0.5
     *   will behave the same as [linear] easing.
     */
    fun midpoint(position: Number): EasingFunction {
        val p = position.toDouble()
        if (p <= 0) return EasingFunction { 1.0 }
        if (p >= 1) return EasingFunction { 0.0 }
        return EasingFunction { t ->
            when {
                t <= p -> scaleRange(0.0, p, 0.0, 0.5, t)
                else -> scaleRange(p, 1.0, 0.5, 1.0, t)
            }
        }
    }
}

/**
 * P₀ is always (0, 0), and P₁ is always (1, 1)
 */
private class CubicBezierEasing(
    private val x1: Double,
    private val y1: Double,
    private val x2: Double,
    private val y2: Double,
) : EasingFunction {
    init {
        require(x1 in 0.0..1.0 && x2 in 0.0..1.0) { "Bezier x coordinates must be in the range [0, 1]" }
    }

    private companion object {
        const val EPSILON = 1e-7
        const val NEWTON_ITERATIONS = 4
    }

    // Calculate the polynomial coefficients
    private val cx = 3.0 * x1
    private val cy = 3.0 * y1

    private val bx = 3.0 * (x2 - x1) - cx
    private val by = 3.0 * (y2 - y1) - cy

    private val ax = 1 - cx - bx
    private val ay = 1 - cy - by

    /** Get the `x` value of the curve at a given [t] */
    private fun sampleCurveX(t: Double): Double {
        // aₓt³ + bₓt² + cₓt
        return ((ax * t + bx) * t + cx) * t
    }

    /** Get the `y` value of the curve at a given [t] */
    private fun sampleCurveY(t: Double): Double {
        return ((ay * t + by) * t + cy) * t
    }

    /** Get the derivative `x'` value of the curve at a given [t] */
    private fun sampleCurveXDerivative(t: Double): Double {
        // 3aₓt² + 2bₓt + cₓ
        return (3.0 * ax * t + 2.0 * bx) * t + cx
    }

    private fun findTFromX(x: Double): Double {
        var t = x
        // Fast path: newton's method
        for (i in 0..NEWTON_ITERATIONS) {
            val x2 = sampleCurveX(t)
            if (x.approxEq(x2, EPSILON)) {
                return t
            }
            val dx = sampleCurveXDerivative(t)
            // If the derivative is too small, we won't converge
            if (dx.approxEq(0.0, 1e-6)) break
            t -= (x2 - x) / dx
        }

        // Slow path: bisection
        var lo = 0.0
        var hi = 1.0
        t = x
        if (t < lo) return lo
        if (t > hi) return hi

        while (lo < hi) {
            val x2 = sampleCurveX(t)
            if (x2.approxEq(x, EPSILON)) return t
            if (x > x2) lo = t
            else hi = t
            t = (hi - lo) / 2.0 + lo
        }

        return t
    }

    // The `t` value coming in is the `x` value of the curve. We need to find the `t` value of the
    // curve in order to find its `y` value.
    override fun ease(x: Double): Double {
        // edge cases handled according to https://www.w3.org/TR/css-easing-1/#cubic-bezier-algo
        return when {
            x < 0 -> {
                when {
                    x1 > 0 -> tangent(0.0, 0.0, x1, y1, x)
                    x2 > 0 -> tangent(0.0, 0.0, x2, y2, x)
                    else -> 0.0
                }
            }
            x > 1 -> {
                when {
                    x2 < 1 -> tangent(x2, y2, 1.0, 1.0, x)
                    x1 < 1 -> tangent(x1, y1, 1.0, 1.0, x)
                    else -> 1.0
                }
            }
            else -> sampleCurveY(findTFromX(x))
        }
    }

    private fun tangent(x1: Double, y1: Double, x2: Double, y2: Double, x: Double): Double {
        return (y2 - y1) / (x2 - x1) * (x - x1) + y1
    }

    private fun Double.approxEq(b: Double, epsilon: Double): Boolean {
        return (this - b).absoluteValue < epsilon
    }
}
