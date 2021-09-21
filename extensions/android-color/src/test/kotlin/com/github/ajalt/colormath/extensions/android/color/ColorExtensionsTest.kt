package com.github.ajalt.colormath.extensions.android.color

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ColorExtensionsTest {

    private val androidBlue = Color.valueOf(Color.BLUE)
    private val colormathBlue = com.github.ajalt.colormath.model.RGB(0, 0, 1, 1)

    @Test
    fun toColormathColor() {
        assertEquals(colormathBlue, androidBlue.toColormathColor())
    }

    @Test
    fun toColormathSRGB() {
        assertEquals(colormathBlue, androidBlue.toColormathSRGB())
    }

    @Test
    fun toAndroidColor() {
        assertEquals(androidBlue, colormathBlue.toAndroidColor())
    }
}
