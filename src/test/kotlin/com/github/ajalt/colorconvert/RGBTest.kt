package com.github.ajalt.colorconvert

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class RGBTest {
    @Test
    fun `RGB to HSV`() {
        forall(
                row(RGB(0, 0, 0), HSV(0, 0, 0)),
                row(RGB(140, 200, 100), HSV(96, 50, 78)),
                row(RGB(96, 127, 83), HSV(102, 35, 50)),
                row(RGB(255, 255, 255), HSV(0, 0, 100))
        ) { rgb, hsv ->
            rgb.toHSV() shouldBe hsv
        }
    }

    @Test
    fun `RGB to HSL`() {
        forall(
                row(RGB(0, 0, 0), HSL(0, 0, 0)),
                row(RGB(140, 200, 100), HSL(96, 48, 59)),
                row(RGB(96, 127, 83), HSL(102, 21, 41)),
                row(RGB(255, 255, 255), HSL(0, 0, 100))
        ) { rgb, hsl ->
            rgb.toHSL() shouldBe hsl
        }
    }

    @Test
    fun `RGB to Hex`() {
        forall(
                row(RGB(0, 0, 0).toHex(), "000000"),
                row(RGB(140, 200, 100).toHex(true), "#8cc864"),
                row(RGB(255, 255, 255).toHex(), "ffffff")
        ) { actual, expected ->
            actual shouldBe expected
        }
    }

    @Test
    fun `Hex to RGB`() {
        forall(
                row("000000", RGB(0, 0, 0)),
                row("#8CC864", RGB(140, 200, 100)),
                row("ffffff", RGB(255, 255, 255))
        ) { hex, rgb ->
            RGB(hex) shouldBe rgb
        }
    }

    @Test
    fun `RGB to CMYK`() {
        forall(
                row(RGB(0, 0, 0), CMYK(0, 0, 0, 100)),
                row(RGB(255, 255, 255), CMYK(0, 0, 0, 0)),
                row(RGB(255, 0, 0), CMYK(0, 100, 100, 0)),
                row(RGB(0, 255, 0), CMYK(100, 0, 100, 0)),
                row(RGB(0, 0, 255), CMYK(100, 100, 0, 0)),
                row(RGB(255, 255, 0), CMYK(0, 0, 100, 0)),
                row(RGB(0, 255, 255), CMYK(100, 0, 0, 0)),
                row(RGB(255, 0, 255), CMYK(0, 100, 0, 0)),
                row(RGB(140, 200, 100), CMYK(30, 0, 50, 22))
        ) { rgb, cmyk ->
            rgb.toCMYK() shouldBe cmyk
        }
    }

    @Test
    fun `RGB to Ansi16`() {
        forall(
                row(RGB(0, 0, 0), 30),
                row(RGB(128, 0, 0), 31),
                row(RGB(0, 128, 0), 32),
                row(RGB(128, 128, 0), 33),
                row(RGB(0, 0, 128), 34),
                row(RGB(128, 0, 128), 35),
                row(RGB(0, 128, 128), 36),
                row(RGB(170, 170, 170), 37),
                row(RGB(255, 0, 0), 91),
                row(RGB(0, 255, 0), 92),
                row(RGB(255, 255, 0), 93),
                row(RGB(0, 0, 255), 94),
                row(RGB(255, 0, 255), 95),
                row(RGB(0, 255, 255), 96),
                row(RGB(255, 255, 255), 97)
        ) { rgb, ansi ->
            rgb.toAnsi16() shouldBe Ansi16(ansi)
        }
    }

    @Test
    fun `RGB to Ansi256`() {
        forall(
                row(RGB(0, 0, 0), 16),
                row(RGB(51, 102, 0), 64),
                row(RGB(92, 191, 84), 114),
                row(RGB(255, 255, 255), 231),
                row(RGB(100, 100, 100), 241),
                row(RGB(238, 238, 238), 254)
        ) { rgb, ansi ->
            rgb.toAnsi256() shouldBe Ansi256(ansi)
        }
    }
}
