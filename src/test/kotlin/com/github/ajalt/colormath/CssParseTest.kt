package com.github.ajalt.colormath

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import org.junit.Test

class CssParseTest {
    @Test
    fun `parseCssColor named`() {
        Color.fromCss("rebeccapurple") shouldBe RGB("#663399")
    }

    @Test
    fun `parseCssColor invalid`() = forall(
            row(""),
            row("foo"),
            row("#ff"),
            row("#ffg"),
            row("#12345"),
            row("#1122334"),
            row("#112233445"),
            row("rgb (1,2,3)"),
            row("rgb(1,2,#abc)"),
            row("rgb(1deg,2,3)"),
            row("rgb(1grad,2,3)"),
            row("rgb(1rad,2,3)"),
            row("rgb(1turn,2,3)"),
            row("rgb(1,2,3,4,5)"),
            row("rgb(1,2 3)"),
            row("rgb(-1,2,3)"),
            row("rgb(1%,2,3)"),
            row("rgb(1,2%,3)"),
            row("rgb(1,2,3%)"),
            row("rgb(256,2,3)"),
            row("rgb(1,256,3)"),
            row("rgb(1,2,256)"),
            row("rgb(1,-2,3)"),
            row("rgb(1,2,-3)"),
            row("rgb(1,2,3,-1)"),
            row("rgb(1 2,3)"),
            row("rgb(1 2 3, 4)"),
            row("rgb(1,2,3 / 4)"),
            row("rgb(1,2,3 4)"),
            row("hsl(1%,2%,3%)"),
            row("hsl(1,-2%,3%)"),
            row("hsl(1,2%,-3%)"),
            row("hsl(1,2%,3%,-4%)"),
            row("hsl(1,2%,3)"),
            row("hsl(1,2,3%)"),
            row("hsl(1degrees,2%,3%)"),
            row("hsl(1ddeg,2%,3%)"),
            row("hsl(1Deg,2%,3%)")
    ) {
        shouldThrow<IllegalArgumentException> {
            Color.fromCss(it)
        }
    }

    // Cases mostly from https://developer.mozilla.org/en-US/docs/Web/CSS/color_value
    @Test
    fun `parseCssColor valid`() = forall(
            row("#f09"),
            row("#F09"),
            row("#ff0099"),
            row("#FF0099"),
            row("rgb(255,0,153)"),
            row("rgb(255, 0, 153)"),
            row("rgb(255, 0, 153.0)"),
            row("rgb(100%,0%,60%)"),
            row("rgb(100%, 0%, 60%)"),
            row("rgb(255 0 153)"),
            row("#f09f"),
            row("#F09F"),
            row("#ff0099ff"),
            row("#FF0099FF"),
            row("rgb(255, 0, 153, 1)"),
            row("rgb(255, 0, 153, 100%)"),
            row("rgb(255 0 153 / 1)"),
            row("rgb(255 0 153 / 100%)"),
            row("rgb(255, 0, 153.4, 1)")
    ) {
        Color.fromCss(it) shouldBe RGB(255, 0, 153)
    }

    @Test
    fun `parseCssColor float exponents`() {
        Color.fromCss("rgb(1e2, .5e1, .5e0, +.25e2%)") shouldBe RGB(100, 5, 1, .25f)
    }

    @Test
    fun `parseCssColor alpha`() = forall(
            row("#3a30", 0f),
            row("#3A3F", 1f),
            row("#33aa3300", 0f),
            row("#33AA3380", 0x80 / 0xff.toFloat()),
            row("rgba(51, 170, 51, .1)", .1f),
            row("rgba(51, 170, 51, .4)", .4f),
            row("rgba(51, 170, 51, .7)", .7f),
            row("rgba(51, 170, 51,  1)", 1f),
            row("rgba(51 170 51 / 0.4)", .4f),
            row("rgba(51 170 51 / 40%)", .4f),
            row("rgba(51, 170, 50.6, 1)", 1f)
    ) { color, alpha ->
        Color.fromCss(color) shouldBe RGB(51, 170, 51, alpha)
    }

    @Test
    fun `parseCssColor hsl`() = forall(
            row("hsl(270,60%,70%)", 270, 60, 70, 1f),
            row("hsl(270, 60%, 70%)", 270, 60, 70, 1f),
            row("hsl(270 60% 70%)", 270, 60, 70, 1f),
            row("hsl(270deg, 60%, 70%)", 270, 60, 70, 1f),
            row("hsl(4.71239rad, 60%, 70%)", 270, 60, 70, 1f),
            row("hsl(.75turn, 60%, 70%)", 270, 60, 70, 1f),
            row("hsl(270, 60%, 50%, .15)", 270, 60, 50, .15f),
            row("hsl(270, 60%, 50%, 15%)", 270, 60, 50, .15f),
            row("hsl(270 60% 50% / .15)", 270, 60, 50, .15f),
            row("hsl(270 60% 50% / 15%)", 270, 60, 50, .15f),
            row("hsla(240, 100%, 50%, .05)", 240, 100, 50, .05f),
            row("hsla(240, 100%, 50%, .4)", 240, 100, 50, .4f),
            row("hsla(240, 100%, 50%, .7)", 240, 100, 50, .7f),
            row("hsla(240, 100%, 50%, 1)", 240, 100, 50, 1f),
            row("hsla(240 100% 50% / .05)", 240, 100, 50, .05f),
            row("hsla(240 100% 50% / 5%)", 240, 100, 50, .05f)
    ) { color, h, s, l, alpha ->
        Color.fromCss(color) shouldBe HSL(h, s, l, alpha)
    }

    @Test
    fun `parseCssColor hsl angles`() = forall(
            row("90", 90),
            row("1170", 90),
            row("90deg", 90),
            row("100grad", 90),
            row("0.25turn", 90),
            row("1.5708rad", 90),
            row("180", 180),
            row("180deg", 180),
            row("200grad", 180),
            row("0.5turn", 180),
            row("3.1416rad", 180),
            row("-90", 270),
            row("-90deg", 270),
            row("-100grad", 270),
            row("-0.25turn", 270),
            row("-1.5708rad", 270),
            row("0", 0),
            row("0deg", 0),
            row("0grad", 0),
            row("0turn", 0),
            row("0rad", 0)
    ) { angle, degrees ->
        Color.fromCss("hsl($angle, 0%, 0%)") shouldBe HSL(degrees, 0, 0)
    }
}
