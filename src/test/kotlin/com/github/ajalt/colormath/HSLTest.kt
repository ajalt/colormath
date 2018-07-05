package com.github.ajalt.colormath

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class HSLTest {
    @Test
    fun `HSL to RGB`() {
        forall(
                row(HSL(0, 0, 0), RGB(0, 0, 0)),
                row(HSL(96, 48, 59), RGB(140, 201, 100)),
                row(HSL(279, 73, 13), RGB(40, 9, 57)),
                row(HSL(0, 0, 100), RGB(255, 255, 255))
        ) { hsl, rgb ->
            hsl.toRGB() shouldBe rgb
        }
    }

    @Test
    fun `HSL to HSV`() {
        forall(
                row(HSL(0, 0, 0), HSV(0, 0, 0)),
                row(HSL(96, 48, 59), HSV(96, 50, 79)),
                row(HSL(279, 73, 13), HSV(279, 84, 22)),
                row(HSL(0, 0, 100), HSV(0, 0, 100))
        ) { hsl, hsv ->
            hsl.toHSV() shouldBe hsv
        }
    }

    @Test
    fun `HSL indirect conversions`() {
        forall(
                row(HSL(240, 100, 50).toAnsi16(), Ansi16(94)),
                row(HSL(240, 100, 50).toAnsi256(), Ansi256(21))
        ) { actual, expected ->
            actual shouldBe expected
        }
    }
}
