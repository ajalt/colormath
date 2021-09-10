package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class Ansi16Test {
    @Test
    fun roundtrip() = roundtripTest(Ansi16(30))

    @Test
    @JsName("Ansi16_to_RGB")
    fun `Ansi16 to RGB`() = listOf(
        Ansi16(30) to RGBInt(0, 0, 0),
        Ansi16(31) to RGBInt(128, 0, 0),
        Ansi16(32) to RGBInt(0, 128, 0),
        Ansi16(33) to RGBInt(128, 128, 0),
        Ansi16(34) to RGBInt(0, 0, 128),
        Ansi16(35) to RGBInt(128, 0, 128),
        Ansi16(36) to RGBInt(0, 128, 128),
        Ansi16(37) to RGBInt(192, 192, 192),
        Ansi16(90) to RGBInt(128, 128, 128),
        Ansi16(91) to RGBInt(255, 0, 0),
        Ansi16(92) to RGBInt(0, 255, 0),
        Ansi16(93) to RGBInt(255, 255, 0),
        Ansi16(94) to RGBInt(0, 0, 255),
        Ansi16(95) to RGBInt(255, 0, 255),
        Ansi16(96) to RGBInt(0, 255, 255),
        Ansi16(97) to RGBInt(255, 255, 255),
    ).let {
        val tests = it + it.map { (l, r) -> Ansi16(l.code + 10) to r }
        testColorConversions(*tests.toTypedArray(), testInverse = false)
    }

    @Test
    @JsName("Ansi16_to_Ansi256")
    fun `Ansi16 to Ansi256`() = testColorConversions(
        Ansi16(30) to Ansi256(0),
        Ansi16(31) to Ansi256(1),
        Ansi16(32) to Ansi256(2),
        Ansi16(33) to Ansi256(3),
        Ansi16(34) to Ansi256(4),
        Ansi16(35) to Ansi256(5),
        Ansi16(36) to Ansi256(6),
        Ansi16(37) to Ansi256(7),
        Ansi16(90) to Ansi256(8),
        Ansi16(91) to Ansi256(9),
        Ansi16(92) to Ansi256(10),
        Ansi16(93) to Ansi256(11),
        Ansi16(94) to Ansi256(12),
        Ansi16(95) to Ansi256(13),
        Ansi16(96) to Ansi256(14),
        Ansi16(97) to Ansi256(15),
    )
}
