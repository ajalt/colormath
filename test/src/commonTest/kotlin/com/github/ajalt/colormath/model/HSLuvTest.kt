package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class HSLuvTest {
    @Test
    fun roundtrip() = roundtripTest(HSLuv(0.1, 0.011, 0.012, 0.04), intermediate = LCHuv)

    // Test cases generated from the reference implementation
    @[Test JsName("LCHuv_to_HSLuv")]
    fun `LCHuv to HSLuv`() = testColorConversions(
        LCHuv(0.00, 0.00, Double.NaN) to HSLuv(Double.NaN, 0.0, 0.0),
        LCHuv(0.18, 0.18, 64.80) to HSLuv(64.8, 86.24411410293375, 0.18),
        LCHuv(0.40, 0.50, 216.00) to HSLuv(216.0, 138.871331171819, 0.4),
        LCHuv(1.00, 1.00, 0.00) to HSLuv(0.0, 36.33223336102162, 1.0),
    )
}
