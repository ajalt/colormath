package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.HSL
import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.shouldEqualColor
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import kotlin.js.JsName
import kotlin.test.Test

class InterpolateTest {
    @Test
    fun interpolate() = forAll(
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), 0, RGB.from255(0, 0, 0)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), .5, RGB.from255(127, 127, 127)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254), 1, RGB.from255(254, 254, 254)),
        row(RGB(0, 0, 0), RGB.from255(254, 254, 254).toXYZ(), 1, RGB.from255(254, 254, 254)),
    ) { c1, c2, a, ex ->
        c1.interpolate(c2, a).shouldEqualColor(ex)
        c1.space.interpolator(c1, c2).interpolate(a).shouldEqualColor(ex)
    }

    @Test
    @JsName("interpolator_with_midpoint")
    fun `interpolator with midpoint`() = forAll(
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
            stop(RGB("#800")) {
                easing = EasingFunctions.midpoint(0.2)
            }
            stop(RGB("#888"))
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    @JsName("NaN_hues")
    fun `NaN hues`() = forAll(
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
    @JsName("explicit_positions")
    fun `explicit positions`() = forAll(
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
            stop(RGB("#333"), .2)
            stop(RGB("#333"), .8)
            stop(RGB("#555"), .8)
        }.interpolate(pos).shouldEqualColor(ex)
    }

    @Test
    fun sequence() {
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
    @JsName("monotone_spline_equal_spacing")
    fun `monotone spline equal spacing`() = forAll(
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
    @JsName("monotone_spline_unequal_spacing")
    fun `monotone spline unequal spacing`() = forAll(
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
    @JsName("monotone_spline_parabolic_endpoints")
    fun `monotone spline parabolic endpoints`() = forAll(
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
    @JsName("alpha_fixup")
    fun `alpha fixup`() {
        RGB.interpolator {
            premultiplyAlpha = false
            stop(RGB("#0001"))
            stop(RGB("#222"))
        }.interpolate(.5).shouldEqualColor(RGB("#1118"))
    }

    @Test
    @JsName("monotone_spline_with_NaN_hues")
    fun `monotone spline with NaN hues`() = forAll(
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
}
