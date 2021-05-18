package com.github.ajalt.colormath

import io.kotest.assertions.withClue
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test

class RGBTest {
    private fun packRgb(red: Int, green: Int, blue: Int): Int {
        // Formula matches Android's Color.java
        return -0x1000000 or (red shl 16) or (green shl 8) or blue
    }

    @Test
    @JsName("RGB_from_bytes")
    fun `RGB from bytes`() {
        forAll(
            row(RGB(Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE), RGB(0, 0, 0)),
            row(RGB(Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE), RGB(255, 255, 255)),
            row(RGB(0.toByte(), 0.toByte(), 0.toByte()), RGB(128, 128, 128))
        ) { actual: RGB, expected: RGB ->
            actual shouldBe expected
        }
    }

    @Test
    @JsName("RGB_to_HSV")
    fun `RGB to HSV`() {
        forAll(
            row(RGB(0, 0, 0), HSV(0, 0, 0)),
            row(RGB(140, 200, 100), HSV(96, 50, 78)),
            row(RGB(96, 127, 83), HSV(102, 35, 50)),
            row(RGB(255, 255, 255), HSV(0, 0, 100))
        ) { rgb, hsv ->
            rgb.toHSV() shouldBe hsv
        }
    }

    @Test
    @JsName("RGB_to_HSL")
    fun `RGB to HSL`() {
        forAll(
            row(RGB(0, 0, 0), HSL(0, 0, 0)),
            row(RGB(140, 200, 100), HSL(96, 48, 59)),
            row(RGB(96, 127, 83), HSL(102, 21, 41)),
            row(RGB(255, 255, 255), HSL(0, 0, 100))
        ) { rgb, hsl ->
            rgb.toHSL() shouldBe hsl
        }
    }

    @Test
    @JsName("RGB_to_Hex")
    fun `RGB to Hex`() {
        forAll(
            row(RGB(0, 0, 0).toHex(false), "000000"),
            row(RGB(140, 200, 100).toHex(), "#8cc864"),
            row(RGB(140, 200, 100).toHex(true), "#8cc864"),
            row(RGB(255, 255, 255).toHex(false), "ffffff")
        ) { actual, expected ->
            actual shouldBe expected
        }
    }

    @Test
    @JsName("Hex_to_RGB")
    fun `Hex to RGB`() {
        forAll(
            row("000000", RGB(0, 0, 0)),
            row("#8CC864", RGB(140, 200, 100)),
            row("ffffff", RGB(255, 255, 255)),
            row("ffffff00", RGB(255, 255, 255, 0f)),
            row("#ffffff00", RGB(255, 255, 255, 0f)),
            row("#3a30", RGB(51, 170, 51, 0f)),
            row("#3A3F", RGB(51, 170, 51, 1f)),
            row("#33aa3300", RGB(51, 170, 51, 0f)),
            row("#33AA3380", RGB(51, 170, 51, 0x80 / 0xff.toFloat())),
        ) { hex, rgb ->
            RGB(hex) shouldBe rgb
        }
    }

    @Test
    @JsName("RGB_to_XYZ")
    fun `RGB to XYZ`() {
        forAll(
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
    @JsName("RGB_to_LAB")
    fun `RGB to LAB`() {
        forAll(
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
            val lab = rgb.toLAB()
            lab.l shouldBe (l plusOrMinus 0.01)
            lab.a shouldBe (a plusOrMinus 0.01)
            lab.b shouldBe (b plusOrMinus 0.01)
        }
    }

    @Test
    @JsName("RGB_to_LUV")
    fun `RGB to LUV`() {
        forAll(
            row(RGB(0, 0, 0), 0.0, 0.0, 0.0),
            row(RGB(255, 255, 255), 100.0, 0.0, 0.0),
            row(RGB(255, 0, 0), 53.2408, 175.0151, 37.7564),
            row(RGB(0, 255, 0), 87.7347, -83.0776, 107.3985),
            row(RGB(0, 0, 255), 32.2970, -9.4054, -130.3423),
            row(RGB(255, 255, 0), 97.1393, 7.7056, 106.7866),
            row(RGB(0, 255, 255), 91.1132, -70.4773, -15.2042),
            row(RGB(255, 0, 255), 60.3242, 84.0714, -108.6834),
            row(RGB(92, 191, 84), 69.5940, -46.2383, 63.2284)
        ) { rgb, l, u, v ->
            val luv = rgb.toLUV()
            withClue("l") { luv.l.toDouble() } shouldBe (l plusOrMinus 0.0005)
            withClue("u") { luv.u.toDouble() } shouldBe (u plusOrMinus 0.0005)
            withClue("v") { luv.v.toDouble() } shouldBe (v plusOrMinus 0.0005)
        }
    }

    @Test
    @JsName("RGB_to_LCH")
    fun `RGB to LCH`() {
        forAll(
            row(RGB(0, 0, 0), 0.0, 0.0, 0.0),
            row(RGB(255, 0, 0), 53.2408, 179.0414, 12.1740),
            row(RGB(0, 255, 0), 87.7347, 135.7804, 127.7236),
            row(RGB(0, 0, 255), 32.2970, 130.6812, 265.8727),
            row(RGB(255, 255, 0), 97.1393, 107.0643, 85.8727),
            row(RGB(0, 255, 255), 91.1132, 72.0987, 192.1740),
            row(RGB(255, 0, 255), 60.3242, 137.4048, 307.7236),
            row(RGB(92, 191, 84), 69.5940, 78.3314, 126.1776),
        ) { rgb, l, c, h ->
            val lch = rgb.toLCH()
            withClue("l") { lch.l.toDouble() shouldBe (l plusOrMinus 0.0005) }
            withClue("c") { lch.c.toDouble() shouldBe (c plusOrMinus 0.0005) }
            withClue("h") { lch.h.toDouble() shouldBe (h plusOrMinus 0.0005) }
        }
    }

    @Test
    @JsName("RGB_white_to_LCH")
    fun `RGB white to LCH`() {
        // With white, any hue can be used, so only test L and C
        val lch = RGB(255, 255, 255).toLCH()
        lch.l shouldBe (100f plusOrMinus 0.0005f)
        lch.c shouldBe (0f plusOrMinus 0.0005f)
    }

    @Test
    @JsName("RGB_to_CMYK")
    fun `RGB to CMYK`() {
        forAll(
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
    @JsName("RGB_TO_HWB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    fun `RGB to HWB`() {
        forAll(
            row(RGB("#996666"), HWB(0.0, 40.0, 40.0)),
            row(RGB("#998066"), HWB(30.0, 40.0, 40.0)),
            row(RGB("#999966"), HWB(60.0, 40.0, 40.0)),
            row(RGB("#809966"), HWB(90.0, 40.0, 40.0)),
            row(RGB("#669966"), HWB(120.0, 40.0, 40.0)),
            row(RGB("#66997f"), HWB(150.0, 40.0, 40.0)),
            row(RGB("#669999"), HWB(180.0, 40.0, 40.0)),
            row(RGB("#667f99"), HWB(210.0, 40.0, 40.0)),
            row(RGB("#666699"), HWB(240.0, 40.0, 40.0)),
            row(RGB("#7f6699"), HWB(270.0, 40.0, 40.0)),
            row(RGB("#996699"), HWB(300.0, 40.0, 40.0)),
            row(RGB("#996680"), HWB(330.0, 40.0, 40.0)),
            row(RGB("#80ff00"), HWB(90.0, 0.0, 0.0)),
            row(RGB("#b3cc99"), HWB(90.0, 60.0, 20.0)),
            row(RGB("#4c6633"), HWB(90.0, 20.0, 60.0)),
        ) { rgb, hwb ->
            val it = rgb.toHWB()
            // the tolerances here are really wide, due to the imprecision of the integer RGB.
            // The w3 spec doesn't have any rgb -> hwb test cases, so this is as precise as we can
            // get by flipping the hwb -> rgb examples.
            withClue("h") { it.h shouldBe (hwb.h plusOrMinus 0.6f) }
            withClue("w") { it.w shouldBe (hwb.w plusOrMinus 0.6f) }
            withClue("b") { it.b shouldBe (hwb.b plusOrMinus 0.6f) }
        }
    }

    @Test
    @JsName("RGB_TO_HWB_gray")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    fun `RGB to HWB gray`() {
        forAll(
            row(RGB("#000000"), 0f, 100f),
            row(RGB("#666666"), 40f, 60f),
            row(RGB("#999999"), 60f, 40f),
            row(RGB("#ffffff"), 100f, 0f),
        ) { rgb, ew, eb ->
            // hue is arbitrary for grays
            val (_, w, b) = rgb.toHWB()
            withClue("w") { w shouldBe (ew plusOrMinus 0.005f) }
            withClue("b") { b shouldBe (eb plusOrMinus 0.005f) }
        }
    }

    @Test
    @JsName("RGB_to_Ansi16")
    fun `RGB to Ansi16`() {
        forAll(
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
    @JsName("RGB_to_Ansi256")
    fun `RGB to Ansi256`() {
        forAll(
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
