@file:Suppress("TestFunctionName")

package com.github.ajalt.colormath

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

// https://github.com/colour-science/colour/blob/cea486394e3925718a0f3dd427edc9dd5b674f0c/colour/models/rgb/transfer_functions/tests/test_rimm_romm_rgb.py
class RGBColorSpacesTest {
    @Test
    fun ADOBE_RGB() = doTest(ADOBE_RGB, 0.18, 0.45852946567989455)

    @Test
    fun BT_2020() = doTest(BT_2020, 0.18, 0.409007728864150)

    @Test
    fun DCI_P3() = doTest(DCI_P3, 0.18, 0.5170902489415321)

    @Test
    fun DISPLAY_P3() = doTest(DISPLAY_P3, 0.18, 0.46135612950044164)

    @Test
    fun ROMM_RGB() = doTest(ROMM_RGB, 0.18, 0.385711424751138)

    private fun doTest(space: RGBColorSpace, linear: Double, nonlinear: Double) = forAll(
        row(0.0, 0.0, "eotf"),
        row(nonlinear, linear, "eotf"),
        row(1.0, 1.0, "eotf"),

        row(0.0, 0.0, "oetf"),
        row(linear, nonlinear, "oetf"),
        row(1.0, 1.0, "oetf"),
    ) { input, ex, func ->
        val actual = when (func) {
            "eotf" -> space.transferFunctions.eotf(input.toFloat())
            else -> space.transferFunctions.oetf(input.toFloat())
        }
        actual.toDouble() shouldBe (ex plusOrMinus 1e-10)
    }
}
