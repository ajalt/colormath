package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.shouldEqualColor
import com.github.ajalt.colormath.testColorConversions
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class HSLTest {
    @Test
    fun roundtrip() = roundtripTest(HSL(0.01, 0.02, 0.03, 0.04))

    @Test
    @JsName("HSL_to_RGB")
    fun `HSL to RGB`() = testColorConversions(
        HSL(Double.NaN, 0.00, 0.00) to RGB(0.0, 0.0, 0.0),
        HSL(64.80, 0.18, 0.18) to RGB(0.207216, 0.2124, 0.1476),
        HSL(144.00, 0.50, 0.60) to RGB(0.4, 0.8, 0.56),
        HSL(Double.NaN, 0.00, 1.00) to RGB(1.0, 1.0, 1.0),
    )

    @Test
    @JsName("HSL_to_HSV")
    fun `HSL to HSV`() = testColorConversions(
        HSL(0.00, 0.00, 0.00) to HSV(0.0, 0.0, 0.0),
        HSL(64.80, 0.18, 0.18) to HSV(64.8, 0.30508475, 0.2124),
        HSL(144.00, 0.50, 0.60) to HSV(144.0, 0.5, 0.8),
        HSL(0.00, 0.00, 1.00) to HSV(0.0, 0.0, 1.0),
    )

    @Test
    fun clamp() {
        forAll(
            row(HSL(0.0, 0.0, 0.0), HSL(0.0, 0.0, 0.0)),
            row(HSL(359, 1.0, 1.0), HSL(359, 1.0, 1.0)),
            row(HSL(361, 1.0, 1.0), HSL(1, 1.0, 1.0)),
            row(HSL(180, 2, 2), HSL(180, 1.0, 1.0)),
        ) { hsl, ex ->
            hsl.clamp().shouldEqualColor(ex)
        }
        val hsl = HSL(359, .9, .9, .9)
        hsl.clamp().shouldBeSameInstanceAs(hsl)
    }
}
