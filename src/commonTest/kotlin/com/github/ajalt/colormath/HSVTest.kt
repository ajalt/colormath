package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class HSVTest {
    @Test
    fun roundtrip() {
        HSV(0.01, 0.02, 0.03, 0.04).let { it.toHSV() shouldBeSameInstanceAs it }
        HSV(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toHSV().shouldEqualColor(it) }
    }

    @Test
    @JsName("HSV_to_RGB")
    fun `HSV to RGB`() = forAll(
        row(HSV(0.0, 0.0, 0.0), RGB(0.0, 0.0, 0.0)),
        row(HSV(0.18 * 360, 0.18, 0.18), RGB(0.177408, 0.18, 0.1476)),
        row(HSV(0.25* 360, 0.5, 0.75), RGB(0.5625, 0.75, 0.375)),
        row(HSV(1.0* 360, 1.0, 1.0), RGB(1.0, 0.0, 0.0)),
    ) { hsv, rgb ->
        hsv.toSRGB().shouldEqualColor(rgb)
    }

    @Test
    @JsName("HSV_to_HSL")
    fun `HSV to HSL`() = forAll(
        row(HSV(0.0, 0.0, 0.0), HSL(0.0, 0.0, 0.0)),
        row(HSV(0.18, 0.18, 0.18), HSL(0.18, 0.0989011, 0.1638)),
        row(HSV(0.25, 0.5, 0.75), HSL(0.25, 0.42857143, 0.5625)),
        row(HSV(1.0, 1.0, 1.0), HSL(1.0, 1.0, 0.5)),
    ) { hsv, hsl ->
        hsv.toHSL().shouldEqualColor(hsl)
    }
}
