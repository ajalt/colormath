package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBInt
import com.github.ajalt.colormath.model.xyY
import com.github.ajalt.colormath.shouldEqualColor
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import kotlin.test.Test

class TransformTest {
    @Test
    fun divideAlpha() = forAll(
        row(RGB(.1, .1, .1, 1), RGB(.1, .1, .1, 1)),
        row(RGB(.1, .1, .1, .5), RGB(.2, .2, .2, .5)),
        row(RGB(.1, .1, .1, 0), RGB(.1, .1, .1, 0)),
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
            row(LCHab50.mix(purple, .5, plum, .5), mixed),
            row(LCHab50.mix(purple, .5, plum), mixed),
            row(LCHab50.mix(purple, plum, .5), mixed),
            row(LCHab50.mix(purple, plum), mixed),
            row(LCHab50.mix(plum, purple), mixed),
            row(LCHab50.mix(purple, .8, plum, .8), mixed),
            row(LCHab50.mix(purple, .3, plum, .3), LCHab50(51.51, 52.21, 325.8, 0.6)),
            row(LCHab50.mix(LCHab50(62.253, 54.011, 63.677), .4, LCHab50(91.374, 31.406, 98.834)),
                LCHab50(79.7256, 40.448, 84.771)),
            row(LCHab50.mix(LCHab50(50, 50, 60), LCHab50(50, 50, 0), HueAdjustments.longer),
                LCHab50(50, 50, 210)),
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
