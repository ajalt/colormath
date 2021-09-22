package com.github.ajalt.colormath.extensions.android.colorint

import android.graphics.Color
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.github.ajalt.colormath.extensions.android.colorint.fromColorInt
import com.github.ajalt.colormath.extensions.android.colorint.toColorInt
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBInt
import com.github.ajalt.colormath.model.SRGB

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ColorIntExtensionsTest {
    private val colormathBlue = com.github.ajalt.colormath.model.RGB(0, 0, 1, 1)

    @org.junit.Test
    fun toColorInt() {
        assertEquals(Color.BLUE, colormathBlue.toColorInt())
    }

    @org.junit.Test
    fun fromColorIntRGB() {
        assertEquals(RGB.fromColorInt(Color.BLUE), colormathBlue)
    }

    @org.junit.Test
    fun fromColorIntRGBInt() {
        assertEquals(RGBInt.fromColorInt(Color.BLUE), colormathBlue.toRGBInt())
    }
}
