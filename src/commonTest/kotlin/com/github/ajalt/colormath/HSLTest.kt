package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class HSLTest {
    @Test
    fun roundtrip() {
        HSL(0.01, 0.02, 0.03, 0.04).let { it.toHSL() shouldBeSameInstanceAs it }
        HSL(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toHSL().shouldEqualColor(it) }
    }

    @Test
    @JsName("HSL_to_RGB")
    fun `HSL to RGB`() = forAll(
        row(HSL(0.0, 0.0, 0.0), RGB(0.0, 0.0, 0.0)),
        row(HSL(0.18 * 360, 0.18, 0.18), RGB(0.207216, 0.2124, 0.1476)),
        row(HSL(0.25 * 360, 0.5, 0.75), RGB(0.75, 0.875, 0.625)),
        row(HSL(1.0 * 360, 1.0, 1.0), RGB(1.0, 1.0, 1.0)),
    ) { hsl, rgb ->
        hsl.toSRGB().shouldEqualColor(rgb)
    }

    @Test
    @JsName("HSL_to_HSV")
    fun `HSL to HSV`() = forAll(
        row(HSL(0.0, 0.0, 0.0), HSV(0.0, 0.0, 0.0)),
        row(HSL(0.18 * 360, 0.18, 0.18), HSV(0.18 * 360, 0.30508475, 0.2124)),
        row(HSL(0.25 * 360, 0.5, 0.75), HSV(0.25 * 360, 0.28571429, 0.875)),
        row(HSL(1.0 * 360, 1.0, 1.0), HSV(0.0, 0.0, 1.0)),
    ) { hsl, hsv ->
        hsl.toHSV().shouldEqualColor(hsv)
    }
}
