package com.github.ajalt.colorconvert

import io.kotlintest.data.forall
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
}
