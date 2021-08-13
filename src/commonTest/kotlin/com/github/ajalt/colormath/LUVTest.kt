package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class LUVTest {
    @Test
    fun roundtrip() {
        LUV(0.01, 0.02, 0.03, 0.04).let { it.toLUV() shouldBeSameInstanceAs it }
        LUV(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toLUV().shouldEqualColor(it) }
    }

    @Test
    @JsName("LUV_to_XYZ")
    fun `LUV to XYZ`() = testColorConversions(
        LUV(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        LUV(18.00, 18.00, 18.00) to XYZ(0.02854945, 0.02518041, 0.00312744),
        LUV(40.00, 50.00, 60.00) to XYZ(0.12749789, 0.11250974, -0.02679452),
        LUV(100.00, 100.00, 100.00) to XYZ(1.13379604, 1.0, 0.12420117),
        tolerance = 5e-4,
    )

    @Test
    @JsName("LUV_to_HCL")
    fun `LUV to HCL`() = testColorConversions(
        LUV(0.00, 0.00, 0.00) to HCL(0.0, 0.0, 0.0),
        LUV(18.00, 18.00, 18.00) to HCL(45.0, 25.45584412, 18.0),
        LUV(40.00, 50.00, 60.00) to HCL(50.19442891, 78.10249676, 40.0),
        LUV(100.00, 100.00, 100.00) to HCL(45.0, 141.42135624, 100.0),
    )
}
