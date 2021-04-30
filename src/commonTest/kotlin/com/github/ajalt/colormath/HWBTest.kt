package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class HWBTest {
    @Test
    @JsName("HWB_to_RGB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    // At the time of this writing, no browsers implemented hwb. These tests are based on the
    // example colors from the working draft, and a few differ by one point on a single channel,
    // presumably due to the use of a different algorithm.
    fun `HWB to RGB`() {
        forAll(
            row(HWB(0.0, 40.0, 40.0), RGB("#996666")),
            row(HWB(30.0, 40.0, 40.0), RGB("#998066")),
            row(HWB(60.0, 40.0, 40.0), RGB("#999966")),
            row(HWB(90.0, 40.0, 40.0), RGB("#809966")),
            row(HWB(120.0, 40.0, 40.0), RGB("#669966")),
            row(HWB(150.0, 40.0, 40.0), RGB("#669980")),
            row(HWB(180.0, 40.0, 40.0), RGB("#669999")),
            row(HWB(210.0, 40.0, 40.0), RGB("#668099")),
            row(HWB(240.0, 40.0, 40.0), RGB("#666699")),
            row(HWB(270.0, 40.0, 40.0), RGB("#806699")),
            row(HWB(300.0, 40.0, 40.0), RGB("#996699")),
            row(HWB(330.0, 40.0, 40.0), RGB("#996680")),

            row(HWB(90.0, 0.0, 0.0), RGB("#80ff00")),
            row(HWB(90.0, 60.0, 20.0), RGB("#b3cc99")),
            row(HWB(90.0, 20.0, 60.0), RGB("#4c6633")),
        ) { hwb, rgb ->
            hwb.toRGB() shouldBe rgb
        }
    }

    @Test
    @JsName("HWB_to_RGB_gray")
    // All hues convert to the same gray values
    fun `HWB to RGB gray`() {
        for (h in 0..3600) {
            HWB(h / 10.0, 0.0, 100.0).toRGB() shouldBe RGB("#000000")
            HWB(h / 10.0, 40.0, 60.0).toRGB() shouldBe RGB("#666666")
            HWB(h / 10.0, 60.0, 40.0).toRGB() shouldBe RGB("#999999")
            HWB(h / 10.0, 100.0, 0.0).toRGB() shouldBe RGB("#ffffff")
            HWB(h / 10.0, 100.0, 100.0).toRGB() shouldBe RGB("#808080")
        }
    }
}
