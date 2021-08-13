package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test


class HCLTest {
    @Test
    fun roundtrip() {
        HCL(0.01, 0.02, 0.03, 0.04).let { it.toHCL() shouldBeSameInstanceAs it }
        HCL(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toHCL().shouldEqualColor(it) }
    }

    @Test
    @JsName("HCL_to_LUV")
    fun `HCL to LUV`() = testColorConversions(
        HCL(0.00, 0.00, 0.00) to LUV(0.0, 0.0, 0.0),
        HCL(64.80, 0.18, 0.18) to LUV(0.18, 0.07664027, 0.16286887),
        HCL(216.00, 0.50, 0.40) to LUV(0.4, -0.4045085, -0.29389263),
        HCL(0.00, 1.00, 1.00) to LUV(1.0, 1.0, -0.0),
    )
}
