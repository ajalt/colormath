@file:Suppress("TestFunctionName")

package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.RGBColorSpaces.ACEScct
import com.github.ajalt.colormath.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.RGBColorSpaces.BT709
import com.github.ajalt.colormath.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

// Test values from https://github.com/colour-science/
class RGBColorSpacesTransferFunctionsTest {
    @Test
    fun SRGB() = doTest(SRGB, 0.01292, 0.46135612950044164)

    @Test
    fun ADOBE_RGB() = doTest(AdobeRGB, 0.043239356144868332, 0.45852946567989455)

    @Test
    fun BT_2020() = doTest(BT2020, 0.0045, 0.40884640249350368)

    @Test
    fun BT_709() = doTest(BT709, 0.0045, 0.409007728864150)

    @Test
    fun BT_709_extra() = forAll(
        row(0.015, 0.0675, "oetf"),
        row(0.0675, 0.015, "eotf"),
    ) { input, ex, func ->
        doSingleTest(BT709, input, ex, func)
    }

    @Test
    fun DCI_P3() = doTest(DCI_P3, 0.070170382867038292, 0.5170902489415321)

    @Test
    fun DISPLAY_P3() = doTest(DisplayP3, 0.01292, 0.46135612950044164)

    @Test
    fun ROMM_RGB() = doTest(ROMM_RGB, 0.016, 0.385711424751138)

    @Test
    fun ACEScc() = doTest(ACEScc, -0.01402878337112365, 0.413588402492442, -0.358447488584475, 0.554794520547945)

    @Test
    fun ACEScct() = doTest(ACEScct, 0.08344577193748999, 0.413588402492442, 0.072905534195835495, 0.554794520547945)

    private fun doTest(space: RGBColorSpace, zzOne: Double, eighteen: Double, zero: Double = 0.0, one: Double = 1.0) {
        forAll(
            row(0.0, zero, "oetf"),
            row(0.001, zzOne, "oetf"),
            row(0.18, eighteen, "oetf"),
            row(1.0, one, "oetf"),

            row(zero, 0.0, "eotf"),
            row(zzOne, 0.001, "eotf"),
            row(eighteen, 0.18, "eotf"),
            row(one, 1.0, "eotf"),
        ) { input, ex, func ->
            doSingleTest(space, input, ex, func)
        }
    }

    private fun doSingleTest(
        space: RGBColorSpace,
        input: Double,
        ex: Double,
        func: String,
    ) {
        val actual = when (func) {
            "oetf" -> space.transferFunctions.oetf(input.toFloat())
            else -> space.transferFunctions.eotf(input.toFloat())
        }
        actual.toDouble() shouldBe (ex plusOrMinus 1e-6)
    }
}
