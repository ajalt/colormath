package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class LUVTest {
    @Test
    @JsName("LUV_to_XYZ")
    fun `LUV to XYZ`() {
        forAll(
                row(LUV(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
                row(LUV(000.0, 100.0, 100.0), 0.0, 0.0, 0.0),
                row(LUV(000.0, 000.0, 100.0), 0.0, 0.0, 0.0),
                row(LUV(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
                row(LUV(100.0, 000.0, 000.0), 095.0470, 100.000, 108.8830),
                row(LUV(100.0, 100.0, 100.0), 113.3803, 100.0000, 012.4034),
                row(LUV(075.0, 075.0, 075.0), 054.7378, 048.2781, 005.9881),
                row(LUV(050.0, 050.0, 050.0), 020.8831, 018.4187, 002.2845),
                row(LUV(41.52787529, 96.83626054, 17.75210149), 020.654008, 012.197225, 005.136952),
                row(LUV(55.11636304, -37.59308176, 44.13768458), 014.222010, 023.042768, 010.495772),
                row(LUV(29.80565520, -10.96316802, -65.06751860), 007.818780, 006.157201, 028.099326),
        ) { luv, x, y, z ->
            val xyz = luv.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.5)
            xyz.y shouldBe (y plusOrMinus 0.5)
            xyz.z shouldBe (z plusOrMinus 0.5)
        }
    }
}
