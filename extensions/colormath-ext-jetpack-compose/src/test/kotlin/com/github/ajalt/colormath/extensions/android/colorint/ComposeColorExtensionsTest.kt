package com.github.ajalt.colormath.extensions.android.colorint

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathSRGB
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.RGB

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ComposeColorExtensionsTest {
    private val colormathBlue = com.github.ajalt.colormath.model.RGB(0, 0, 1, 1)

    @org.junit.Test
    fun toColormathColor() {
        assertEquals(colormathBlue, Color.Blue.toColormathColor())
    }

    @org.junit.Test
    fun toColormathSRGB() {
        assertEquals(colormathBlue, Color.Blue.toColormathSRGB())
    }

    @org.junit.Test
    fun toComposeColor() {
        assertEquals(Color.Blue, colormathBlue.toComposeColor())
    }
}
