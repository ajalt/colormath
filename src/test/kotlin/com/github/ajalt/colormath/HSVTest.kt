package com.github.ajalt.colormath

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class HSVTest {
    @Test
    fun `HSV to RGB`() {
        forall(
                row(HSV(0, 0, 0), RGB(0, 0, 0)),
                row(HSV(96, 50, 78), RGB(139, 199, 99)),
                row(HSV(289, 85, 87), RGB(187, 33, 222)),
                row(HSV(0, 0, 100), RGB(255, 255, 255))
        ) { hsv, rgb ->
            hsv.toRGB() shouldBe rgb
        }
    }

    @Test
    fun `HSV to HSL`() {
        forall(
                row(HSV(0, 0, 0), HSL(0, 0, 0)),
                row(HSV(96, 50, 78), HSL(96, 47, 58)),
                row(HSV(289, 85, 87), HSL(289, 74, 50)),
                row(HSV(0, 0, 100), HSL(0, 0, 100))
        ) { hsv, hsl ->
            hsv.toHSL() shouldBe hsl
        }
    }

    @Test
    fun `HSV indirect conversions`() {
        forall(
                row(HSV(240, 100, 100).toAnsi16(), Ansi16(94)),
                row(HSV(240, 100, 100).toAnsi256(), Ansi256(21))
        ) { actual, expected ->
            actual shouldBe expected
        }
    }
}
