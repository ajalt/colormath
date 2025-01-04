package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.companionTest
import com.github.ajalt.colormath.convertToSpaceTest
import com.github.ajalt.colormath.model.LCHuvColorSpaces.LCHuv50
import com.github.ajalt.colormath.model.LCHuvColorSpaces.LCHuv65
import com.github.ajalt.colormath.model.LUVColorSpaces.LUV50
import com.github.ajalt.colormath.model.LUVColorSpaces.LUV65
import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class LUVTest {
    @Test
    fun roundtrip() = roundtripTest(LUV(0.01, 0.02, 0.03, 0.04))

    @Test
    fun conversion() = convertToSpaceTest(LUV65, LCHuv65, LCHuv50, HSL, to = LUV50)

    @Test
    fun companion() = companionTest(LUV, LUV65)

    @[Test JsName("LUV_to_XYZ")]
    fun `LUV to XYZ`() = testColorConversions(
        LUV(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        LUV(18.00, 18.00, 18.00) to XYZ(0.02854945, 0.02518041, 0.00312744),
        LUV(40.00, 50.00, 60.00) to XYZ(0.12749789, 0.11250974, -0.02679452),
        LUV(100.00, 100.00, 100.00) to XYZ(1.13379604, 1.0, 0.12420117),
        tolerance = 5e-4,
    )

    @[Test JsName("LUV_to_LCHuv")]
    fun `LUV to LCHuv`() = testColorConversions(
        LUV(0.00, 0.00, 0.00) to LCHuv(0.0, 0.0, Double.NaN),
        LUV(18.00, 18.00, 18.00) to LCHuv(18.0, 25.45584412, 45.0),
        LUV(40.00, 50.00, 60.00) to LCHuv(40.0, 78.10249676, 50.19442891),
        LUV(100.00, 100.00, 100.00) to LCHuv(100.0, 141.42135624, 45.0),
    )
}
