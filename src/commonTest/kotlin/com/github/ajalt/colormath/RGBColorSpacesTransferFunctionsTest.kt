@file:Suppress("TestFunctionName")

package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.RGBColorSpaces.ADOBE_RGB
import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.RGBColorSpaces.DISPLAY_P3
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

// Test values from https://github.com/colour-science/colour/tree/develop/colour/models/rgb/transfer_functions/tests
class RGBColorSpacesTransferFunctionsTest {
    @Test
    fun SRGB() = doTest(SRGB, 0.46135612950044164)

    @Test
    fun ADOBE_RGB() = doTest(ADOBE_RGB, 0.45852946567989455)

    @Test
    fun BT_2020() = doTest(BT_2020, 0.409007728864150)

    @Test
    fun DCI_P3() = doTest(DCI_P3, 0.5170902489415321)

    @Test
    fun DISPLAY_P3() = doTest(DISPLAY_P3, 0.46135612950044164)

    @Test
    fun ROMM_RGB() = doTest(ROMM_RGB, 0.385711424751138)

    @Test
    fun ACEScc() = doTest(ACEScc, 0.413588402492442, -0.358447488584475, 0.554794520547945)

    // extra tests from [Academy S-2014-003], Appendix C
    @Test
    fun ACEScc_extra() = forAll(
        row(0.000000059605, -0.35828683, "oetf"),
        row(0.0011854, -0.000023420209, "oetf"),
        row(222.88, 0.4132216, "oetf"),
        row(0.1792, 1.000007, "oetf"),
        row(65504.0, 1.4679964, "oetf"),
    ) { input, ex, func ->
        doSingleTest(ACEScc, input, ex, func)
    }

    private fun doTest(space: RGBColorSpace, mid: Double, zero: Double = 0.0, one: Double = 1.0) {
        forAll(
            row(0.0, zero, "oetf"),
            row(0.18, mid, "oetf"),
            row(1.0, one, "oetf"),

            row(zero, 0.0, "eotf"),
            row(mid, 0.18, "eotf"),
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
        actual.toDouble() shouldBe (ex plusOrMinus 1e-10)
    }
}
