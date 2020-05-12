package com.github.ajalt.colormath

import io.kotlintest.data.forall
import io.kotlintest.matchers.doubles.plusOrMinus
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test


private fun packRgb(red: Int, green: Int, blue: Int): Int {
    // Formula matches Android's Color.java
    return -0x1000000 or (red shl 16) or (green shl 8) or blue
}

class RGBTest {
    @Test
    fun `RGB from bytes`() {
        forall(
                row(RGB(Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE), RGB(0, 0, 0)),
                row(RGB(Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE), RGB(255, 255, 255)),
                row(RGB(0.toByte(), 0.toByte(), 0.toByte()), RGB(128, 128, 128))
        ) { actual: RGB, expected: RGB ->
            actual shouldBe expected
        }
    }

    @Test
    fun `RGB from packed`() {
        forall(
                row(0, 0, 0),
                row(76, 127, 201),
                row(255, 255, 255)
        ) { r, g, b ->
            RGB.fromInt(packRgb(r, g, b)) shouldBe RGB(r, g, b)
        }
    }

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
                row(RGB(0, 0, 0).toHex(false), "000000"),
                row(RGB(140, 200, 100).toHex(), "#8cc864"),
                row(RGB(140, 200, 100).toHex(true), "#8cc864"),
                row(RGB(255, 255, 255).toHex(false), "ffffff")
        ) { actual, expected ->
            actual shouldBe expected
        }
    }

    @Test
    fun `Hex to RGB`() {
        forall(
                row("000000", RGB(0, 0, 0)),
                row("#8CC864", RGB(140, 200, 100)),
                row("ffffff", RGB(255, 255, 255)),
                row("ffffff00", RGB(255, 255, 255, 0f)),
                row("#ffffff00", RGB(255, 255, 255, 0f))
        ) { hex, rgb ->
            RGB(hex) shouldBe rgb
        }
    }

    @Test
    fun `RGB to XYZ`() {
        forall(
                row(RGB(0, 0, 0), 0.0, 0.0, 0.0),
                row(RGB(255, 255, 255), 95.04, 100.00, 108.88),
                row(RGB(255, 0, 0), 41.24, 21.26, 1.93),
                row(RGB(0, 255, 0), 35.75, 71.51, 11.91),
                row(RGB(0, 0, 255), 18.04, 7.21, 95.03),
                row(RGB(255, 255, 0), 77.00, 92.78, 13.85),
                row(RGB(0, 255, 255), 53.80, 78.73, 106.95),
                row(RGB(255, 0, 255), 59.28, 28.48, 96.964),
                row(RGB(92, 191, 84), 24.64, 40.17, 14.84)
        ) { rgb, x, y, z ->
            val xyz = rgb.toXYZ()
            xyz.x shouldBe (x plusOrMinus 0.01)
            xyz.y shouldBe (y plusOrMinus 0.01)
            xyz.z shouldBe (z plusOrMinus 0.01)
        }
    }

    @Test
    fun `RGB to LAB`() {
        forall(
                row(RGB(0, 0, 0), 0.0, 0.0, 0.0),
                row(RGB(255, 255, 255), 100.0, 0.0, 0.0),
                row(RGB(255, 0, 0), 53.24, 80.09, 67.20),
                row(RGB(0, 255, 0), 87.73, -86.18, 83.17),
                row(RGB(0, 0, 255), 32.29, 79.18, -107.86),
                row(RGB(255, 255, 0), 97.13, -21.55, 94.47),
                row(RGB(0, 255, 255), 91.11, -48.08, -14.13),
                row(RGB(255, 0, 255), 60.32, 98.23, -60.82),
                row(RGB(92, 191, 84), 69.59, -50.11, 44.64)
        ) { rgb, l, a, b ->
            val xyz = rgb.toLAB()
            xyz.l shouldBe (l plusOrMinus 0.01)
            xyz.a shouldBe (a plusOrMinus 0.01)
            xyz.b shouldBe (b plusOrMinus 0.01)
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

    @Test
    fun toPackedInt() {
        assertAll(
                10000,
                Gen.choose(0, 255),
                Gen.choose(0, 255),
                Gen.choose(0, 255),
                Gen.choose(0, 255)
        ) { r, g, b, a ->
            val rgb = RGB(r, g, b, a / 255f)
            RGB.fromInt(rgb.toPackedInt()) shouldBe rgb
        }
    }
}
