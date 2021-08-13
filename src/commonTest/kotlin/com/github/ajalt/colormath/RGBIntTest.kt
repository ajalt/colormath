package com.github.ajalt.colormath

import kotlin.js.JsName
import kotlin.test.Test

class RGBIntTest {
    @Test
    fun roundtrip() = roundtripTest(
        RGBInt(128, 128, 128, 128),
        RGBInt(128u, 128u, 128u, 128u),
        RGBInt(1128.toUByte(), 128.toUByte(), 128.toUByte(), 128.toUByte()),
    )

    @Test
    @JsName("RGBInt_to_RGB")
    fun `RGBInt to RGB`() = testColorConversions(
        RGBInt(0x00000000u) to RGB(0, 0, 0, 0f),
        RGBInt(0x00800000u) to RGB(128, 0, 0, 0f),
        RGBInt(0x00008000u) to RGB(0, 128, 0, 0f),
        RGBInt(0x00808000u) to RGB(128, 128, 0, 0f),
        RGBInt(0x00000080u) to RGB(0, 0, 128, 0f),
        RGBInt(0x00800080u) to RGB(128, 0, 128, 0f),
        RGBInt(0x00008080u) to RGB(0, 128, 128, 0f),
        RGBInt(0x0000ff00u) to RGB(0, 255, 0, 0f),
        RGBInt(0x00ffff00u) to RGB(255, 255, 0, 0f),
        RGBInt(0xffffffffu) to RGB(255, 255, 255, 1f),
        RGBInt(0x33aaaaaau) to RGB(170, 170, 170, .2f),
    )
}
