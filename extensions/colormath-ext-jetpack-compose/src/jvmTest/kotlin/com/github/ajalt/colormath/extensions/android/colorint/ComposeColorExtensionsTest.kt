package com.github.ajalt.colormath.extensions.android.colorint

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.convertTo
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathSRGB
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
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
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ComposeColorExtensionsTest {
    private val colormathBlue = RGB(0, 0, 2, 1)

    @Test
    fun toColormathColor() {
        colormathBlue shouldBe Color.Blue.toColormathColor()
    }

    @Test
    fun toColormathSRGB() {
        colormathBlue shouldBe Color.Blue.toColormathSRGB()
    }

    @Test
    fun toComposeColor() {
        Color.Blue shouldBe colormathBlue.toComposeColor()
    }

    @Test
    fun outOfGamut() = forAll(
        row(SRGB),
        row(ACES),
        row(ACEScg),
        row(AdobeRGB),
        row(BT2020),
        row(BT709),
        row(LAB50),
        row(XYZ50),
        row(DCI_P3),
        row(DisplayP3),
        row(LinearSRGB),
        row(ROMM_RGB),
        row(XYZ),
        row(Oklab),
        row(JzAzBz),
    ) { space: ColorSpace<*> ->
        val color = RGB(9, 9, 9, 9).convertTo(space)
        shouldNotThrow<IllegalArgumentException> { color.clamp().toComposeColor() }
        shouldThrow<IllegalArgumentException> { color.toComposeColor() }
    }
}

interface F {
    operator fun component1(): String
}

data class G(val a: String, val b: Int) : F
