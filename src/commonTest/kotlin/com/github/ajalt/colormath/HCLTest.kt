package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import kotlin.js.JsName
import kotlin.test.Test


class HCLTest {
    @Test
    @JsName("HCL_to_LUV")
    fun `HCL to LUV`() = forAll(
        row(HCL(180.0, 0.0, 0.0), LUV(0.0, 0.0, 0.0)),
        row(HCL(12.1740, 179.0414, 53.2408), LUV(53.2408, 175.0151, 37.7564)),
        row(HCL(127.7236, 135.7804, 87.7347), LUV(87.7347, -83.0776, 107.3985)),
        row(HCL(265.8727, 130.6812, 32.2970), LUV(32.2970, -9.4054, -130.3423)),
        row(HCL(85.8727, 107.0643, 97.1393), LUV(97.1393, 7.7056, 106.7866)),
        row(HCL(192.1740, 72.0987, 91.1132), LUV(91.1132, -70.4773, -15.2042)),
        row(HCL(307.7236, 137.4048, 60.3242), LUV(60.3242, 84.0714, -108.6834)),
        row(HCL(126.1776, 78.3314, 69.5940), LUV(69.5940, -46.2383, 63.2284)),
    ) { hcl, luv ->
        hcl.toLUV().shouldEqualColor(luv)
    }
}
