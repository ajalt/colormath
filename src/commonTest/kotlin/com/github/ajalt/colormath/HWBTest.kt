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
            row(HWB(0f, 40f, 40f), RGB("#996666")),
            row(HWB(30f, 40f, 40f), RGB("#998066")),
            row(HWB(60f, 40f, 40f), RGB("#999966")),
            row(HWB(90f, 40f, 40f), RGB("#809966")),
            row(HWB(120f, 40f, 40f), RGB("#669966")),
            row(HWB(150f, 40f, 40f), RGB("#669980")),
            row(HWB(180f, 40f, 40f), RGB("#669999")),
            row(HWB(210f, 40f, 40f), RGB("#668099")),
            row(HWB(240f, 40f, 40f), RGB("#666699")),
            row(HWB(270f, 40f, 40f), RGB("#806699")),
            row(HWB(300f, 40f, 40f), RGB("#996699")),
            row(HWB(330f, 40f, 40f), RGB("#996680")),

            row(HWB(90f, 0f, 0f), RGB("#80ff00")),
            row(HWB(90f, 60f, 20f), RGB("#b3cc99")),
            row(HWB(90f, 20f, 60f), RGB("#4c6633")),
        ) { hwb, rgb ->
            hwb.toRGB() shouldBe rgb
        }
    }

    @Test
    @JsName("HWB_to_RGB_gray")
    // All hues convert to the same gray values
    fun `HWB to RGB gray`() {
        for (h in 0..3600) {
            HWB(h / 10f, 0f, 100f).toRGB() shouldBe RGB("#000000")
            HWB(h / 10f, 40f, 60f).toRGB() shouldBe RGB("#666666")
            HWB(h / 10f, 60f, 40f).toRGB() shouldBe RGB("#999999")
            HWB(h / 10f, 100f, 0f).toRGB() shouldBe RGB("#ffffff")
            HWB(h / 10f, 100f, 100f).toRGB() shouldBe RGB("#808080")
        }
    }
}
