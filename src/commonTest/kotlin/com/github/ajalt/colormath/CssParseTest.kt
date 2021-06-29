package com.github.ajalt.colormath

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test


class CssParseTest {
    @Test
    @JsName("parseCssColor_named")
    fun `parseCssColor named`() {
        Color.parse("rebeccapurple") shouldBe RGB("#663399")
    }

    @Test
    @JsName("parseCssColor_invalid")
    fun `parseCssColor invalid`() = forAll(
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
        row("rgb(1%,2,3)"),
        row("rgb(1,2%,3)"),
        row("rgb(1,2,3%)"),
        row("rgb(1 2,3)"),
        row("rgb(1 2 3, 4)"),
        row("rgb(1,2,3 / 4)"),
        row("rgb(1,2,3 4)"),
        row("hsl(1%,2%,3%)"),
        row("hsl(1,2%,3)"),
        row("hsl(1,2,3%)"),
        row("hsl(1degrees,2%,3%)"),
        row("hsl(1ddeg,2%,3%)"),
        row("hsl(1Deg,2%,3%)")
    ) {
        shouldThrow<IllegalArgumentException> {
            Color.parse(it)
        }
    }

    @Test
    @JsName("parseCssColor_clamp")
    fun `parseCssColor clamp`() = forAll(
        row("rgb(-1,2,3)", RGB(0, 2, 3)),
        row("rgb(256,2,3)", RGB(255, 2, 3)),
        row("rgb(1,256,3)", RGB(1, 255, 3)),
        row("rgb(1,2,256)", RGB(1, 2, 255)),
        row("rgb(1,-2,3)", RGB(1, 0, 3)),
        row("rgb(1,2,-3)", RGB(1, 2, 0)),
        row("rgb(1,2,3,-1)", RGB(1, 2, 3, 0f)),
        row("hsl(1,-2%,3%)", HSL(1, 0, 3)),
        row("hsl(1,2%,-3%)", HSL(1, 2, 0)),
        row("hsl(1,2%,3%,-4%)", HSL(1, 2, 3, 0f)),
    ) { it, ex ->
        Color.parse(it) shouldBe ex
    }

    // Cases mostly from https://developer.mozilla.org/en-US/docs/Web/CSS/color_value
    @Test
    @JsName("parseCssColor_valid")
    fun `parseCssColor valid`() = forAll(
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
        Color.parse(it) shouldBe RGB(255, 0, 153)
    }

    @Test
    @JsName("parseCssColor_float_exponents")
    fun `parseCssColor float exponents`() {
        Color.parse("rgb(1e2, .5e1, .5e0, +.25e2%)") shouldBe RGB(100, 5, 1, .25f)
    }

    @Test
    @JsName("parseCssColor_alpha")
    fun `parseCssColor alpha`() = forAll(
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
        Color.parse(color) shouldBe RGB(51, 170, 51, alpha)
    }

    @Test
    @JsName("parseCssColor_hsl")
    fun `parseCssColor hsl`() = forAll(
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
        Color.parse(color).shouldEqualColor(HSL(h, s, l, alpha))
    }

    @Test
    @JsName("parseCssColor_hsl_angles")
    fun `parseCssColor hsl angles`() = forAll(
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
        Color.parse("hsl($angle, 0%, 0%)").shouldEqualColor(HSL(degrees, 0, 0))
    }

    @Test
    @JsName("parseCssColor_lab")
    // https://www.w3.org/TR/css-color-4/#funcdef-lab
    fun `parseCssColor lab`() = forAll(
        row("lab(29.2345% 39.3825 20.0664)", LAB(29.2345, 39.3825, 20.0664)),
        row("lab(52.2345% 40.1645 59.9971)", LAB(52.2345, 40.1645, 59.9971)),
        row("lab(60.2345% -5.3654 58.956)", LAB(60.2345, -5.3654, 58.956)),
        row("lab(62.2345% -34.9638 47.7721)", LAB(62.2345, -34.9638, 47.7721)),
        row("lab(67.5345% -8.6911 -41.6019)", LAB(67.5345, -8.6911, -41.6019)),
    ) { str, lab ->
        Color.parse(str).shouldEqualColor(lab)
    }

    @Test
    @JsName("parseCssColor_lch")
    // https://www.w3.org/TR/css-color-4/#funcdef-lch
    fun `parseCssColor lch`() = forAll(
        row("lch(29.2345% 44.2 27)", LCH(29.2345, 44.2, 27.0)),
        row("lch(52.2345% 72.2 56.2)", LCH(52.2345, 72.2, 56.2)),
        row("lch(60.2345% 59.2 95.2)", LCH(60.2345, 59.2, 95.2)),
        row("lch(62.2345% 59.2 126.2)", LCH(62.2345, 59.2, 126.2)),
        row("lch(67.5345% 42.5 258.2)", LCH(67.5345, 42.5, 258.2)),
    ) { str, lch ->
        Color.parse(str).shouldEqualColor(lch)
    }

    @Test
    @JsName("parseCssColor_hwb")
    // No examples in the spec for this one
    fun `parseCssColor hwb`() = forAll(
        row("hwb(180 0% 0%)", HWB(180.0, 0.0, 0.0)),
        row("hwb(180deg 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(200grad 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(0.5turn 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(3.1416rad 23.4% 45.6%)", HWB(180.0, .234, .456)),
    ) { str, hwb ->
        Color.parse(str).shouldEqualColor(hwb)
    }
}
