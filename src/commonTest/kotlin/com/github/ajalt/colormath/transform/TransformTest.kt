package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.HSL
import com.github.ajalt.colormath.LCH
import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.shouldEqualColor
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
        c1.model.interpolator(c1, c2).interpolate(a).shouldEqualColor(ex)
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
        // Specifying colors manually since the examples use D50
        val purple = LCH(29.6920, 66.8302, 327.1094)
        val plum = LCH(73.3321, 37.6076, 324.5817)
        val mixed = LCH(51.51, 52.21, 325.8)
        forAll(
            row(LCH.mix(purple, .5f, plum, .5f), mixed),
            row(LCH.mix(purple, .5f, plum), mixed),
            row(LCH.mix(purple, plum, .5f), mixed),
            row(LCH.mix(purple, plum), mixed),
            row(LCH.mix(plum, purple), mixed),
            row(LCH.mix(purple, .8f, plum, .8f), mixed),
            row(LCH.mix(purple, .3f, plum, .3f), LCH(51.51, 52.21, 325.8, 0.6)),
            row(LCH.mix(LCH(62.253, 54.011, 63.677), .4f, LCH(91.374, 31.406, 98.834)), LCH(79.7256, 40.448, 84.771)),
            row(LCH.mix(LCH(50f, 50f, 60f), LCH(50f, 50f, 0f), HueAdjustments.longer), LCH(50f, 50f, 210f))
        ) { actual, ex ->
            actual.shouldEqualColor(ex, 0.1)
        }
    }
}
