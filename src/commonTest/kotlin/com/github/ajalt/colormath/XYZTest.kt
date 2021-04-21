package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class XYZTest {
    @Test
    @JsName("XYZ_to_RGB")
    fun `XYZ to RGB`() {
        forAll(
            row(XYZ(000.0, 000.0, 000.0), RGB(0, 0, 0)),
            row(XYZ(025.0, 025.0, 025.0), RGB(149, 134, 131)),
            row(XYZ(050.0, 050.0, 050.0), RGB(204, 183, 180)),
            row(XYZ(075.0, 075.0, 075.0), RGB(244, 219, 215)),
            row(XYZ(100.0, 100.0, 100.0), RGB(255, 249, 244)),
            row(XYZ(100.0, 000.0, 000.0), RGB(255, 0, 67)),
            row(XYZ(000.0, 100.0, 000.0), RGB(0, 255, 0)),
            row(XYZ(000.0, 000.0, 100.0), RGB(0, 57, 255)),
            row(XYZ(95.0470, 100.0000, 108.8830), RGB(255, 255, 255)),
        ) { xyz, rgb ->
            xyz.toRGB() shouldBe rgb
        }
    }

    @Test
    @JsName("XYZ_to_LAB")
    fun `XYZ to LAB`() {
        forAll(
            row(XYZ(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
            row(XYZ(025.0, 025.0, 025.0), 57.075, 5.379, 3.524),
            row(XYZ(050.0, 050.0, 050.0), 76.069, 6.777, 4.440),
            row(XYZ(075.0, 075.0, 075.0), 89.393, 7.758, 5.082),
            row(XYZ(100.0, 100.0, 100.0), 100.0, 8.539, 5.594),
            row(XYZ(100.0, 000.0, 000.0), 0.0, 439.573, 0.0),
            row(XYZ(000.0, 100.0, 000.0), 100.000, -431.034, 172.414),
            row(XYZ(000.0, 000.0, 100.0), 0.000, 0.000, -166.820),
            row(XYZ(95.0470, 100.0000, 108.8830), 100.0, 0.0, 0.0),
        ) { xyz, l, a, b ->
            val lab = xyz.toLAB()
            lab.l shouldBe (l plusOrMinus 0.005)
            lab.a shouldBe (a plusOrMinus 0.005)
            lab.b shouldBe (b plusOrMinus 0.005)
        }
    }

    @Test
    @JsName("XYZ_to_LUV")
    fun `XYZ to LUV`() {
        forAll(
            row(XYZ(000.0, 000.0, 000.0), 0.0, 0.0, 0.0),
            row(XYZ(025.0, 025.0, 025.0), 57.0754, 9.4131, 3.9680),
            row(XYZ(050.0, 050.0, 050.0), 76.0693, 12.5457, 5.2885),
            row(XYZ(075.0, 075.0, 075.0), 89.3930, 14.7431, 6.2149),
            row(XYZ(100.0, 100.0, 100.0), 100.0000, 16.4924, 6.9523),
            row(XYZ(100.0, 000.0, 000.0), 0.0, 0.0, 0.0),
            row(XYZ(000.0, 100.0, 000.0), 100.0000, -257.1918, 171.1628),
            row(XYZ(000.0, 000.0, 100.0), 0.0, 0.0, 0.0),
            row(XYZ(95.0470, 100.0000, 108.8830), 100.0, 0.0, 0.0),
        ) { xyz, l, u, v ->
            val luv = xyz.toLUV()
            luv.l shouldBe (l plusOrMinus 0.005)
            luv.u shouldBe (u plusOrMinus 0.005)
            luv.v shouldBe (v plusOrMinus 0.005)
        }
    }
}
