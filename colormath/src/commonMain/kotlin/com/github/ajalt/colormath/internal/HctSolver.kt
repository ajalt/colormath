package com.github.ajalt.colormath.internal

import com.github.ajalt.colormath.model.CAM16ViewingConditions
import com.github.ajalt.colormath.model.RGBColorSpaces.SRGB
import com.github.ajalt.colormath.model.yFromLstar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

// https://github.com/material-foundation/material-color-utilities

private val XYZ_TO_CAM16RGB = arrayOf(
    doubleArrayOf(0.401288, 0.650173, -0.051461),
    doubleArrayOf(-0.250268, 1.204414, 0.045854),
    doubleArrayOf(-0.002079, 0.048952, 0.953127),
)

// These matrices are derived from colormath's sRGB matrix so that the solver is exactly consistent
// with the conversion in the other direction, which routes through colormath's XYZ.
private val SCALED_DISCOUNT_FROM_LINRGB = run {
    val vc = CAM16ViewingConditions.DEFAULT
    val srgbToXyz = SRGB.matrixToXyz
    Array(3) { i ->
        DoubleArray(3) { j ->
            val xyzColumn = DoubleArray(3) { k -> srgbToXyz[k * 3 + j].toDouble() }
            val cam = XYZ_TO_CAM16RGB[i]
            vc.fl / 100.0 * vc.rgbD[i] *
                    (cam[0] * xyzColumn[0] + cam[1] * xyzColumn[1] + cam[2] * xyzColumn[2])
        }
    }
}

private val LINRGB_FROM_SCALED_DISCOUNT = inverse3(SCALED_DISCOUNT_FROM_LINRGB)

private val K_R = SRGB.matrixToXyz[3].toDouble()
private val K_G = SRGB.matrixToXyz[4].toDouble()
private val K_B = SRGB.matrixToXyz[5].toDouble()

// The linear rgb coordinates of the planes between adjacent 8-bit srgb component values
private val CRITICAL_PLANES = DoubleArray(255) { trueLinearized(it + 0.5) }

/**
 * Find a color in the sRGB gamut with the given HCT [hueDegrees], [chroma], and [lstar] tone.
 *
 * Returns linear sRGB coordinates in the range `[0, 100]`. If no color with the requested chroma
 * is in gamut, the returned color has the requested hue and tone, and the largest chroma that is
 * in gamut.
 */
internal fun solveHctToLinearSrgb(hueDegrees: Double, chroma: Double, lstar: Double): DoubleArray {
    if (chroma < 0.0001 || lstar < 0.0001 || lstar > 99.9999) {
        val y = yFromLstar(lstar.coerceIn(0.0, 100.0))
        return doubleArrayOf(y, y, y)
    }
    val hueRadians = hueDegrees.normalizeDeg().degToRad()
    val y = yFromLstar(lstar)
    return findResultByJ(hueRadians, chroma, y) ?: bisectToLimit(y, hueRadians)
}

private fun inverse3(m: Array<DoubleArray>): Array<DoubleArray> {
    val c00 = m[1][1] * m[2][2] - m[1][2] * m[2][1]
    val c01 = m[1][2] * m[2][0] - m[1][0] * m[2][2]
    val c02 = m[1][0] * m[2][1] - m[1][1] * m[2][0]
    val det = m[0][0] * c00 + m[0][1] * c01 + m[0][2] * c02
    return arrayOf(
        doubleArrayOf(
            c00,
            m[0][2] * m[2][1] - m[0][1] * m[2][2],
            m[0][1] * m[1][2] - m[0][2] * m[1][1]
        ),
        doubleArrayOf(
            c01,
            m[0][0] * m[2][2] - m[0][2] * m[2][0],
            m[0][2] * m[1][0] - m[0][0] * m[1][2]
        ),
        doubleArrayOf(
            c02,
            m[0][1] * m[2][0] - m[0][0] * m[2][1],
            m[0][0] * m[1][1] - m[0][1] * m[1][0]
        ),
    ).also { for (row in it) for (j in 0..2) row[j] /= det }
}

private fun trueLinearized(rgbComponent: Double): Double {
    val normalized = rgbComponent / 255.0
    return when {
        normalized <= 0.040449936 -> normalized / 12.92 * 100.0
        else -> ((normalized + 0.055) / 1.055).pow(2.4) * 100.0
    }
}

private fun trueDelinearized(rgbComponent: Double): Double {
    val normalized = rgbComponent / 100.0
    val delinearized = when {
        normalized <= 0.0031308 -> normalized * 12.92
        else -> 1.055 * normalized.pow(1.0 / 2.4) - 0.055
    }
    return delinearized * 255.0
}

private fun mul3(v0: Double, v1: Double, v2: Double, m: Array<DoubleArray>): DoubleArray {
    return DoubleArray(3) { v0 * m[it][0] + v1 * m[it][1] + v2 * m[it][2] }
}

private fun chromaticAdaptation(component: Double): Double {
    val af = abs(component).pow(0.42)
    return sign(component) * 400.0 * af / (af + 27.13)
}

private fun inverseChromaticAdaptation(adapted: Double): Double {
    val base = max(0.0, 27.13 * abs(adapted) / (400.0 - abs(adapted)))
    return sign(adapted) * base.pow(1.0 / 0.42)
}

/** The hue of a linear RGB color in CAM16, in radians */
private fun hueOf(linrgb: DoubleArray): Double {
    val scaled = mul3(linrgb[0], linrgb[1], linrgb[2], SCALED_DISCOUNT_FROM_LINRGB)
    val rA = chromaticAdaptation(scaled[0])
    val gA = chromaticAdaptation(scaled[1])
    val bA = chromaticAdaptation(scaled[2])
    val a = (11.0 * rA + -12.0 * gA + bA) / 11.0
    val b = (rA + gA - 2.0 * bA) / 9.0
    return atan2(b, a)
}

private fun sanitizeRadians(angle: Double): Double = (angle + PI * 8) % (PI * 2)

private fun areInCyclicOrder(a: Double, b: Double, c: Double): Boolean {
    return sanitizeRadians(b - a) < sanitizeRadians(c - a)
}

/** Intersect the segment AB with the plane perpendicular to [axis] at [coordinate] */
private fun setCoordinate(
    source: DoubleArray,
    coordinate: Double,
    target: DoubleArray,
    axis: Int,
): DoubleArray {
    val t = (coordinate - source[axis]) / (target[axis] - source[axis])
    return DoubleArray(3) { source[it] + (target[it] - source[it]) * t }
}

/**
 * The nth possible vertex of the polygonal intersection of the y plane and the RGB cube, in
 * linear RGB coordinates, or null if this possible vertex lies outside of the cube.
 */
private fun nthVertex(y: Double, n: Int): DoubleArray? {
    val coordA = if (n % 4 <= 1) 0.0 else 100.0
    val coordB = if (n % 2 == 0) 0.0 else 100.0
    val (r, g, b) = when {
        n < 4 -> Triple((y - coordA * K_G - coordB * K_B) / K_R, coordA, coordB)
        n < 8 -> Triple(coordB, (y - coordB * K_R - coordA * K_B) / K_G, coordA)
        else -> Triple(coordA, coordB, (y - coordA * K_R - coordB * K_G) / K_B)
    }
    return when {
        r in 0.0..100.0 && g in 0.0..100.0 && b in 0.0..100.0 -> doubleArrayOf(r, g, b)
        else -> null
    }
}

/** The two endpoints of the segment of the y plane's boundary containing the target hue */
private fun bisectToSegment(y: Double, targetHue: Double): Pair<DoubleArray, DoubleArray> {
    var left: DoubleArray? = null
    var right: DoubleArray? = null
    var leftHue = 0.0
    var rightHue = 0.0
    var uncut = true
    for (n in 0 until 12) {
        val mid = nthVertex(y, n) ?: continue
        val midHue = hueOf(mid)
        if (left == null || right == null) {
            left = mid
            right = mid
            leftHue = midHue
            rightHue = midHue
            continue
        }
        if (uncut || areInCyclicOrder(leftHue, midHue, rightHue)) {
            uncut = false
            if (areInCyclicOrder(leftHue, targetHue, midHue)) {
                right = mid
                rightHue = midHue
            } else {
                left = mid
                leftHue = midHue
            }
        }
    }
    return checkNotNull(left) to checkNotNull(right)
}

/** A color with the given [y] and [targetHue] on the boundary of the sRGB cube */
private fun bisectToLimit(y: Double, targetHue: Double): DoubleArray {
    var (left, right) = bisectToSegment(y, targetHue)
    var leftHue = hueOf(left)
    for (axis in 0 until 3) {
        if (left[axis] != right[axis]) {
            var lPlane: Int
            var rPlane: Int
            if (left[axis] < right[axis]) {
                lPlane = floor(trueDelinearized(left[axis]) - 0.5).toInt()
                rPlane = ceil(trueDelinearized(right[axis]) - 0.5).toInt()
            } else {
                lPlane = ceil(trueDelinearized(left[axis]) - 0.5).toInt()
                rPlane = floor(trueDelinearized(right[axis]) - 0.5).toInt()
            }
            for (i in 0 until 8) {
                if (abs(rPlane - lPlane) <= 1) break
                val mPlane = floor((lPlane + rPlane) / 2.0).toInt()
                val mid = setCoordinate(left, CRITICAL_PLANES[mPlane], right, axis)
                val midHue = hueOf(mid)
                if (areInCyclicOrder(leftHue, targetHue, midHue)) {
                    right = mid
                    rPlane = mPlane
                } else {
                    left = mid
                    leftHue = midHue
                    lPlane = mPlane
                }
            }
        }
    }
    return DoubleArray(3) { (left[it] + right[it]) / 2 }
}

/** A color with the given hue, chroma, and y, found with Newton iteration on CAM16 lightness, or null */
private fun findResultByJ(hueRadians: Double, chroma: Double, y: Double): DoubleArray? {
    var j = sqrt(y) * 11.0
    val vc = CAM16ViewingConditions.DEFAULT
    val tInnerCoeff = 1 / (1.64 - 0.29.pow(vc.n)).pow(0.73)
    val eHue = 0.25 * (cos(hueRadians + 2.0) + 3.8)
    val p1 = eHue * (50000.0 / 13.0) * vc.nc * vc.ncb
    val hSin = sin(hueRadians)
    val hCos = cos(hueRadians)
    for (iterationRound in 0 until 5) {
        val jNormalized = j / 100.0
        val alpha = if (chroma == 0.0 || j == 0.0) 0.0 else chroma / sqrt(jNormalized)
        val t = (alpha * tInnerCoeff).pow(1.0 / 0.9)
        val ac = vc.aw * jNormalized.pow(1.0 / vc.c / vc.z)
        val p2 = ac / vc.nbb
        val gamma = 23.0 * (p2 + 0.305) * t / (23.0 * p1 + 11 * t * hCos + 108.0 * t * hSin)
        val a = gamma * hCos
        val b = gamma * hSin
        val rA = (460.0 * p2 + 451.0 * a + 288.0 * b) / 1403.0
        val gA = (460.0 * p2 - 891.0 * a - 261.0 * b) / 1403.0
        val bA = (460.0 * p2 - 220.0 * a - 6300.0 * b) / 1403.0
        val linrgb = mul3(
            inverseChromaticAdaptation(rA),
            inverseChromaticAdaptation(gA),
            inverseChromaticAdaptation(bA),
            LINRGB_FROM_SCALED_DISCOUNT,
        )
        if (linrgb[0] < 0 || linrgb[1] < 0 || linrgb[2] < 0) return null
        val fnj = K_R * linrgb[0] + K_G * linrgb[1] + K_B * linrgb[2]
        if (fnj <= 0) return null
        if (iterationRound == 4 || abs(fnj - y) < 0.002) {
            if (linrgb[0] > 100.01 || linrgb[1] > 100.01 || linrgb[2] > 100.01) return null
            return linrgb
        }
        // Newton's method with 2 * fn(j) / j approximating fn'(j)
        j -= (fnj - y) * j / (2 * fnj)
    }
    return null
}
