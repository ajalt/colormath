package com.github.ajalt.colorconvert

import io.kotlintest.data.forall
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class XYZTest {
    @Test
    fun `XYZ to RGB`() {
        forall(
                row(XYZ(0.0, 0.0, 0.0), RGB(0, 0, 0)),
                row(XYZ(0.25, 0.25, 0.25), RGB(149, 134, 131)),
                row(XYZ(0.5, 0.5, 0.5), RGB(204, 183, 180)),
                row(XYZ(0.75, 0.75, 0.75), RGB(244, 219, 215)),
                row(XYZ(1.0, 1.0, 1.0), RGB(255, 249, 244)),
                row(XYZ(1.0, 0.0, 0.0), RGB(255, 0, 67)),
                row(XYZ(0.0, 1.0, 0.0), RGB(0, 255, 0)),
                row(XYZ(0.0, 0.0, 1.0), RGB(0, 57, 255))
        ) { xyz, rgb ->
            xyz.toRGB() shouldBe rgb
        }
    }

    @Test
    fun `XYZ to LAB`() {
        forall(
                row(XYZ(0.0, 0.0, 0.0), 0.0, 0.0, 0.0),
                row(XYZ(0.25, 0.25, 0.25), 57.075, 5.379, 3.524),
                row(XYZ(0.5, 0.5, 0.5), 76.069, 6.777, 4.440),
                row(XYZ(0.75, 0.75, 0.75), 89.393, 7.758, 5.082),
                row(XYZ(1.0, 1.0, 1.0), 100.0, 8.539, 5.594),
                row(XYZ(1.0, 0.0, 0.0), 0.0, 439.573, 0.0),
                row(XYZ(0.0, 1.0, 0.0), 100.000, -431.034, 172.414),
                row(XYZ(0.0, 0.0, 1.0), 0.000, 0.000, -166.820)
        ) { xyz, l, a, b ->
            val lab = xyz.toLAB()
            lab.l shouldBe (l plusOrMinus 0.005)
            lab.a shouldBe (a plusOrMinus 0.005)
            lab.b shouldBe (b plusOrMinus 0.005)
        }
    }
}
