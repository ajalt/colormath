package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test


class LCHTest {
    @Test
    fun roundtrip() {
        LCH(0.1, 0.011, 0.015, 0.04).let { it.toLCH() shouldBeSameInstanceAs it }
        LCH(0.1, 0.011, 0.015, 0.04f).let { it.toSRGB().toLCH().shouldEqualColor(it, 0.001) }
    }

    @Test
    @JsName("LCH_to_LAB")
    fun `LCH to LAB`() = forAll(
        row(LCH(0.0, 0.0, 0.0), LAB(0.0, 0.0, 0.0)),
        row(LCH(100.0, 0.0, 0.0), LAB(100.0, 0.0, 0.0)),
        row(LCH(53.2408, 104.5518, 39.9990), LAB(53.2408, 80.0925, 67.2032)),
        row(LCH(42.3746, 0.0000, 180.0000), LAB(42.3746, 0.0, 0.0)),
        row(LCH50(53.2408, 104.5518, 39.9990), LAB50(53.2408, 80.0925, 67.2032)),
    ) { lch, lab ->
        lch.toLAB().shouldEqualColor(lab)
    }
}
