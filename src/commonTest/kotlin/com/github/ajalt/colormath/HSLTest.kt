package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class HSLTest {
    @Test
    @JsName("HSL_to_RGB")
    fun `HSL to RGB`() = forAll(
        row(HSL(0, 0, 0), RGB(0, 0, 0)),
        row(HSL(180, 0, 0), RGB(0, 0, 0)),
        row(HSL(96, 48, 59), RGB(140, 201, 100)),
        row(HSL(279, 73, 13), RGB(40, 9, 57)),
        row(HSL(0, 0, 100), RGB(255, 255, 255))
    ) { hsl, rgb ->
        hsl should convertTo(rgb)
    }

    @Test
    @JsName("HSL_to_HSV")
    fun `HSL to HSV`() = forAll(
        row(HSL(0, 0, 0), HSV(0, 0, 0)),
        row(HSL(96, 48, 59), HSV(96, 50, 79)),
        row(HSL(279, 73, 13), HSV(279, 84, 22)),
        row(HSL(0, 0, 100), HSV(0, 0, 100))
    ) { hsl, hsv ->
        val (h, s, v) = hsl.toHSV()
        h shouldBe (hsv.h plusOrMinus 0.005f)
        s shouldBe (hsv.s plusOrMinus 0.005f)
        v shouldBe (hsv.v plusOrMinus 0.005f)
    }

    @Test
    @JsName("HSL_indirect_conversions")
    fun `HSL indirect conversions`() = forAll(
        row(HSL(240, 100, 50).toAnsi16(), Ansi16(94)),
        row(HSL(240, 100, 50).toAnsi256(), Ansi256(21))
    ) { actual, expected ->
        actual shouldBe expected
    }
}
