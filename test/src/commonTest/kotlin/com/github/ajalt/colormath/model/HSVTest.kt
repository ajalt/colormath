package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class HSVTest {
    @Test
    fun roundtrip() = roundtripTest(HSV(0.01, 0.02, 0.03, 0.04))

    @[Test JsName("HSV_to_RGB")]
    fun `HSV to RGB`() = testColorConversions(
        HSV(Double.NaN, 0.00, 0.00) to RGB(0.0, 0.0, 0.0),
        HSV(64.80, 0.18, 0.18) to RGB(0.177408, 0.18, 0.1476),
        HSV(144.00, 0.50, 0.60) to RGB(0.3, 0.6, 0.42),
        HSV(0.00, 1.00, 1.00) to RGB(1.0, 0.0, 0.0),
    )

    @[Test JsName("HSV_to_RGB_NaN_S")]
    fun `HSV to RGB NaN S`() = testColorConversions(
        HSV(Double.NaN, Double.NaN, 0.00) to RGB(0.0, 0.0, 0.0),
        HSV(0.00, Double.NaN, 0.00) to RGB(0.0, 0.0, 0.0),
        HSV(Double.NaN, Double.NaN, 0.50) to RGB(0.5, 0.5, 0.5),
        testInverse = false,
    )

    @[Test JsName("HSV_to_HSL")]
    fun `HSV to HSL`() = testColorConversions(
        HSV(0.00, 0.00, 0.00) to HSL(0.0, 0.0, 0.0),
        HSV(64.80, 0.18, 0.18) to HSL(64.8, 0.0989011, 0.1638),
        HSV(144.00, 0.50, 0.60) to HSL(144.0, 0.33333333, 0.45),
        HSV(0.00, 1.00, 1.00) to HSL(0.0, 1.0, 0.5),
    )
}
