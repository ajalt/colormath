package com.github.ajalt.colormath

import com.github.ajalt.colormath.AngleUnit.*
import com.github.ajalt.colormath.RenderCondition.*
import com.github.ajalt.colormath.RenderCondition.AUTO
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.RGBColorSpaces.ACES
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ65
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

private val XYZ55 = XYZColorSpace(Illuminant.D55)

class CssRenderTest {
    private data class P(
        val hueUnit: AngleUnit = AngleUnit.AUTO,
        val renderAlpha: RenderCondition = AUTO,
        val unitsPercent: Boolean = false,
        val alphaPercent: Boolean = false,
        val legacyName: Boolean = false,
        val legacyFormat: Boolean = false,
    )

    @Test
    fun formatCssRgb() = forAll(
        row(RGB(0, 0, 0), P(), "rgb(0 0 0)"),
        row(RGB(0, 0, 0), P(legacyName = true), "rgba(0 0 0)"),
        row(RGB(0, 0, 0), P(legacyFormat = true), "rgb(0, 0, 0)"),
        row(RGB(0, 0, 0), P(renderAlpha = ALWAYS), "rgb(0 0 0 / 1)"),
        row(RGB(0, 0, 0, .5f), P(), "rgb(0 0 0 / 0.5)"),
        row(RGB(0, 0, 0, .5f), P(legacyFormat = true), "rgb(0, 0, 0, 0.5)"),
        row(RGB(0, 0, 0, .5f), P(renderAlpha = NEVER), "rgb(0 0 0)"),
        row(RGB(1, .5, 0), P(unitsPercent = true), "rgb(100% 50% 0%)"),
        row(RGB(1, .5, 0, .5f), P(unitsPercent = true), "rgb(100% 50% 0% / 0.5)"),
        row(RGB(1, .5, 0, .5f), P(alphaPercent = true), "rgb(255 128 0 / 50%)"),
        row(
            RGB(1, .5, 0, .5f),
            P(unitsPercent = true, alphaPercent = true),
            "rgb(100% 50% 0% / 50%)"
        ),
        testfn = ::doParamTest,
    )

    @Test
    fun formatCssHsl() = forAll(
        row(HSL(0, 0, 0), P(), "hsl(0 0% 0%)"),
        row(HSL(0, 0, 0), P(legacyName = true), "hsla(0 0% 0%)"),
        row(HSL(0, 0, 0, .5f), P(), "hsl(0 0% 0% / 0.5)"),
        row(HSL(0, 0, 0, .5f), P(legacyFormat = true), "hsl(0, 0%, 0%, 0.5)"),
        row(HSL(0, 0, 0), P(legacyFormat = true), "hsl(0, 0%, 0%)"),
        row(HSL(0, 0, 0, .5f), P(alphaPercent = true), "hsl(0 0% 0% / 50%)"),
        row(HSL(0, 0, 0), P(renderAlpha = ALWAYS), "hsl(0 0% 0% / 1)"),
        row(HSL(0, 0, 0, .5f), P(renderAlpha = NEVER), "hsl(0 0% 0%)"),
        row(HSL(180, .5, .5), P(), "hsl(180 50% 50%)"),
        row(HSL(180, .5, .5), P(hueUnit = DEGREES), "hsl(180deg 50% 50%)"),
        row(HSL(180, .5, .5), P(hueUnit = GRADIANS), "hsl(200grad 50% 50%)"),
        row(HSL(180, .5, .5), P(hueUnit = RADIANS), "hsl(3.1416rad 50% 50%)"),
        row(HSL(180, .5, .5), P(hueUnit = TURNS), "hsl(0.5turn 50% 50%)"),
        row(HSL(Float.NaN, 0, 0), P(hueUnit = TURNS), "hsl(none 0% 0%)"),
    ) { color, p, expected ->
        color.formatCssString(
            p.hueUnit, p.renderAlpha, true, p.alphaPercent, p.legacyName, p.legacyFormat
        ) shouldBe expected
    }

    @Test
    fun formatCssString() = forAll(
        row(RGB.from255(1, 2, 3), "rgb(1 2 3)"),
        row(DisplayP3(.1, .2, .3), "color(display-p3 0.1 0.2 0.3)"),
        row(AdobeRGB(.1, .2, .3), "color(a98-rgb 0.1 0.2 0.3)"),
        row(ROMM_RGB(.1, .2, .3), "color(prophoto-rgb 0.1 0.2 0.3)"),
        row(BT2020(.1, .2, .3), "color(rec2020 0.1 0.2 0.3)"),
        row(ACEScc(.1, .2, .3), "color(--acescc 0.1 0.2 0.3)"),
        row(HSL(1.0, .2, .3), "hsl(1 20% 30%)"),
        row(LAB50(1.0, 20.0, 30.0), "lab(1% 20 30)"),
        row(LCHab50(1.0, 20.0, 30.0), "lch(1% 20 30)"),
        row(HWB(1.0, .2, .3), "hwb(1 20% 30%)"),
        row(Oklab(1.0, .2, .3), "oklab(100% 0.2 0.3)"),
        row(Oklch(1.0, .2, .3), "oklch(100% 0.2 0.3)"),
        row(XYZ50(.1, .2, .3), "color(xyz 0.1 0.2 0.3)"),
        row(XYZ65(.1, .2, .3), "color(xyz-d65 0.1 0.2 0.3)"),
        row(LinearSRGB(.1, .2, .3), "color(srgb-linear 0.1 0.2 0.3)"),
        row(JzAzBz(.1, .2, .3), "color(--jzazbz 0.1 0.2 0.3)"),
    ) { color, expected ->
        color.formatCssString() shouldBe expected
        if ("--" !in expected) Color.parse(color.formatCssString()).shouldEqualColor(color)
    }

    @Test
    @JsName("formatCssString_custom_space")
    fun `formatCssString custom space`() = forAll(
        row(ACEScc(.1, .2, .3), "color(acescc 0.1 0.2 0.3)"),
        row(JzAzBz(1, -1, -0.5), "color(jzazbz 1 -1 -0.5)"),
        row(JzAzBz(0.0162790, -2.91842E-4, -0.00161363), "color(jzazbz 0.0163 -0.0003 -0.0016)"),
        row(XYZ55(.1, .2, .3), "color(xyz-d55 0.1 0.2 0.3)"),
        row(LUV(.1, .2, .3).toSRGB().toLUV(), "color(luv 0.1 0.2 0.3)"),
    ) { color, expected ->
        color.formatCssString(
            customColorSpaces = mapOf(
                "acescc" to ACEScc, "jzazbz" to JzAzBz, "luv" to LUV, "xyz-d55" to XYZ55,
            )
        ) shouldBe expected
    }

    @Test
    fun formatCssStringOrNull() = forAll(
        row(RGB.from255(1, 2, 3), "rgb(1 2 3)"),
        row(ACES(.1, .2, .3), null),
        row(JzAzBz(.1, .2, .3), null),
    ) { color, expected ->
        color.formatCssStringOrNull() shouldBe expected
    }

    @Test
    fun formatCssHsv() = forAll(
        row(HSV(0, 1, 1), P(unitsPercent = false), "color(--hsv 0 1 1)"),
        row(HSV(0, 1, 1), P(unitsPercent = true), "color(--hsv 0% 100% 100%)"),
        row(HSV(Float.NaN, 0, 1), P(unitsPercent = false), "color(--hsv none 0 1)"),
        row(HSV(Float.NaN, 0, 1), P(unitsPercent = true), "color(--hsv none 0% 100%)"),
        testfn = ::doParamTest,
    )

    @Test
    fun formatCssOklch() = forAll(
        row(Oklch(.1, .2, 180), P(), "oklch(10% 0.2 180)"),
        row(Oklch(0, 0, Float.NaN), P(), "oklch(0% 0 none)"),
        testfn = ::doParamTest,
    )

    private fun doParamTest(color: Color, p: P, expected: String) {
        color.formatCssString(
            p.hueUnit,
            p.renderAlpha,
            p.unitsPercent,
            p.alphaPercent,
            p.legacyName,
            p.legacyFormat
        ) shouldBe expected
    }
}

