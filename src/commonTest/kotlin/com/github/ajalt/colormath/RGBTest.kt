package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class RGBTest {
    @Test
    fun roundtrip() = roundtripTest(RGB(0.01, 0.02, 0.03, 0.04), intermediate = XYZ)

    @Test
    fun clamp() {
        RGB(.1, .2, .3).clamp() shouldBe RGB(.1, .2, .3)
        ROMM_RGB(.5, -1.0, 1.1, 3.0).clamp() shouldBe ROMM_RGB(.5, 0.0, 1.0, 1.0)
    }

    @Test
    fun grey() {
        RGB.grey(.1f, .2f) shouldBe RGB(.1, .1, .1, .2)
        ROMM_RGB.grey(.5) shouldBe ROMM_RGB(.5, .5, .5)
    }

    @Test
    @JsName("RGB_to_HSV")
    fun `RGB to HSV`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to HSV(Double.NaN, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to HSV(Double.NaN, 0.0, 0.18),
        RGB(0.40, 0.50, 0.60) to HSV(210.0, 0.33333333, 0.6),
        RGB(1.00, 1.00, 1.00) to HSV(Double.NaN, 0.0, 1.0),
    )

    @Test
    @JsName("RGB_to_HSL")
    fun `RGB to HSL`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to HSL(Double.NaN, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to HSL(Double.NaN, 0.0, 0.18),
        RGB(0.40, 0.50, 0.60) to HSL(210.0, 0.2, 0.5),
        RGB(1.00, 1.00, 1.00) to HSL(Double.NaN, 0.0, 1.0),
    )

    @Test
    @JsName("RGB_to_Hex")
    fun `RGB to Hex`() = forAll(
        row(RGB.from255(0, 0, 0).toHex(false), "000000"),
        row(RGB.from255(140, 200, 100).toHex(), "#8cc864"),
        row(RGB.from255(140, 200, 100).toHex(true), "#8cc864"),
        row(RGB.from255(255, 255, 255).toHex(false), "ffffff")
    ) { actual, expected ->
        actual shouldBe expected
    }

    @Test
    @JsName("Hex_to_RGB")
    fun `Hex to RGB`() = forAll(
        row("000000", RGB.from255(0, 0, 0)),
        row("#8CC864", RGB.from255(140, 200, 100)),
        row("ffffff", RGB.from255(255, 255, 255)),
        row("ffffff00", RGB.from255(255, 255, 255, 0f)),
        row("#ffffff00", RGB.from255(255, 255, 255, 0f)),
        row("#3a30", RGB.from255(51, 170, 51, 0f)),
        row("#3A3F", RGB.from255(51, 170, 51, 1f)),
        row("#33aa3300", RGB.from255(51, 170, 51, 0f)),
        row("#33AA3380", RGB.from255(51, 170, 51, 0x80 / 0xff.toFloat())),
    ) { hex, rgb ->
        RGB(hex) shouldBe rgb
    }

    @Test
    @JsName("RGB_to_XYZ")
    fun `RGB to XYZ`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to XYZ(0.0258636, 0.02721178, 0.0296352),
        RGB(0.40, 0.50, 0.60) to XYZ(0.18882301, 0.20432514, 0.33086999),
        RGB(1.00, 1.00, 1.00) to XYZ(0.95045593, 1.0, 1.08905775),
    )

    @Test
    @JsName("RGB_to_LAB")
    fun `RGB to LAB`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to LAB(0.0, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to LAB(18.89075051, 0.0, 0.0),
        RGB(0.40, 0.50, 0.60) to LAB(52.32273694, -2.74447861, -16.6536267),
        RGB(1.00, 1.00, 1.00) to LAB(100.0, 0.0, 0.0),
    )

    @Test
    @JsName("RGB_to_LUV")
    fun `RGB to LUV`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to LUV(0.0, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to LUV(18.89075051, 0.0, 0.0),
        RGB(0.40, 0.50, 0.60) to LUV(52.32273694, -13.5765706, -23.98061646),
        RGB(1.00, 1.00, 1.00) to LUV(100.0, 0.0, 0.0),
    )

    @Test
    @JsName("RGB_to_CMYK")
    fun `RGB to CMYK`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to CMYK(0.0, 0.0, 0.0, 1.0),
        RGB(0.18, 0.18, 0.18) to CMYK(0.0, 0.0, 0.0, 0.82),
        RGB(0.40, 0.50, 0.60) to CMYK(0.33333333, 0.16666667, 0.0, 0.4),
        RGB(1.00, 1.00, 1.00) to CMYK(0.0, 0.0, 0.0, 0.0),
    )

    @Test
    @JsName("RGB_to_HWB")
    // https://www.w3.org/TR/css-color-4/#hwb-examples
    fun `RGB to HWB`() = testColorConversions(
        RGB("#996666") to HWB(0.0, .4, .4),
        RGB("#998066") to HWB(30.0, .4, .4),
        RGB("#999966") to HWB(60.0, .4, .4),
        RGB("#809966") to HWB(90.0, .4, .4),
        RGB("#669966") to HWB(120.0, .4, .4),
        RGB("#66997f") to HWB(150.0, .4, .4),
        RGB("#669999") to HWB(180.0, .4, .4),
        RGB("#667f99") to HWB(210.0, .4, .4),
        RGB("#666699") to HWB(240.0, .4, .4),
        RGB("#7f6699") to HWB(270.0, .4, .4),
        RGB("#996699") to HWB(300.0, .4, .4),
        RGB("#996680") to HWB(330.0, .4, .4),
        RGB("#80ff00") to HWB(90.0, .0, .0),
        RGB("#b3cc99") to HWB(90.0, .6, .2),
        RGB("#4c6633") to HWB(90.0, .2, .6),
        // the tolerances here are really wide, due to the imprecision of the integer RGB. The w3
        // spec doesn't have any rgb -> hwb test cases, so this is as precise as we can get by
        // flipping the hwb -> rgb examples.
        tolerance = 0.6
    )

    @Test
    @JsName("RGB_to_HWB_gray")
    fun `RGB to HWB gray`() = testColorConversions(
        RGB("#000000") to HWB(0f, 0f, 1f),
        RGB("#666666") to HWB(0f, .4f, .6f),
        RGB("#999999") to HWB(0f, .6f, .4f),
        RGB("#ffffff") to HWB(0f, 1f, 0f),
        ignorePolar = true
    )

    @Test
    @JsName("RGB_to_Oklab")
    fun `RGB to Oklab`() = testColorConversions(
        RGB(0.00, 0.00, 0.00) to Oklab(0.0, 0.0, 0.0),
        RGB(0.18, 0.18, 0.18) to Oklab(0.30078197, -0.00000654, -0.00003704),
        RGB(0.40, 0.50, 0.60) to Oklab(0.58774836, -0.01788409, -0.04586991),
        RGB(1.00, 1.00, 1.00) to Oklab(0.9999988, -0.00002176, -0.00012316),
        tolerance = 5e-4
    )

    @Test
    @JsName("RGB_to_Ansi16")
    fun `RGB to Ansi16`() = testColorConversions(
        RGBInt(0, 0, 0) to Ansi16(30),
        RGBInt(128, 0, 0) to Ansi16(31),
        RGBInt(0, 128, 0) to Ansi16(32),
        RGBInt(128, 128, 0) to Ansi16(33),
        RGBInt(0, 0, 128) to Ansi16(34),
        RGBInt(128, 0, 128) to Ansi16(35),
        RGBInt(0, 128, 128) to Ansi16(36),
        RGBInt(192, 192, 192) to Ansi16(37),
        RGBInt(255, 0, 0) to Ansi16(91),
        RGBInt(0, 255, 0) to Ansi16(92),
        RGBInt(255, 255, 0) to Ansi16(93),
        RGBInt(0, 0, 255) to Ansi16(94),
        RGBInt(255, 0, 255) to Ansi16(95),
        RGBInt(0, 255, 255) to Ansi16(96),
        RGBInt(255, 255, 255) to Ansi16(97),
    )

    @Test
    @JsName("RGB_to_Ansi256")
    fun `RGB to Ansi256`() = testColorConversions(
        RGBInt(0, 0, 0) to Ansi256(16),
        RGBInt(51, 102, 0) to Ansi256(64),
        RGBInt(102, 204, 102) to Ansi256(114),
        RGBInt(255, 255, 255) to Ansi256(231),
        RGBInt(98, 98, 98) to Ansi256(241),
    )
}
