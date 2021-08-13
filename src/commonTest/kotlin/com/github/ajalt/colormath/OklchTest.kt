package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test


class OklchTest {
    @Test
    fun roundtrip() {
        Oklch(0.01, 0.02, 0.03, 0.04).let { it.toOklch() shouldBeSameInstanceAs it }
        Oklch(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toOklch().shouldEqualColor(it) }
    }

    @Test
    @JsName("Oklch_to_RGB")
    fun `Oklch to RGB`() = forAll(
        row(Oklab(0.0, 0.0, 0.0), Oklch(0.0, 0.0, 0.0)),
        row(Oklab(0.18, 0.18, 0.18), Oklch(0.18, 0.25455844, 45.0)),
        row(Oklab(0.25, 0.5, 0.75), Oklch(0.25, 0.90138782, 56.30993247)),
        row(Oklab(1.0, 1.0, 1.0), Oklch(1.0, 1.41421356, 45.0)),
    ) { lch, oklch ->
        lch.toOklch().shouldEqualColor(oklch)
    }
}
