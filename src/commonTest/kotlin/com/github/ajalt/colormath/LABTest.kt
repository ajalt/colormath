package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class LABTest {
    @Test
    @JsName("LAB_to_XYZ")
    fun `LAB to XYZ`() = forAll(
        row(LAB(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
        row(LAB(050.0, 050.0, 050.0), 0.28454, 0.18419, 0.03533),
        row(LAB(075.0, 075.0, 075.0), 0.77563, 0.48278, 0.07476),
        row(LAB(100.0, 100.0, 100.0), 1.64241, 1.00000, 0.13610),
        row(LAB(100.0, 000.0, 000.0), 0.95047, 1.00000, 1.08883),
        row(LAB(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
        row(LAB(000.0, 000.0, 100.0), 0.0, 0.0, 0.0)
    ) { lab, x, y, z ->
        val xyz = lab.toXYZ()
        xyz.x.toDouble() shouldBe (x plusOrMinus 0.0005)
        xyz.y.toDouble() shouldBe (y plusOrMinus 0.0005)
        xyz.z.toDouble() shouldBe (z plusOrMinus 0.0005)
    }
}
