package com.github.ajalt.colormath

import kotlin.Double.Companion.NaN
import kotlin.js.JsName
import kotlin.test.Test


class LCHabTest {
    @Test
    fun roundtrip() = roundtripTest(
        LCHab(0.1, 0.011, 0.015, 0.04),
        LCHab(0.1, 0.011, 0.015, 0.04f),
        intermediate = LAB,
    )

    @Test
    @JsName("LCHab_to_LAB")
    fun `LCHab to LAB`() = testColorConversions(
        LCHab(0.00, 0.00, NaN) to LAB(0.0, 0.0, 0.0),
        LCHab(18.00, 18.00, 64.80) to LAB(18.0, 7.66402725, 16.28688694),
        LCHab(40.00, 50.00, 216.00) to LAB(40.0, -40.45084972, -29.38926261),
        LCHab(100.00, 100.00, 0.00) to LAB(100.0, 100.0, -0.0),
    )
}
