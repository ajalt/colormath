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
    fun `HWB to RGB`() {
        forAll(
            row(HWB(0f, 40f, 40f), RGB("#996666")),
//            row(HWB(30f, 40f, 40f), RGB("#998066")),
//            row(HWB(60f, 40f, 40f), RGB("#999966")),
//            row(HWB(90f, 40f, 40f), RGB("#809966")),
//            row(HWB(120f, 40f, 40f), RGB("#669966")),
//            row(HWB(150f, 40f, 40f), RGB("#66997f")),
//            row(HWB(180f, 40f, 40f), RGB("#669999")),
//            row(HWB(210f, 40f, 40f), RGB("#667f99")),
//            row(HWB(240f, 40f, 40f), RGB("#666699")),
//            row(HWB(270f, 40f, 40f), RGB("#7f6699")),
//            row(HWB(300f, 40f, 40f), RGB("#996699")),
//            row(HWB(330f, 40f, 40f), RGB("#996680")),
//
//            row(HWB(90f, 0f, 0f), RGB("#80ff00")),
//            row(HWB(90f, 0f, 100f), RGB("#000000")),
//            row(HWB(90f, 100f, 0f), RGB("#ffffff")),
//            row(HWB(90f, 100f, 100f), RGB("#808080")),
//            row(HWB(90f, 100f, 20f), RGB("#d4d5d5")),
//            row(HWB(90f, 20f, 100f), RGB("#2a2a2b")),
        ) { hwb, rgb ->
            hwb.toRGB() shouldBe rgb
        }
    }
}
