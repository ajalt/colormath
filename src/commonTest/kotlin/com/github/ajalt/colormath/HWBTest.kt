package com.github.ajalt.colormath

import kotlin.js.JsName
import kotlin.test.Test

class HWBTest {
    @Test
    fun roundtrip() = roundtripTest(
        HWB(0.01, 0.02, 0.03, 0.04),
        HWB(0.01, 0.02, 0.03, 0.04f),
    )

    @Test
    @JsName("HWB_to_RGB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    // At the time of this writing, no browsers implemented hwb. These tests are based on the
    // example colors from the working draft, and a few differ by one point on a single channel,
    // presumably due to the use of a different algorithm.
    fun `HWB to RGB`() = testColorConversions(
        HWB(000.0, .400, .400) to RGB("#996666"),
        HWB(030.0, .400, .400) to RGB("#998066"),
        HWB(060.0, .400, .400) to RGB("#999966"),
        HWB(090.0, .400, .400) to RGB("#809966"),
        HWB(120.0, .400, .400) to RGB("#669966"),
        HWB(150.0, .400, .400) to RGB("#669980"),
        HWB(180.0, .400, .400) to RGB("#669999"),
        HWB(210.0, .400, .400) to RGB("#668099"),
        HWB(240.0, .400, .400) to RGB("#666699"),
        HWB(270.0, .400, .400) to RGB("#806699"),
        HWB(300.0, .400, .400) to RGB("#996699"),
        HWB(330.0, .400, .400) to RGB("#996680"),

        HWB(90.0, .000, .000) to RGB("#80ff00"),
        HWB(90.0, .600, .200) to RGB("#b3cc99"),
        HWB(90.0, .200, .600) to RGB("#4c6633"),
        HWB(00.0, .400, .600) to RGB("#666666"),
        tolerance = 5e-3,
        testInverse = false
    )
}
