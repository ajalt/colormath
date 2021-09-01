package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.LCHabColorSpaces.LCHab50
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import kotlin.js.JsName
import kotlin.test.Test

class TransformTest {
    @Test
    fun interpolate() = forAll(
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), 0f, RGB.from255(0, 0, 0)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), .5f, RGB.from255(127, 127, 127)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), 1f, RGB.from255(254, 254, 254)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254).toXYZ(), 1f, RGB.from255(254, 254, 254)),
    ) { c1, c2, a, ex ->
        c1.interpolate(c2, a).shouldEqualColor(ex)
        c1.space.interpolator(c1, c2).interpolate(a).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_with_hint")
    fun `interpolator with hint`() = forAll(
        row(0.00, RGB("#000")),
        row(0.25, RGB("#400")),
        row(0.50, RGB("#800")),
        row(0.55, RGB("#822")),
        row(0.60, RGB("#844")),
        row(0.80, RGB("#866")),
        row(1.00, RGB("#888")),
    ) { pos, ex ->
        RGB.interpolator {
            stop(RGB("#000"))
            stop(RGB("#800"))
            hint(.6)
            stop(RGB("#888"))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_with_NaN_hues")
    fun `interpolator with NaN hues`() = forAll(
        row(0.00, HSL(Double.NaN, 0.2, 0.2)),
        row(0.40, HSL(80.0, 0.4, 0.4)),
        row(0.50, HSL(90.0, 0.5, 0.5)),
        row(0.60, HSL(100.0, 0.6, 0.6)),
        row(0.80, HSL(100.0, 0.7, 0.7)),
        row(1.00, HSL(Double.NaN, 0.8, 0.8)),
    ) { pos, ex ->
        HSL.interpolator {
            stop(HSL(Double.NaN, 0.2, 0.2))
            stop(HSL(80.0, 0.4, 0.4), .4)
            stop(HSL(100.0, 0.6, 0.6), .6)
            stop(HSL(Double.NaN, 0.8, 0.8))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_explicit_positions")
    fun `interpolator with explicit positions`() = forAll(
        row(0.00, RGB("#111")),
        row(0.15, RGB("#222")),
        row(0.20, RGB("#333")),
        row(0.50, RGB("#333")),
        row(0.70, RGB("#333")),
        row(0.90, RGB("#555")),
        row(1.00, RGB("#555")),
    ) { pos, ex ->
        RGB.interpolator {
            stop(RGB("#111"), .1)
            stop(RGB("#333"), .2, .8)
            stop(RGB("#555"), .8)
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_sequence")
    fun `interpolator sequence`() {
        RGB.interpolator(RGB("#000"), RGB("#888")).sequence(9).toList().zip(listOf(
            RGB("#000"),
            RGB("#111"),
            RGB("#222"),
            RGB("#333"),
            RGB("#444"),
            RGB("#555"),
            RGB("#666"),
            RGB("#777"),
            RGB("#888"),
        )).forAll { (a, ex) ->
            a.shouldEqualColor(ex)
        }
    }

    @Test
    @JsName("monotone_spline_interpolator_equal_spacing")
    fun `monotone spline interpolator equal spacing`() = forAll(
        row(0.00, RGB(0, 0, 0)),
        row(0.05, RGB(0.17013, 0.17013, 0.17013)),
        row(0.10, RGB(0.36373, 0.36373, 0.36373)),
        row(0.15, RGB(0.5456, 0.5456, 0.5456)),
        row(0.20, RGB(0.68053, 0.68053, 0.68053)),
        row(0.40, RGB(0.43093, 0.43093, 0.43093)),
        row(0.45, RGB(0.3152, 0.3152, 0.3152)),
        row(0.50, RGB(0.26667, 0.26667, 0.26667)),
        row(0.55, RGB(0.34293, 0.34293, 0.34293)),
        row(0.60, RGB(0.5248, 0.5248, 0.5248)),
        row(0.80, RGB(0.9664, 0.9664, 0.9664)),
        row(1.00, RGB(0.53333, 0.53333, 0.53333)),
    ) { pos, ex ->
        RGB.interpolator {
            method = InterpolationMethods.monotoneSpline()
            stop(RGB("#000"))
            stop(RGB("#bbb"))
            stop(RGB("#444"))
            stop(RGB("#fff"))
            stop(RGB("#888"))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("monotonic_spline_interpolator_unequal_spacing")
    fun `monotone spline interpolator unequal spacing`() = forAll(
        row(0.00, RGB(0.26666662, 0.26666662, 0.26666662)),
        row(0.10, RGB(0.0, 0.0, 0.0)),
        row(0.15, RGB(0.16666669, 0.16666669, 0.16666669)),
        row(0.20, RGB(0.26666668, 0.26666668, 0.26666668)),
        row(0.30, RGB(0.2, 0.2, 0.2)),
        row(0.40, RGB(0.13333334, 0.13333334, 0.13333334)),
        row(0.50, RGB(0.17173335, 0.17173335, 0.17173335)),
        row(0.90, RGB(0.6666668, 0.6666668, 0.6666668)),
        row(1.00, RGB(0.7477335, 0.7477335, 0.7477335)),
    ) { pos, ex ->
        RGB.interpolator {
            method = InterpolationMethods.monotoneSpline()
            stop(RGB("#000"), .1)
            stop(RGB("#444"), .2)
            stop(RGB("#222"), .4)
            stop(RGB("#aaa"), .9)
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("monotone_spline_interpolator_parabolic_endpoints")
    fun `monotone spline interpolator parabolic endpoints`() = forAll(
        row(0.00, RGB(0, 0, 0)),
        row(0.05, RGB(0.24693, 0.24693, 0.24693)),
        row(0.10, RGB(0.45013, 0.45013, 0.45013)),
        row(0.15, RGB(0.6032, 0.6032, 0.6032)),
        row(0.20, RGB(0.69973, 0.69973, 0.69973)),
        row(0.40, RGB(0.43093, 0.43093, 0.43093)),
        row(0.45, RGB(0.3152, 0.3152, 0.3152)),
        row(0.50, RGB(0.26667, 0.26667, 0.26667)),
        row(0.55, RGB(0.34293, 0.34293, 0.34293)),
        row(0.60, RGB(0.5248, 0.5248, 0.5248)),
        row(0.80, RGB(0.98133, 0.98133, 0.98133)),
        row(1.00, RGB(0.53333, 0.53333, 0.53333)),
    ) { pos, ex ->
        RGB.interpolator {
            method = InterpolationMethods.monotoneSpline(parabolicEndpoints = true)
            stop(RGB("#000"))
            stop(RGB("#bbb"))
            stop(RGB("#444"))
            stop(RGB("#fff"))
            stop(RGB("#888"))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_alpha_fixup")
    fun `interpolator alpha fixup`() {
        RGB.interpolator {
            premultiplyAlpha = false
            stop(RGB("#0001"))
            stop(RGB("#222"))
        }.interpolate(.5).shouldEqualColor(RGB("#1118"))
    }

    @Test
    fun multiplyAlpha() = forAll(
        row(RGB.from255(100, 100, 100, 1f), RGB.from255(100, 100, 100, 1f)),
        row(RGB.from255(100, 100, 100, 0.5f), RGB.from255(50, 50, 50, 0.5f)),
        row(RGB.from255(100, 100, 100, 0f), RGB.from255(0, 0, 0, 0f)),
    ) { rgb, ex ->
        rgb.multiplyAlpha().shouldEqualColor(ex)
    }

    @Test
    @JsName("monotone_spline_interpolator_with_NaN_hues")
    fun `monotone spline interpolator with NaN hues`() = forAll(
        row(0.00, HSL(Double.NaN, 0.2, 0.2)),
        row(0.40, HSL(80.0, 0.4, 0.4)),
        row(0.45, HSL(140.0, 0.7125, 0.7125)),
        row(0.55, HSL(150.0, 0.8, 0.8)),
        row(0.80, HSL(100.0, 0.675, 0.675)),
        row(1.00, HSL(Double.NaN, 0.8, 0.8)),
    ) { pos, ex ->
        HSL.interpolator {
            method = InterpolationMethods.monotoneSpline()
            stop(HSL(Double.NaN, 0.2, 0.2))
            stop(HSL(80.0, 0.4, 0.4), .4)
            stop(HSL(200.0, 1.0, 1.0), .5)
            stop(HSL(100.0, 0.6, 0.6), .6)
            stop(HSL(Double.NaN, 0.8, 0.8))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    fun divideAlpha() = forAll(
        row(RGB.from255(100, 100, 100, 1f), RGB.from255(100, 100, 100, 1f)),
        row(RGB.from255(50, 50, 50, 0.5f), RGB.from255(100, 100, 100, 0.5f)),
        row(RGB.from255(100, 100, 100, 0f), RGB.from255(100, 100, 100, 0f)),
    ) { rgb, ex ->
        rgb.divideAlpha().shouldEqualColor(ex)
    }

    // most test cases from https://www.w3.org/TR/css-color-5/#color-mix
    @Test
    fun mix() {
        // Specifying colors manually since the W3 examples use the old bradford adaptation
        val purple = LCHab50(29.6920, 66.8302, 327.1094)
        val plum = LCHab50(73.3321, 37.6076, 324.5817)
        val mixed = LCHab50(51.51, 52.21, 325.8)
        forAll(
            row(LCHab50.mix(purple, .5f, plum, .5f), mixed),
            row(LCHab50.mix(purple, .5f, plum), mixed),
            row(LCHab50.mix(purple, plum, .5f), mixed),
            row(LCHab50.mix(purple, plum), mixed),
            row(LCHab50.mix(plum, purple), mixed),
            row(LCHab50.mix(purple, .8f, plum, .8f), mixed),
            row(LCHab50.mix(purple, .3f, plum, .3f), LCHab50(51.51, 52.21, 325.8, 0.6)),
            row(LCHab50.mix(LCHab50(62.253, 54.011, 63.677), .4f, LCHab50(91.374, 31.406, 98.834)),
                LCHab50(79.7256, 40.448, 84.771)),
            row(LCHab50.mix(LCHab50(50f, 50f, 60f), LCHab50(50f, 50f, 0f), HueAdjustments.longer),
                LCHab50(50f, 50f, 210f)),
        ) { actual, ex ->
            actual.shouldEqualColor(ex, 0.1)
        }
    }

    @Test
    fun chromaticAdapter() = forAll(
        row(RGB.createChromaticAdapter(RGB.from255(209, 215, 212)).adapt(RGB.from255(192, 202, 202)),
            RGB(r = 0.9202273, g = 0.94016844, b = 0.9533126)),
        row(RGB.createChromaticAdapter(RGB.from255(209, 215, 212).toChrom()).adapt(RGB.from255(192, 202, 202)),
            RGB(r = 0.9202273, g = 0.94016844, b = 0.9533126)),
        row(RGBInt.createChromaticAdapter(RGBInt(200, 210, 220)).adapt(RGBInt(11, 222, 33)),
            RGB(r = 0.29472744, g = 1.0578139, b = 0.073229484).toRGBInt()),
        row(RGBInt.createChromaticAdapter(RGBInt(200, 210, 220).toChrom()).adapt(RGBInt(11, 222, 33)),
            RGB(r = 0.29472744, g = 1.0578139, b = 0.073229484).toRGBInt()),
    ) { ac, ex ->
        ac.shouldEqualColor(ex)
    }

    @Test
    fun adaptAll() {
        val colors = intArrayOf(RGBInt(192, 202, 202).argb.toInt(), RGBInt(11, 222, 33).argb.toInt())
        RGBInt.createChromaticAdapter(RGBInt(200, 210, 220)).adaptAll(colors)
        RGBInt(colors[0].toUInt()).shouldEqualColor(RGB(0.96045226, 0.9623541, 0.9181748).toRGBInt())
        RGBInt(colors[1].toUInt()).shouldEqualColor(RGB(0.29472744, 1.0578139, 0.073229484).toRGBInt())
    }
}

private fun Color.toChrom(): xyY = toXYZ().toCIExyY()
