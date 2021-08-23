package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.internal.*
import kotlin.jvm.JvmInline
import kotlin.math.*

/**
 * Calculate the euclidean distance between this color and [other].
 *
 * Note that this formula assumes a rectangular color space, and will not return meaningful results
 * in spaces with polar coordinates such as [HSV].
 */
fun <T : Color> T.euclideanDistance(other: T): Float {
    val c1 = toArray()
    val c2 = other.toArray()
    return sqrtSumSq(c1[0] - c2[0], c1[1] - c2[1], c1[2] - c2[2])
}

/**
 * Calculate the difference ΔEab between this color and [other] using the CIE 1976 recommendation.
 *
 * @return a value in the range `[0, 100]`, with 0 meaning the colors are identical.
 */
// http://brucelindbloom.com/Eqn_DeltaE_CIE76.html
fun Color.differenceCIE76(other: Color): Float {
    val (l1, a1, b1) = DoubleLab(toLAB())
    val (l2, a2, b2) = DoubleLab(other.toLAB())
    return sqrtSumSq(l1 - l2, a1 - a2, b1 - b2).toFloat()
}

/**
 * Calculate the difference ΔEab between this color and [other] using the CIE 1994 recommendation.
 *
 * You can specify [textiles]`=true` to use the _textiles_ formula constants. By default, the _graphic
 * arts_ constants are used.
 *
 * @return a value in the range `[0, 100]`, with 0 meaning the colors are identical.
 */
// http://brucelindbloom.com/Eqn_DeltaE_CIE94.html
fun Color.differenceCIE94(other: Color, textiles: Boolean = false): Float {
    val (l1, a1, b1) = DoubleLab(toLAB())
    val (l2, a2, b2) = DoubleLab(other.toLAB())
    val kL = if (textiles) 2 else 1
    val k1 = if (textiles) 0.048 else 0.045
    val k2 = if (textiles) 0.014 else 0.015
    val c1 = sqrtSumSq(a1, b1)
    val c2 = sqrtSumSq(a2, b2)
    val da = a1 - a2
    val db = b1 - b2
    val dl = l1 - l2
    val dc = c1 - c2
    val dh = sqrt(da.pow(2) + db.pow(2) - dc.pow(2))
    val sc = 1 + k1 * c1
    val sh = 1 + k2 * c1
    return sqrtSumSq(dl / kL, dc / sc, dh / sh).toFloat()
}

/**
 * Calculate the difference ΔEab between this color and [other] using the CIE 2000 recommendation.
 *
 * @return a value in the range `[0, 100]`, with 0 meaning the colors are identical.
 */
// http://brucelindbloom.com/Eqn_DeltaE_CIE2000.html
fun Color.differenceCIE2000(other: Color): Float {
    val (l1, a1, b1) = DoubleLab(toLAB())
    val (l2, a2, b2) = DoubleLab(other.toLAB())

    val lbp = (l1 + l2) / 2
    val c1 = sqrtSumSq(a1, b1)
    val c2 = sqrtSumSq(a2, b2)
    val cb = (c1 + c2) / 2
    val cb7 = cb.pow(7)
    val g = (1 - sqrt(cb7 / (cb7 + 25.0.pow(7))))
    val ap1 = a1 * (1 + g)
    val ap2 = a2 * (1 + g)
    val cp1 = sqrtSumSq(ap1, b1)
    val cp2 = sqrtSumSq(ap2, b2)
    val cbp = (cp1 + cp2) / 2
    val hp1 = atan2(b1, ap1).radToDeg().normalizeDeg()
    val hp2 = atan2(b2, ap2).radToDeg().normalizeDeg()
    val hpDiff = abs(hp1 - hp2)
    val hbp = when {
        hpDiff > 180 -> (hp1 + hp2 + 360) / 2
        else -> (hp1 + hp2) / 2
    }
    val t = (1 -
            0.17 * cosDeg(hbp - 30) +
            0.24 * cosDeg(2 * hbp) +
            0.32 * cosDeg(3 * hbp + 6) -
            0.20 * cosDeg(4 * hbp - 63))

    val dhp = when {
        hpDiff <= 180 -> hp2 - hp1
        hp2 <= hp1 -> hp2 - hp1 + 360
        else -> hp2 - hp1 - 360
    }

    val dlp = l2 - l1
    val dcp = cp2 - cp1
    val dHp = 2 * sqrt(cp1 * cp2) * sinDeg(dhp / 2)
    val sl = 1 + (0.015 * (lbp - 50).pow(2)) / sqrt(20 + (lbp - 50).pow(2))
    val sc = 1 + 0.045 * cbp
    val sh = 1 + 0.015 * cbp * t
    val dTheta = 30 * exp(-((hbp - 275) / 25).pow(2))
    val cbp7 = cbp.pow(7)
    val rc = sqrt(cbp7 / (cbp7 + 25.0.pow(7)))
    val rt = -2 * rc * sinDeg(2 * dTheta)
    return sqrt(
        (dlp / sl).pow(2) +
                (dcp / sc).pow(2) +
                (dHp / sh).pow(2) +
                rt * (dcp / sc) * (dHp / sh)
    ).toFloat()
}

fun Color.differenceCMC(other: Color, l: Float = 2f, c: Float = 1f): Float {
    val (l1, a1, b1) = DoubleLab(toLAB())
    val (l2, a2, b2) = DoubleLab(other.toLAB())

    val c1 = sqrtSumSq(a1, b1)
    val c2 = sqrtSumSq(a2, b2)

    val dl = l1 - l2
    val da = a1 - a2
    val db = b1 - b2
    val dc = c1 - c2

    val h1 = atan2(b1, a1).radToDeg().normalizeDeg()
    val t = when {
        h1 in 164.0..345.0 -> 0.56 + abs(0.2 * cosDeg(h1 + 168))
        else -> 0.36 + abs(0.4 * cosDeg(h1 + 35))
    }
    val f = sqrt(c1.pow(4) / (c1.pow(4) + 1900))

    val sl = when {
        l1 < 16 -> 0.511
        else -> 0.040975 * l1 / (1 + 0.01765 * l1)
    }

    val sc = 0.0638 * c1 / (1 + 0.0131 * c1) + 0.638
    val sh = sc * (f * t + 1 - f)
    val dh2 = da.pow(2) + db.pow(2) - dc.pow(2) //(ΔH)²

    val v1 = dl / (l * sl)
    val v2 = dc / (c * sc)
    val sqrt = sqrt(v1.pow(2) + v2.pow(2) + (dh2 / sh.pow(2)))
    return sqrt.toFloat()
}

/**
 * Calculate the difference ΔEz between this color and [other] using the [JzAzBz] color space.
 *
 * @return a value in the range `[0, 1]`, with 0 meaning the colors are identical.
 */
fun Color.differenceEz(other: Color): Float {
    val (j1, c1, h1) = DoubleJch(toJzCzHz())
    val (j2, c2, h2) = DoubleJch(other.toJzCzHz())
    val dH2 = 2 * c1 * c2 * (1 - cosDeg(h2 - h1)) // this is (ΔHz)²
    return sqrt((j2 - j1).pow(2) + (c2 - c1).pow(2) + dH2).toFloat()
}

@JvmInline
private value class DoubleLab(private val lab: LAB) {
    operator fun component1() = lab.l.toDouble()
    operator fun component2() = lab.a.toDouble()
    operator fun component3() = lab.b.toDouble()
}

@JvmInline
private value class DoubleJch(private val jch: JzCzHz) {
    operator fun component1() = jch.j.toDouble()
    operator fun component2() = jch.c.toDouble()
    operator fun component3() = jch.h.toDouble()
}
