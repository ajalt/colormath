package com.github.ajalt.colormath

import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.Row2
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.Float.Companion.NaN
import kotlin.js.JsName
import kotlin.test.Test


class CssParseTest {
    @Test
    @JsName("parseCssColor_named")
    fun `parseCssColor named`() {
        Color.parse("rebeccapurple") shouldBe RGB("#663399")
    }

    @Test
    @JsName("parseCssColor_custom_space")
    fun `parseCssColor custom space`() {
        Color.parse(
            "color(jzazbz 0.1 0.2 0.3)",
            customColorSpaces = mapOf("jzazbz" to JzAzBz)
        ).shouldEqualColor(JzAzBz(.1, .2, .3))
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
        row("rgb(1 2,3)"),
        row("rgb(1 2 3, 4)"),
        row("rgb(1,2,3 / 4)"),
        row("rgb(1,2,3 4)"),
        row("hsl(1%,2%,3%)"),
        row("hsl(1degrees,2%,3%)"),
        row("hsl(1ddeg,2%,3%)"),
        row("hsl(1Deg,2%,3%)"),
        row("color(profoto-rgb 0.4835 0.9167 0.2188)")
    ) {
        shouldThrow<IllegalArgumentException> { Color.parse(it) }
        Color.parseOrNull(it) shouldBe null
    }

    @Test
    @JsName("parseCssColor_clamp")
    fun `parseCssColor clamp`() = doTest(
        row("rgb(-1,2,3)", RGB.from255(0, 2, 3)),
        row("rgb(256,2,3)", RGB.from255(255, 2, 3)),
        row("rgb(1,256,3)", RGB.from255(1, 255, 3)),
        row("rgb(1,2,256)", RGB.from255(1, 2, 255)),
        row("rgb(1,-2,3)", RGB.from255(1, 0, 3)),
        row("rgb(1,2,-3)", RGB.from255(1, 2, 0)),
        row("rgb(1,2,3,-1)", RGB.from255(1, 2, 3, 0)),
        row("hsl(1,-2%,3%)", HSL(1, 0, .03)),
        row("hsl(1,2%,-3%)", HSL(1, .02, 0)),
        row("hsl(1,2%,3%,-4%)", HSL(1, .02, .03, 0f)),
    )

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
    ) {
        Color.parse(it).shouldEqualColor(RGB.from255(255, 0, 153))
        Color.parseOrNull(it).shouldEqualColor(RGB.from255(255, 0, 153))
    }

    @Test
    @JsName("parseCssColor_float_exponents")
    fun `parseCssColor float exponents`() {
        Color.parse("rgb(1e2, .5e1, .5e0, +.25e2%)")
            .shouldBe(RGB(100 / 255f, 5 / 255f, 1 / 255f, .25))
    }

    @Test
    @JsName("parseCssColor_alpha")
    fun `parseCssColor alpha`() = forAll(
        row("#3a3", 1f),
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
        Color.parse(color) shouldBe RGB(51 / 255f, 170 / 255f, 51 / 255f, alpha)
    }

    // https://github.com/web-platform-tests/wpt/blob/master/css/css-color/parsing/color-valid.html
    @Test
    @JsName("parseCssColor_rgb")
    fun `parseCssColor rgb`() = doTest(
        row("#234", RGB.from255(34, 51, 68)),
        row("#FEDCBA", RGB.from255(254, 220, 186)),
        row("rgb(100%, 0%, 0%)", RGB.from255(255, 0, 0)),
        row("rgba(2, 3, 4, 50%)", RGB.from255(2, 3, 4).copy(alpha = .5f)),
        row("rgb(-2, 3, 4)", RGB.from255(0, 3, 4)),
        row("rgb(100, 200, 300)", RGB.from255(100, 200, 255)),
        row("rgb(20, 10, 0, -10)", RGB.from255(20, 10, 0, 0)),
        row("rgb(100%, 200%, 300%)", RGB.from255(255, 255, 255)),
    )

    @Test
    @JsName("parseCssColor_hsl")
    fun `parseCssColor hsl`() = doTest(
        row("hsl(270,60%,70%)", HSL(270, .6, .7, 1f)),
        row("hsl(270, 60%, 70%)", HSL(270, .6, .7, 1f)),
        row("hsl(270 60% 70%)", HSL(270, .6, .7, 1f)),
        row("hsl(270deg, 60%, 70%)", HSL(270, .6, .7, 1f)),
        row("hsl(4.71239rad, 60%, 70%)", HSL(270, .6, .7, 1f)),
        row("hsl(.75turn, 60%, 70%)", HSL(270, .6, .7, 1f)),
        row("hsl(270, 60%, 50%, .15)", HSL(270, .6, .5, .15)),
        row("hsl(270, 60%, 50%, 15%)", HSL(270, .6, .5, .15)),
        row("hsl(270 60% 50% / .15)", HSL(270, .6, .5, .15)),
        row("hsl(270 60% 50% / 15%)", HSL(270, .6, .5, .15)),
        row("hsla(240, 100%, 50%, .05)", HSL(240, 1.0, .5, .05)),
        row("hsla(240, 100%, 50%, .4)", HSL(240, 1.0, .5, .4)),
        row("hsla(240, 100%, 50%, .7)", HSL(240, 1.0, .5, .7)),
        row("hsla(240, 100%, 50%, 1)", HSL(240, 1.0, .5, 1)),
        row("hsla(240 100% 50% / .05)", HSL(240, 1.0, .5, .05)),
        row("hsla(240 100% 50% / 5%)", HSL(240, 1.0, .5, .05))
    )

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
        row("0rad", 0),
        row("none", NaN),
    ) { angle, degrees ->
        Color.parse("hsl($angle, 0%, 0%)").shouldEqualColor(HSL(degrees, 0, 0))
    }

    @Test
    @JsName("parseCssColor_lab")
    // https://www.w3.org/TR/css-color-4/#funcdef-lab
    fun `parseCssColor lab`() = doTest(
        row("lab(29.2345% 39.3825 20.0664)", LAB50(29.2345, 39.3825, 20.0664)),
        row("lab(52.2345% 40.1645 59.9971)", LAB50(52.2345, 40.1645, 59.9971)),
        row("lab(60.2345% -5.3654 58.956)", LAB50(60.2345, -5.3654, 58.956)),
        row("lab(62.2345% -34.9638 47.7721)", LAB50(62.2345, -34.9638, 47.7721)),
        row("lab(67.5345% -8.6911 -41.6019)", LAB50(67.5345, -8.6911, -41.6019)),
    )

    @Test
    @JsName("parseCssColor_lch")
    // https://www.w3.org/TR/css-color-4/#funcdef-lch
    fun `parseCssColor lch`() = doTest(
        row("lch(29.2345% 44.2 27)", LCHab50(29.2345, 44.2, 27.0)),
        row("lch(52.2345% 72.2 56.2)", LCHab50(52.2345, 72.2, 56.2)),
        row("lch(60.2345% 59.2 95.2)", LCHab50(60.2345, 59.2, 95.2)),
        row("lch(62.2345% 59.2 126.2)", LCHab50(62.2345, 59.2, 126.2)),
        row("lch(67.5345% 42.5 258.2)", LCHab50(67.5345, 42.5, 258.2)),
    )

    @Test
    @JsName("parseCssColor_hwb")
    // No examples in the spec for this one
    fun `parseCssColor hwb`() = doTest(
        row("hwb(180 0% 0%)", HWB(180.0, 0.0, 0.0)),
        row("hwb(180deg 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(200grad 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(0.5turn 23.4% 45.6%)", HWB(180.0, .234, .456)),
        row("hwb(3.1416rad 23.4% 45.6%)", HWB(180.0, .234, .456)),
    )

    @Test
    @JsName("parseCssColor_oklab")
    // https://www.w3.org/TR/css-color-4/#ex-oklab-samples
    fun `parseCssColor oklab`() = doTest(
        row("oklab(40.101%  0.1147  0.0453)", Oklab(0.40101, 0.1147, 0.0453)),
        row("oklab(59.686%  0.1009  0.1192)", Oklab(0.59686, 0.1009, 0.1192)),
        row("oklab(0.65125 -0.0320  0.1274)", Oklab(0.65125, -0.0320, 0.1274)),
        row("oklab(66.016% -0.1084  0.1114)", Oklab(0.66016, -0.1084, 0.1114)),
        row("oklab(72.322% -0.0465 -0.1150)", Oklab(0.72322, -0.0465, -0.1150)),
        row("oklab(51.975% -35.075% 26.92%)", RGB("#008000").toOklab()),
    )

    @Test
    @JsName("parseCssColor_oklch")
    // https://www.w3.org/TR/css-color-4/#ex-oklch-samples
    fun `parseCssColor oklch`() = doTest(
        row("oklch(40.101% 0.12332 21.555)", Oklch(0.40101, 0.12332, 21.555)),
        row("oklch(59.686% 0.15619 49.7694)", Oklch(0.59686, 0.15619, 49.7694)),
        row("oklch(0.65125 0.13138 104.097)", Oklch(0.65125, 0.13138, 104.097)),
        row("oklch(0.66016 0.15546 134.231)", Oklch(0.66016, 0.15546, 134.231)),
        row("oklch(72.322% 0.12403 247.996)", Oklch(0.72322, 0.12403, 247.996)),
        row("oklch(0% 0 none)", Oklch(0, 0, NaN)),
        row("oklch(51.975% 44.215% 142.495)", RGB("#008000").toOklch()),
    )

    @Test
    @JsName("parseCssColor_color")
    // https://www.w3.org/TR/css-color-4/#funcdef-color
    fun `parseCssColor color`() = doTest(
        row("color(srgb 25% 50% 75% / 90%)", SRGB(.25, .5, .75, .9)),
        row("color(display-p3 0 1 0)", DisplayP3(0f, 1f, 0f)),
        row("color(a98-rgb 25% 50% 75%)", AdobeRGB(.25, .5, .75)),
        row("color(prophoto-rgb 25% 50% 75% / 90%)", ROMM_RGB(.25, .5, .75, .9)),
        row("color(rec2020 0.42053 0.979780 0.00579)", BT2020(0.42053, 0.979780, 0.00579)),
        row("color(xyz 0.2005 0.14089 0.4472)", XYZ50(0.2005, 0.14089, 0.4472)),
    )

    private fun doTest(vararg rows: Row2<String, Color>) = forAll(*rows) { str, color ->
        Color.parse(str).shouldEqualColor(color)
    }
}
