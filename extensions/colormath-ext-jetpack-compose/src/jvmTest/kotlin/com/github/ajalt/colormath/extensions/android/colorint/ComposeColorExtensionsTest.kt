package com.github.ajalt.colormath.extensions.android.colorint

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.convertTo
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathSRGB
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.JzAzBz
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.Oklab
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ACES
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.BT709
import com.github.ajalt.colormath.model.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.RGBColorSpaces.SRGB
import com.github.ajalt.colormath.model.XYZ
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ComposeColorExtensionsTest {
    private val colormathBlue = RGB(0, 0, 1, 1)

    // TODO(kotest): once kotest is released, go back to using it
    @Test
    fun toColormathColor() {
        assertEquals(colormathBlue, Color.Blue.toColormathColor())
    }

    @Test
    fun toColormathSRGB() {
        assertEquals(colormathBlue, Color.Blue.toColormathSRGB())
    }

    @Test
    fun toComposeColor() {
        assertEquals(Color.Blue, colormathBlue.toComposeColor())
    }

    @Test
    fun outOfGamut() = listOf<ColorSpace<*>>(
        SRGB,
        ACES,
        ACEScg,
        AdobeRGB,
        BT2020,
        BT709,
        LAB50,
        XYZ50,
        DCI_P3,
        DisplayP3,
        LinearSRGB,
        ROMM_RGB,
        XYZ,
        Oklab,
        JzAzBz,
    ).forEach { space: ColorSpace<*> ->
        val color = RGB(9, 9, 9, 9).convertTo(space)

        // shouldNotThrow

        color.clamp().toComposeColor()

        // shouldThrow
        try {
            color.toComposeColor()
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }
}
