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
                row(LUV(41.5279, 96.8363, 17.7521), 020.6539, 012.1972, 005.1346),
                row(LUV(55.1164, -37.5931, 44.1377), 014.2225, 023.0428, 010.4916),
                row(LUV(29.8057, -10.9632, -65.0675), 007.8188, 006.1572, 028.0960),
        ) { luv, x, y, z ->
            val xyz = luv.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.0005)
            xyz.y shouldBe (y plusOrMinus 0.0005)
            xyz.z shouldBe (z plusOrMinus 0.0005)
        }
    }

    @Test
    @JsName("LUV_to_LCH")
    fun `LUV to LCH`() {
        forAll(
            row(LUV(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
            row(LUV(000.0, 100.0, 100.0), 0.0, 0.0, 0.0),
            row(LUV(000.0, 000.0, 100.0), 0.0, 0.0, 0.0),
            row(LUV(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
            row(LUV(100.0, 000.0, 000.0), 095.0470, 100.000, 108.8830),
            row(LUV(100.0, 100.0, 100.0), 113.3803, 100.0000, 012.4034),
            row(LUV(075.0, 075.0, 075.0), 054.7378, 048.2781, 005.9881),
            row(LUV(050.0, 050.0, 050.0), 020.8831, 018.4187, 002.2845),
            row(LUV(41.5279, 96.8363, 17.7521), 020.6539, 012.1972, 005.1346),
            row(LUV(55.1164, -37.5931, 44.1377), 014.2225, 023.0428, 010.4916),
            row(LUV(29.8057, -10.9632, -65.0675), 007.8188, 006.1572, 028.0960),
        ) { luv, x, y, z ->
            val xyz = luv.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.0005)
            xyz.y shouldBe (y plusOrMinus 0.0005)
            xyz.z shouldBe (z plusOrMinus 0.0005)
        }
    }
}
