package com.github.ajalt.colorconvert

import io.kotlintest.data.forall
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class LABTest {
    @Test
    fun `LAB to XYZ`() {
        forall(
                row(LAB(0.0, 0.0, 0.0), 0.0, 0.0, 0.0),
                row(LAB(50.0, 50.0, 50.0), .28454, .18419, .03533),
                row(LAB(75.0, 75.0, 75.0), .77563, .48278, .07476),
                row(LAB(100.0, 100.0, 100.0), 1.64241, 1.00000, .13610),
                row(LAB(100.0, 0.0, 0.0), .95047, 1.0, 1.08883),
                row(LAB(0.0, 100.0, 0.0), 0.0, 0.0, 0.0),
                row(LAB(0.0, 0.0, 100.0), 0.0, 0.0, 0.0)
        ) { lab, x, y, z ->
            val xyz = lab.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.005)
            xyz.y shouldBe (y plusOrMinus 0.005)
            xyz.z shouldBe (z plusOrMinus 0.005)
        }
    }
}
