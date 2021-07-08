package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class JzAzBzTest {
    @Test
    fun roundtrip() {
        JzAzBz(0.01, 0.011, 0.012, 0.04).let { it.toJzAzBz() shouldBeSameInstanceAs it }
        JzAzBz(0.01, 0.011, 0.012, 0.04f).let { it.toRGB().toJzAzBz().shouldEqualColor(it) }
    }

    @Test
    @JsName("JzAzBz_to_XYZ")
    fun `JzAzBz to XYZ`() = forAll(
        // https://github.com/colour-science/colour/blob/develop/colour/models/tests/test_jzazbz.py
        row(JzAzBz(0.00535048, +0.00924302, +0.00526007), XYZ(0.20654, 0.12197, 0.05136)),
        row(JzAzBz(0.00619681, -0.00608426, +0.00534077), XYZ(0.14222, 0.23042, 0.10495)),
        row(JzAzBz(0.01766826, +0.00064174, -0.00052906), XYZ(0.96907, 1.00000, 1.12179)),
    ) { jab, xyz ->
        jab.toXYZ().shouldEqualColor(xyz, 0.00001)
    }
}
