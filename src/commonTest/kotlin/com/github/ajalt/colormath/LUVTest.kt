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
    fun `LUV to XYZ`() = forAll(
        row(LUV(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 100.0, 100.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 000.0, 100.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
        row(LUV(100.0, 000.0, 000.0), 0.950470, 1.000000, 1.088830),
        row(LUV(100.0, 100.0, 100.0), 1.133803, 1.000000, 0.124034),
        row(LUV(075.0, 075.0, 075.0), 0.547378, 0.482781, 0.059881),
        row(LUV(050.0, 050.0, 050.0), 0.208831, 0.184187, 0.022845),
        row(LUV(41.5279, 96.8363, 17.7521), 0.206539, 0.121972, 0.051346),
        row(LUV(55.1164, -37.5931, 44.1377), 0.142225, 0.230428, 0.104916),
        row(LUV(29.8057, -10.9632, -65.0675), 0.078188, 0.061572, 0.280960),
    ) { luv, x, y, z ->
        val xyz = luv.toXYZ()
        xyz.x.toDouble() shouldBe (x plusOrMinus 0.0005)
        xyz.y.toDouble() shouldBe (y plusOrMinus 0.0005)
        xyz.z.toDouble() shouldBe (z plusOrMinus 0.0005)
    }

    @Test
    @JsName("LUV_to_LCH")
    fun `LUV to LCH`() = forAll(
        row(LUV(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 100.0, 100.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 000.0, 100.0), 0.0, 0.0, 0.0),
        row(LUV(000.0, 100.0, 000.0), 0.0, 0.0, 0.0),
        row(LUV(100.0, 000.0, 000.0), 100.0, 0.0, 0.0),
        row(LUV(100.0, 100.0, 100.0), 100.0, 141.421, 45.0),
        row(LUV(075.0, 075.0, 075.0), 075.0, 106.066, 45.0),
        row(LUV(050.0, 050.0, 050.0), 050.0, 70.7107, 45.0),
    ) { luv, l, c, h ->
        val lch = luv.toLCH()
        lch.l.toDouble() shouldBe (l plusOrMinus 0.0005)
        lch.c.toDouble() shouldBe (c plusOrMinus 0.0005)
        lch.h.toDouble() shouldBe (h plusOrMinus 0.0005)
    }
}
