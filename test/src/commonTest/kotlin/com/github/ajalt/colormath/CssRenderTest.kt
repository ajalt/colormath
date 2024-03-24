package com.github.ajalt.colormath

import com.github.ajalt.colormath.AngleUnit.*
import com.github.ajalt.colormath.RenderCondition.*
import com.github.ajalt.colormath.RenderCondition.AUTO
import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.HSV
import com.github.ajalt.colormath.model.HWB
import com.github.ajalt.colormath.model.JzAzBz
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.Oklch
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ACES
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CssRenderTest {
    private data class R(
        val r: Int,
        val g: Int,
        val b: Int,
        val a: Float = 1f,
        val commas: Boolean = false,
        val namedRgba: Boolean = false,
        val rgbPercent: Boolean = false,
        val alphaPercent: Boolean = false,
        val renderAlpha: RenderCondition = AUTO,
    )

    private data class H(
        val h: Number,
        val s: Number,
        val l: Number,
        val a: Float = 1f,
        val commas: Boolean = false,
        val namedHsla: Boolean = false,
        val hueUnit: AngleUnit = AngleUnit.AUTO,
        val alphaPercent: Boolean = false,
        val renderAlpha: RenderCondition = AUTO,
    )

    private data class H2(
        val h: Number,
        val s: Number,
        val v: Number,
        val a: Float = 1f,
        val commas: Boolean = false,
        val namedHsla: Boolean = false,
        val hueUnit: AngleUnit = AngleUnit.AUTO,
        val alphaPercent: Boolean = false,
        val renderAlpha: RenderCondition = AUTO,
        val unitsPercent: Boolean = false
    )

    private data class O(
        val l: Number,
        val c: Number,
        val h: Number,
        val a: Float = 1f,
        val commas: Boolean = false,
        val namedHsla: Boolean = false,
        val hueUnit: AngleUnit = AngleUnit.AUTO,
        val alphaPercent: Boolean = false,
        val renderAlpha: RenderCondition = AUTO,
        val unitsPercent: Boolean = false
    )

    @Test
    fun formatCssRgb() = forAll(
        row(R(0, 0, 0), "rgb(0 0 0)"),
        row(R(0, 0, 0, namedRgba = true), "rgba(0 0 0)"),
        row(R(0, 0, 0, commas = true), "rgb(0, 0, 0)"),
        row(R(0, 0, 0, renderAlpha = ALWAYS), "rgb(0 0 0 / 1)"),
        row(R(0, 0, 0, .5f), "rgb(0 0 0 / 0.5)"),
        row(R(0, 0, 0, .5f, commas = true), "rgb(0, 0, 0, 0.5)"),
        row(R(0, 0, 0, .5f, renderAlpha = NEVER), "rgb(0 0 0)"),
        row(R(255, 128, 0, rgbPercent = true), "rgb(100% 50% 0%)"),
        row(R(255, 128, 0, .5f, rgbPercent = true), "rgb(100% 50% 0% / 0.5)"),
        row(R(255, 128, 0, .5f, alphaPercent = true), "rgb(255 128 0 / 50%)"),
        row(R(255, 128, 0, .5f, rgbPercent = true, alphaPercent = true), "rgb(100% 50% 0% / 50%)")
    ) { (r, g, b, a, commas, namedRgba, rgbPercent, alphaPercent, renderAlpha), expected ->
        RGB(r / 255f, g / 255f, b / 255f, a).formatCssString(
            AngleUnit.AUTO,
            renderAlpha,
            rgbPercent,
            alphaPercent,
            namedRgba,
            commas
        ) shouldBe expected
    }

    @Test
    fun formatCssHsl() = forAll(
        row(H(0, 0, 0), "hsl(0 0% 0%)"),
        row(H(0, 0, 0, namedHsla = true), "hsla(0 0% 0%)"),
        row(H(0, 0, 0, .5f), "hsl(0 0% 0% / 0.5)"),
        row(H(0, 0, 0, .5f, commas = true), "hsl(0, 0%, 0%, 0.5)"),
        row(H(0, 0, 0, commas = true), "hsl(0, 0%, 0%)"),
        row(H(0, 0, 0, .5f, alphaPercent = true), "hsl(0 0% 0% / 50%)"),
        row(H(0, 0, 0, renderAlpha = ALWAYS), "hsl(0 0% 0% / 1)"),
        row(H(0, 0, 0, .5f, renderAlpha = NEVER), "hsl(0 0% 0%)"),
        row(H(180, .5, .5), "hsl(180 50% 50%)"),
        row(H(180, .5, .5, hueUnit = DEGREES), "hsl(180deg 50% 50%)"),
        row(H(180, .5, .5, hueUnit = GRADIANS), "hsl(200grad 50% 50%)"),
        row(H(180, .5, .5, hueUnit = RADIANS), "hsl(3.1415rad 50% 50%)"),
        row(H(180, .5, .5, hueUnit = TURNS), "hsl(0.5turn 50% 50%)"),
    ) { (h, s, l, a, commas, namedHsla, hueUnit, alphaPercent, renderAlpha), expected ->
        HSL(h, s, l, a).formatCssString(
            hueUnit,
            renderAlpha,
            true,
            alphaPercent,
            namedHsla,
            commas
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
        row(XYZ50(.1, .2, .3), "color(xyz 0.1 0.2 0.3)"),
        row(JzAzBz(.1, .2, .3), "color(--jzazbz 0.1 0.2 0.3)"),
    ) { color, expected ->
        color.formatCssString() shouldBe expected
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
        row(H2(0, 1, 1, unitsPercent = false), "color(--hsv 0 1 1)"),
        row(H2(0, 1, 1, unitsPercent = true), "color(--hsv 0% 100% 100%)"),
        row(H2(Float.NaN, 0, 1, unitsPercent = false), "color(--hsv NaN 0 1)"),
        row(H2(Float.NaN, 0, 1, unitsPercent = true), "color(--hsv NaN 0% 100%)"),
    ) { (h, s, v, a, commas, namedHsla, hueUnit, alphaPercent, renderAlpha, unitsPercent), expected ->
        HSV(h, s, v, a).formatCssString(
            hueUnit,
            renderAlpha,
            unitsPercent,
            alphaPercent,
            namedHsla,
            commas
        ) shouldBe expected
    }

    @Test
    fun formatCssOklch() = forAll(
        row(O(0, 0, Float.NaN, unitsPercent = false), "color(--oklch 0 0 NaN)"),
        row(O(0, 0, Float.NaN, unitsPercent = true), "color(--oklch 0% 0% NaN)"),
    ) { (l, c, h, a, commas, namedHsla, hueUnit, alphaPercent, renderAlpha, unitsPercent), expected ->
        Oklch(l, c, h, a).formatCssString(
            hueUnit,
            renderAlpha,
            unitsPercent,
            alphaPercent,
            namedHsla,
            commas
        ) shouldBe expected
    }
}
