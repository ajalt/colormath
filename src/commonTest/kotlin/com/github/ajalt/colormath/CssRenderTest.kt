package com.github.ajalt.colormath

import com.github.ajalt.colormath.AngleUnit.*
import com.github.ajalt.colormath.RenderCondition.*
import com.github.ajalt.colormath.RenderCondition.AUTO
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@Suppress("BooleanLiteralArgument")
class CssRenderTest {
    private data class R(
            val r: Int,
            val g: Int,
            val b: Int,
            val a: Float = 1f,
            val commas: Boolean = true,
            val namedRgba: Boolean = false,
            val rgbPercent: Boolean = false,
            val alphaPercent: Boolean = false,
            val renderAlpha: RenderCondition = AUTO
    )

    private data class H(
            val h: Int,
            val s: Int,
            val l: Int,
            val a: Float = 1f,
            val commas: Boolean = true,
            val namedHsla: Boolean = false,
            val hueUnit: AngleUnit = AngleUnit.AUTO,
            val alphaPercent: Boolean = false,
            val renderAlpha: RenderCondition = AUTO
    )

    @Test
    fun toCssRgb() = forAll(
            row(R(0, 0, 0), "rgb(0, 0, 0)"),
            row(R(0, 0, 0, namedRgba = true), "rgba(0, 0, 0)"),
            row(R(0, 0, 0, commas = false), "rgb(0 0 0)"),
            row(R(0, 0, 0, renderAlpha = ALWAYS), "rgb(0, 0, 0, 1)"),
            row(R(0, 0, 0, .5f), "rgb(0, 0, 0, .5)"),
            row(R(0, 0, 0, .5f, commas = false), "rgb(0 0 0 / .5)"),
            row(R(0, 0, 0, .5f, renderAlpha = NEVER), "rgb(0, 0, 0)"),
            row(R(255, 128, 0, rgbPercent = true), "rgb(100%, 50%, 0%)"),
            row(R(255, 128, 0, .5f, rgbPercent = true), "rgb(100%, 50%, 0%, .5)"),
            row(R(255, 128, 0, .5f, alphaPercent = true), "rgb(255, 128, 0, 50%)"),
            row(R(255, 128, 0, .5f, rgbPercent = true, alphaPercent = true), "rgb(100%, 50%, 0%, 50%)")
    ) { (r, g, b, a, commas, namedRgba, rgbPercent, alphaPercent, renderAlpha), expected ->
        RGB(r, g, b, a).toCssRgb(commas, namedRgba, rgbPercent, alphaPercent, renderAlpha) shouldBe expected
    }

    @Test
    fun toCssHsl() = forAll(
            row(H(0, 0, 0), "hsl(0, 0%, 0%)"),
            row(H(0, 0, 0, namedHsla = true), "hsla(0, 0%, 0%)"),
            row(H(0, 0, 0, .5f), "hsl(0, 0%, 0%, .5)"),
            row(H(0, 0, 0, .5f, commas = false), "hsl(0 0% 0% / .5)"),
            row(H(0, 0, 0, commas = false), "hsl(0 0% 0%)"),
            row(H(0, 0, 0, .5f, alphaPercent = true), "hsl(0, 0%, 0%, 50%)"),
            row(H(0, 0, 0, renderAlpha = ALWAYS), "hsl(0, 0%, 0%, 1)"),
            row(H(0, 0, 0, .5f, renderAlpha = NEVER), "hsl(0, 0%, 0%)"),
            row(H(180, 50, 50, hueUnit = DEGREES), "hsl(180deg, 50%, 50%)"),
            row(H(180, 50, 50, hueUnit = GRADIANS), "hsl(200grad, 50%, 50%)"),
            row(H(180, 50, 50, hueUnit = RADIANS), "hsl(3.1415rad, 50%, 50%)"),
            row(H(180, 50, 50, hueUnit = TURNS), "hsl(.5turn, 50%, 50%)")
    ) { (h, s, l, a, commas, namedHsla, hueUnit, alphaPercent, renderAlpha), expected ->
        HSL(h, s, l, a).toCssHsl(commas, namedHsla, hueUnit, alphaPercent, renderAlpha) shouldBe expected
    }
}
