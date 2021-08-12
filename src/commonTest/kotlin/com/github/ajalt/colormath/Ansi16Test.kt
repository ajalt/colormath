package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class Ansi16Test {
    @Test
    fun roundtrip() {
        Ansi16(30).let { it.toAnsi16() shouldBeSameInstanceAs it }
        Ansi16(31).let { it.toSRGB().toAnsi16().shouldEqualColor(it) }
    }

    @Test
    @JsName("Ansi16_to_RGB")
    fun `Ansi16 to RGB`() = forAll(
        row(30, RGB(0, 0, 0)),
        row(31, RGB(128, 0, 0)),
        row(32, RGB(0, 128, 0)),
        row(33, RGB(128, 128, 0)),
        row(34, RGB(0, 0, 128)),
        row(35, RGB(128, 0, 128)),
        row(36, RGB(0, 128, 128)),
        row(37, RGB(192, 192, 192)),
        row(90, RGB(128, 128, 128)),
        row(91, RGB(255, 0, 0)),
        row(92, RGB(0, 255, 0)),
        row(93, RGB(255, 255, 0)),
        row(94, RGB(0, 0, 255)),
        row(95, RGB(255, 0, 255)),
        row(96, RGB(0, 255, 255)),
        row(97, RGB(255, 255, 255))
    ) { ansi, rgb ->
        Ansi16(ansi).toSRGB().shouldEqualColor(rgb, 5e-3)
        Ansi16(ansi + 10).toSRGB().shouldEqualColor(rgb, 5e-3)
    }

    @Test
    @JsName("Ansi16_to_Ansi256")
    fun `Ansi16 to Ansi256`() = forAll(
        row(30, 0),
        row(31, 1),
        row(32, 2),
        row(33, 3),
        row(34, 4),
        row(35, 5),
        row(36, 6),
        row(37, 7),
        row(90, 8),
        row(91, 9),
        row(92, 10),
        row(93, 11),
        row(94, 12),
        row(95, 13),
        row(96, 14),
        row(97, 15)
    ) { ansi16, ansi256 ->
        Ansi16(ansi16).toAnsi256().shouldEqualColor(Ansi256(ansi256))
    }
}
