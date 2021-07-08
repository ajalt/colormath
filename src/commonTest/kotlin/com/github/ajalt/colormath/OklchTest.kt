package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test


class OklchTest {
    @Test
    fun roundtrip() {
        Oklch(0.01, 0.02, 0.03, 0.04).let { it.toOklch() shouldBeSameInstanceAs it }
        Oklch(0.01, 0.02, 0.03, 0.04f).let { it.toRGB().toOklch().shouldEqualColor(it) }
    }

    @Test
    @JsName("Oklch_to_RGB")
    // https://github.com/Evercoder/culori/blob/master/test/oklch.test.js
    fun `Oklch to RGB`() = forAll(
        row(Oklch(1.0000, 0.0000, 00.0000), RGB("#fff")),
        row(Oklch(0.1776, 0.0000, 00.0000), RGB("#111")),
        row(Oklch(0.0000, 0.0000, 00.0000), RGB("#000")),
        row(Oklch(0.6279, 0.2576, 29.2210), RGB("#f00")),
    ) { lch, rgb ->
        lch should convertTo(rgb)
    }
}
