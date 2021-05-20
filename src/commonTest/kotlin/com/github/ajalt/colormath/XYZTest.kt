package com.github.ajalt.colormath

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.should
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
            row(XYZ(95.0470, 100.0000, 108.8830), RGB(255, 255, 255)),
        ) { xyz, rgb ->
            xyz should convertTo(rgb)
        }
    }

    @Test
    @JsName("XYZ_to_RGB_HDR")
    fun `XYZ to RGB_HDR`() {
        val (r, g, b) = XYZ(100.0, 100.0, 100.0).toRGB()
        assertSoftly {
            withClue("r") { r shouldBe (1.08516f plusOrMinus 0.00005f) }
            withClue("g") { g shouldBe (0.97692f plusOrMinus 0.00005f) }
            withClue("b") { b shouldBe (0.95881f plusOrMinus 0.00005f) }
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
            row(XYZ(95.0470, 100.0000, 108.883), 100.0, 0.0, 0.0),
        ) { xyz, l, u, v ->
            val luv = xyz.toLUV()
            luv.l shouldBe (l.toFloat() plusOrMinus 0.005f)
            luv.u shouldBe (u.toFloat() plusOrMinus 0.005f)
            luv.v shouldBe (v.toFloat() plusOrMinus 0.005f)
        }
    }
}
