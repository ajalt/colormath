package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class HWBTest {
    @Test
    fun roundtrip() {
        HWB(0.01, 0.02, 0.03, 0.04).let { it.toHWB() shouldBeSameInstanceAs it }
        HWB(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toHWB().shouldEqualColor(it) }
    }

    @Test
    @JsName("HWB_to_RGB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    // At the time of this writing, no browsers implemented hwb. These tests are based on the
    // example colors from the working draft, and a few differ by one point on a single channel,
    // presumably due to the use of a different algorithm.
    fun `HWB to RGB`() = forAll(
        row(HWB(000.0, .400, .400), RGB("#996666")),
        row(HWB(030.0, .400, .400), RGB("#998066")),
        row(HWB(060.0, .400, .400), RGB("#999966")),
        row(HWB(090.0, .400, .400), RGB("#809966")),
        row(HWB(120.0, .400, .400), RGB("#669966")),
        row(HWB(150.0, .400, .400), RGB("#669980")),
        row(HWB(180.0, .400, .400), RGB("#669999")),
        row(HWB(210.0, .400, .400), RGB("#668099")),
        row(HWB(240.0, .400, .400), RGB("#666699")),
        row(HWB(270.0, .400, .400), RGB("#806699")),
        row(HWB(300.0, .400, .400), RGB("#996699")),
        row(HWB(330.0, .400, .400), RGB("#996680")),

        row(HWB(90.0, .000, .000), RGB("#80ff00")),
        row(HWB(90.0, .600, .200), RGB("#b3cc99")),
        row(HWB(90.0, .200, .600), RGB("#4c6633")),
    ) { hwb, rgb ->
        hwb.toSRGB().shouldEqualColor(rgb, 0.002)
    }

    @Test
    @JsName("HWB_to_RGB_gray")
    // All hues convert to the same gray values
    fun `HWB to RGB gray`() {
        for (h in 0..3600) {
            HWB(h / 10.0, .00, 1.0).toSRGB().shouldEqualColor(RGB("#000000"), 5e-3)
            HWB(h / 10.0, .40, .60).toSRGB().shouldEqualColor(RGB("#666666"), 5e-3)
            HWB(h / 10.0, .60, .40).toSRGB().shouldEqualColor(RGB("#999999"), 5e-3)
            HWB(h / 10.0, 1.0, .00).toSRGB().shouldEqualColor(RGB("#ffffff"), 5e-3)
            HWB(h / 10.0, 1.0, 1.0).toSRGB().shouldEqualColor(RGB("#808080"), 5e-3)
        }
    }
}
