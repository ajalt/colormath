package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import kotlin.js.JsName
import kotlin.test.Test

class OklabTest {
    @Test
    @JsName("Oklab_to_XYZ")
    fun `Oklab to XYZ`() = forAll(
        row(Oklab(+1.000, +0.000, +0.000), XYZ(0.950, 1.000, 1.089)),
        row(Oklab(+0.450, +1.236, -0.019), XYZ(1.000, 0.000, 0.000)),
        row(Oklab(+0.922, -0.671, +0.263), XYZ(0.000, 1.000, 0.000)),
        row(Oklab(+0.153, -1.415, -0.449), XYZ(0.000, 0.000, 1.000)),
    ) { oklab, xyz ->
        oklab.toXYZ().shouldEqualColor(xyz, 0.002f)
    }

    // Ottosson only provides test cases for XYZ <-> Oklab. These RGB cases are taken from
    // https://github.com/Evercoder/culori/blob/master/test/oklab.test.js, which is the
    // only project I can find that actually has RGB test cases.
    @Test
    @JsName("Oklab_to_RGB")
    fun `Oklab to RGB`() = forAll(
        row(Oklab(1.0000, 0.0000, 0.0000), RGB("#fff")),
        row(Oklab(0.1776, 0.0000, 0.0000), RGB("#111")),
        row(Oklab(0.0000, 0.0000, 0.0000), RGB("#000")),
        row(Oklab(0.6279, 0.2249, 0.1258), RGB("#f00")),
    ) { oklab, rgb ->
        oklab should convertTo(rgb)
    }
}
