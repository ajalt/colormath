package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class RGBIntTest {
    @Test
    @JsName("RGBInt_to_RGB")
    fun `RGBInt to RGB`() = forAll(
        row(0x00000000u, RGB(0, 0, 0, 0f)),
        row(0x00800000u, RGB(128, 0, 0, 0f)),
        row(0x00008000u, RGB(0, 128, 0, 0f)),
        row(0x00808000u, RGB(128, 128, 0, 0f)),
        row(0x00000080u, RGB(0, 0, 128, 0f)),
        row(0x00800080u, RGB(128, 0, 128, 0f)),
        row(0x00008080u, RGB(0, 128, 128, 0f)),
        row(0x0000ff00u, RGB(0, 255, 0, 0f)),
        row(0x00ffff00u, RGB(255, 255, 0, 0f)),
        row(0xffffffffu, RGB(255, 255, 255, 1f)),
        row(0x33aaaaaau, RGB(170, 170, 170, .2f)),
    ) { rgba, rgb ->
        RGBInt(rgba).toRGB() shouldBe rgb
    }

    @Test
    @JsName("RGB_to_RGBInt")
    fun `RGB to RGBInt`() = forAll(
        row(RGB(0, 0, 0, 0f), 0x00000000u),
        row(RGB(128, 0, 0, 0f), 0x00800000u),
        row(RGB(0, 128, 0, 0f), 0x00008000u),
        row(RGB(128, 128, 0, 0f), 0x00808000u),
        row(RGB(0, 0, 128, 0f), 0x00000080u),
        row(RGB(128, 0, 128, 0f), 0x00800080u),
        row(RGB(0, 128, 128, 0f), 0x00008080u),
        row(RGB(0, 255, 0, 0f), 0x0000ff00u),
        row(RGB(255, 255, 0, 0f), 0x00ffff00u),
        row(RGB(255, 255, 255, 1f), 0xffffffffu),
        row(RGB(170, 170, 170, .2f), 0x33aaaaaau),
    ) { rgb, argb ->
        rgb.toRGBInt() shouldBe RGBInt(argb)
        rgb.toRGBInt().argb shouldBe RGBInt(argb).argb
    }
}
