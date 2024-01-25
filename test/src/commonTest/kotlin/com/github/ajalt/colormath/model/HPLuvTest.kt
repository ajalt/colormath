package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class HPLuvTest {
    @Test
    fun roundtrip() = roundtripTest(HPLuv(0.1, 0.011, 0.012, 0.04), intermediate = LCHuv)

    @Test
    @JsName("LCHuv_to_HPLuv")
    fun `LCHuv to HPLuv`() = testColorConversions(
        LCHuv(0.00, 0.00, Double.NaN) to HPLuv(Double.NaN, 0.0, 0.0),
        LCHuv(0.18, 0.18, 64.80) to HPLuv(64.8, 126.8934854430029, 0.18),
        LCHuv(0.40, 0.50, 216.00) to HPLuv(216.0, 158.6168568037536, 0.4),
        LCHuv(1.00, 1.00, 0.00) to HPLuv(0.0, 126.89348544300287, 1.0),
    )
}
