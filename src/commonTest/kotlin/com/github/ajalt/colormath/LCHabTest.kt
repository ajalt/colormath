package com.github.ajalt.colormath

import com.github.ajalt.colormath.LABColorSpaces.LAB50
import com.github.ajalt.colormath.LABColorSpaces.LAB65
import com.github.ajalt.colormath.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.LCHabColorSpaces.LCHab65
import kotlin.js.JsName
import kotlin.test.Test


class LCHabTest {
    @Test
    fun roundtrip() = roundtripTest(LCHab(0.1, 0.011, 0.015, 0.04), intermediate = LAB)

    @Test
    fun conversion() = convertToSpaceTest(LAB65, LCHab65, LAB50, HSL, to = LCHab50)

    @Test
    @JsName("LCHab_to_LAB")
    fun `LCHab to LAB`() = testColorConversions(
        LCHab(0.00, 0.00, Double.NaN) to LAB(0.0, 0.0, 0.0),
        LCHab(18.00, 18.00, 64.80) to LAB(18.0, 7.66402725, 16.28688694),
        LCHab(40.00, 50.00, 216.00) to LAB(40.0, -40.45084972, -29.38926261),
        LCHab(100.00, 100.00, 0.00) to LAB(100.0, 100.0, -0.0),
    )
}
