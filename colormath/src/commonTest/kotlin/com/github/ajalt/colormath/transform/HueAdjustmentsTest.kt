package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.shouldBeFloat
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt
import kotlin.test.Test

class HueAdjustmentsTest {
    @Test
    fun nan() {
        val l = listOf(0f, Float.NaN, 10f, 20f, Float.NaN, Float.NaN)
        val ac = HueAdjustments.shorter(l)
        for ((a, ex) in ac.zip(l)) a.shouldBeFloat(ex)
    }

    @Test
    fun shorter() = doTest(
        HueAdjustments.shorter,
        listOf(0, 300, 50, 0, 100),
        listOf(0, -60, 50, 0, 100),
    )

    @Test
    fun longer() = doTest(
        HueAdjustments.longer,
        listOf(0, 300, 50, 0, 100),
        listOf(0, 300, 50, 360, 100),
    )

    @Test
    fun increasing() = doTest(
        HueAdjustments.increasing,
        listOf(0, 300, 50, 0, 100),
        listOf(0, 300, 410, 720, 820),
    )

    @Test
    fun decreasing() = doTest(
        HueAdjustments.decreasing,
        listOf(0, 300, 50, 0, 100),
        listOf(0, -60, -310, -360, -620),
    )

    @Test
    fun specified() = doTest(
        HueAdjustments.specified,
        listOf(0, 300, 50, 0, 100),
        listOf(0, 300, 50, 0, 100),
    )

    private fun doTest(adj: ComponentAdjustment, before: List<Int>, expected: List<Int>) {
        val lerp = HSL.interpolator {
            componentAdjustment("h", adj)
            before.forEach { stop(HSL(it.toDouble(), .5, .5)) }
        }
        val actual = List(before.size) { lerp.interpolate(it / before.lastIndex.toDouble()).h.roundToInt() }
        actual shouldBe expected
    }
}
