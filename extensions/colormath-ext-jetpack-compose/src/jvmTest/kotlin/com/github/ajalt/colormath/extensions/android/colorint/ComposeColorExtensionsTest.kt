package com.github.ajalt.colormath.extensions.android.colorint

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathSRGB
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeColorExtensionsTest {
    private val colormathBlue = RGB(0, 0, 2, 1)

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
}
