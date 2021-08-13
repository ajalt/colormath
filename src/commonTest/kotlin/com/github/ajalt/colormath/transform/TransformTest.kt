package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.*
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import kotlin.jvm.JvmName
import kotlin.test.Test

class TransformTest {
    @Test
    fun interpolate() = forAll(
        row(RGB(0, 0, 0), RGB(254, 254, 254), 0f, RGB(0, 0, 0)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), .5f, RGB(127, 127, 127)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), 1f, RGB(254, 254, 254)),
        row(RGB(0, 0, 0), RGB(254, 254, 254).toXYZ(), 1f, RGB(254, 254, 254)),
    ) { c1, c2, a, ex ->
        c1.interpolate(c2, a).shouldEqualColor(ex)
        c1.space.interpolator(c1, c2).interpolate(a).shouldEqualColor(ex)
    }

    @Test
    @JvmName("interpolator_with_hue_adjustment")
    fun `interpolator with hue_adjustment`() = forAll(
        row(0.00, 60),
        row(0.25, 230),
        row(0.50, 40),
        row(0.75, 200),
        row(1.00, 0),
    ) { amount, h ->
        val lerp = HSL.interpolator {
            hueAdjustment = HueAdjustments.increasing
            stop(HSL(60, 50, 50))
            stop(HSL(40, 50, 50))
            stop(HSL(0, 50, 50))
        }
        lerp.interpolate(amount).h shouldBe h

    }

    @Test
    @JvmName("interpolator_with_hint")
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
    @JvmName("interpolator_explicit_positions")
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
    @JvmName("interpolator_sequence")
    fun `interpolator sequence`() {
        RGB.interpolator(RGB("#000"), RGB("#888")).sequence(9)
            .toList().zip(listOf(
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
    fun multiplyAlpha() = forAll(
        row(RGB(100, 100, 100, 1f), RGB(100, 100, 100, 1f)),
        row(RGB(100, 100, 100, 0.5f), RGB(50, 50, 50, 0.5f)),
        row(RGB(100, 100, 100, 0f), RGB(0, 0, 0, 0f)),
    ) { rgb, ex ->
        rgb.multiplyAlpha().shouldEqualColor(ex)
    }

    @Test
    fun divideAlpha() = forAll(
        row(RGB(100, 100, 100, 1f), RGB(100, 100, 100, 1f)),
        row(RGB(50, 50, 50, 0.5f), RGB(100, 100, 100, 0.5f)),
        row(RGB(100, 100, 100, 0f), RGB(100, 100, 100, 0f)),
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
            row(LCHab50.mix(LCHab50(50f, 50f, 60f), LCHab50(50f, 50f, 0f), HueAdjustments.longer), LCHab50(50f, 50f, 210f))
        ) { actual, ex ->
            actual.shouldEqualColor(ex, 0.1)
        }
    }

    @Test
    fun chromaticAdapter() = forAll(
        row(RGB.createChromaticAdapter(RGB(209, 215, 212)).adapt(RGB(192, 202, 202)),
            RGB(r = 0.9202273, g = 0.94016844, b = 0.9533126)),
        row(RGB.createChromaticAdapter(RGB(209, 215, 212).toChrom()).adapt(RGB(192, 202, 202)),
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
