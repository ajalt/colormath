package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test


class LCHTest {
    @Test
    @JsName("LCH_to_LUV")
    fun `LCH to LUV`() {
        forAll(
            row(LCH(0.0, 0.0, 180.0), 0.0, 0.0, 0.0),
            row(LCH(53.2408, 179.0414, 12.1740), 53.2408, 175.0151, 37.7564),
            row(LCH(87.7347, 135.7804, 127.7236), 87.7347, -83.0776, 107.3985),
            row(LCH(32.2970, 130.6812, 265.8727), 32.2970, -9.4054, -130.3423),
            row(LCH(97.1393, 107.0643, 85.8727), 97.1393, 7.7056, 106.7866),
            row(LCH(91.1132, 72.0987, 192.1740), 91.1132, -70.4773, -15.2042),
            row(LCH(60.3242, 137.4048, 307.7236), 60.3242, 84.0714, -108.6834),
            row(LCH(69.5940, 78.3314, 126.1776), 69.5940, -46.2383, 63.2284),
        ) { lch, l, u, v ->
            val luv = lch.toLUV()
            luv.l shouldBe (l plusOrMinus 0.0005)
            luv.u shouldBe (u plusOrMinus 0.0005)
            luv.v shouldBe (v plusOrMinus 0.0005)
        }
    }
}
