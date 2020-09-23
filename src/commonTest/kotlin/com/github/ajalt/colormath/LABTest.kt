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
    fun `LAB to XYZ`() {
        forAll(
                row(LAB(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
                row(LAB(050.0, 050.0, 050.0), 028.454, 018.419, 003.533),
                row(LAB(075.0, 075.0, 075.0), 077.563, 048.278, 007.476),
                row(LAB(100.0, 100.0, 100.0), 164.241, 100.000, 013.610),
                row(LAB(100.0, 000.0, 000.0), 095.047, 100.000, 108.883),
                row(LAB(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
                row(LAB(000.0, 000.0, 100.0), 0.0, 0.0, 0.0)
        ) { lab, x, y, z ->
            val xyz = lab.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.005)
            xyz.y shouldBe (y plusOrMinus 0.005)
            xyz.z shouldBe (z plusOrMinus 0.005)
        }
    }
}
