package com.github.ajalt.colormath

import com.github.ajalt.colormath.LCHuvColorSpaces.LCHuv50
import com.github.ajalt.colormath.LCHuvColorSpaces.LCHuv65
import com.github.ajalt.colormath.LUVColorSpaces.LUV50
import com.github.ajalt.colormath.LUVColorSpaces.LUV65
import kotlin.js.JsName
import kotlin.test.Test


class LCHuvTest {
    @Test
    fun roundtrip() = roundtripTest(LCHuv(0.01, 0.02, 0.03, 0.04), intermediate = LUV)

    @Test
    fun conversion() = convertToSpaceTest(LCHuv65, LUV65, LUV50, HSL, to = LCHuv50)

    @Test
    @JsName("LCHuv_to_LUV")
    fun `LCHuv to LUV`() = testColorConversions(
        LCHuv(0.00, 0.00, Double.NaN) to LUV(0.0, 0.0, 0.0),
        LCHuv(0.18, 0.18, 64.80) to LUV(0.18, 0.07664027, 0.16286887),
        LCHuv(0.40, 0.50, 216.00) to LUV(0.4, -0.4045085, -0.29389263),
        LCHuv(1.00, 1.00, 0.00) to LUV(1.0, 1.0, -0.0),
    )
}
