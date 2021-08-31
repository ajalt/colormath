package com.github.ajalt.colormath.transform

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign


data class Point(val x: Float, val y: Float)
interface InterpolationMethod {
    interface ChannelInterpolator {
        fun interpolate(t: Float): Float
    }

    fun build(points: List<Point>): ChannelInterpolator
}

object InterpolationMethods {
    fun linear(): InterpolationMethod = object : InterpolationMethod {
        override fun build(points: List<Point>): InterpolationMethod.ChannelInterpolator = LinearInterpolator(points)
    }

    /**
     * Monotonic spline interpolation
     *
     * This method interpolates smoothly between each pair of input points, will include all the
     * input points in the output, and will not produce values outside the minimal bounding box of
     * the input points.
     *
     * ### Reference
     * Steffen, M., “A simple method for monotonic interpolation in one dimension.”, _Astronomy and
     * Astrophysics_, vol. 239, pp. 443–450, 1990.
     * [Online](https://ui.adsabs.harvard.edu/abs/1990A&A...239..443S)
     *
     * @param parabolicEndpoints If true, calculate the curves at the boundary points based on the
     *   unique parabola passing through them. By default, the boundaries are calculated by one-sided
     *   finite differences.
     */
    fun monotoneSpline(parabolicEndpoints: Boolean = false): InterpolationMethod = object : InterpolationMethod {
        override fun build(points: List<Point>): InterpolationMethod.ChannelInterpolator {
            return if (points.size < 3) linear().build(points)
            else MonotonicSplineInterpolator(points, parabolicEndpoints)
        }
    }
}


private class LinearInterpolator(private val points: List<Point>) : InterpolationMethod.ChannelInterpolator {
    init {
        require(points.size > 1) { "At least two points are required for interpolation" }
    }

    override fun interpolate(t: Float): Float {
        val start = points.indexOfLast { it.x <= t }
        if (start < 0) return points.first().y
        if (points[start].x == t || start == points.lastIndex) return points[start].y
        val end = start + 1

        val (lx, ly) = points[start]
        val (rx, ry) = points[end]
        return lerp(ly, ry, (t - lx) / (rx - lx))
    }
}


private class MonotonicSplineInterpolator(
    private val points: List<Point>,
    parabolicEndpoints: Boolean,
) : InterpolationMethod.ChannelInterpolator {
    private val n = points.lastIndex
    private fun x(i: Int) = points[i].x
    private fun y(i: Int) = points[i].y

    /** The size of the interval from i to i+1 */
    private val h = FloatArray(n) { i -> x(i + 1) - x(i) }

    /** The slope of the secant from i to i+1*/
    private val s = FloatArray(n) { i -> (y(i + 1) - y(i)) / h[i] }

    /** The slope of a unique parabola passing through i-1, i, and i+1 */
    private val p = FloatArray(points.size) { i ->
        when (i) {
            0 -> s[0] * (1 + h[0] / (h[0] + h[1])) - s[1] * (h[0] / (h[0] + h[1]))
            n -> s[n - 1] * (1 + h[n - 1] / (h[n - 1] + h[n - 2])) - s[n - 2] * (h[n - 1] / (h[n - 1] + h[n - 2]))
            else -> (s[i - 1] * h[i] + s[i] * h[i - 1]) / (h[i - 1] + h[i])
        }
    }

    /** The first order derivatives, using one-sided finite differences for the endpoints */
    private val yp = FloatArray(points.size) { i ->
        when (i) {
            0 -> when {
                parabolicEndpoints -> when {
                    p[0] * s[0] <= 0 -> 0f
                    abs(p[0]) > 2 * abs(s[0]) -> 2 * s[0]
                    else -> p[0]
                }
                else -> s[0]
            }
            n -> when {
                parabolicEndpoints -> when {
                    p[n] * s[n - 1] <= 0 -> 0f
                    abs(p[n]) > 2 * abs(s[n - 1]) -> 2 * s[n - 1]
                    else -> p[n]

                }
                else -> s[n - 1]
            }
            else -> (sign(s[i - 1]) + sign(s[i])) * minOf(abs(s[i - 1]), abs(s[i]), abs(p[i]) / 2)
        }
    }

    override fun interpolate(t: Float): Float {
        val i = points.indexOfLast { it.x <= t }.coerceIn(0, n - 1)
        val (xi, yi) = points[i]
        val xDiff = t - xi
        if (xDiff == 0f) return yi
        val ai = (yp[i] + yp[i + 1] - 2 * s[i]) / h[i].pow(2)
        val bi = (3 * s[i] - 2 * yp[i] - yp[i + 1]) / h[i]
        val f = ai * xDiff.pow(3) + bi * xDiff.pow(2) + yp[i] * xDiff + yi
        if (f.isNaN()) {
            // we need three or four non-nan points to calculate a spline, so fallback to lerp when necessary
            return when {
                t <= x(0) -> y(0)
                t >= x(n) -> y(n)
                else -> lerp(yi, y(i + 1), xDiff / (x(i + 1) - xi))
            }
        }
        return f
    }
}

private fun lerp(l: Float, r: Float, t: Float): Float {
    return when {
        l.isNaN() -> r
        r.isNaN() -> l
        else -> l + t * (r - l)
    }
}

