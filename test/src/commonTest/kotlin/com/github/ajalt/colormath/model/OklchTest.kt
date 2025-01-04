package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.shouldEqualColor
import com.github.ajalt.colormath.testColorConversions
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test


class OklchTest {
    @Test
    fun roundtrip() = roundtripTest(Oklch(0.01, 0.02, 0.03, 0.04), intermediate = Oklab)

    @[Test JsName("Oklab_to_Oklch")]
    fun `Oklab to Oklch`() = testColorConversions(
        Oklab(0.0, 0.0, 0.0) to Oklch(0.0, 0.0, Double.NaN),
        Oklab(0.18, 0.18, 0.18) to Oklch(0.18, 0.25455844, 45.0),
        Oklab(0.25, 0.5, 0.75) to Oklch(0.25, 0.90138782, 56.30993247),
        Oklab(1.0, 1.0, 1.0) to Oklch(1.0, 1.41421356, 45.0),
    )

    @Test
    fun clamp() {
        forAll(
            row(Oklch(0.0, 0.0, 0.0), Oklch(0.0, 0.0, 0.0)),
            row(Oklch(-1, -1, 361, 3), Oklch(0.0, 0.0, 1)),
        ) { color, ex ->
            color.clamp().shouldEqualColor(ex)
        }
        val oklch = Oklch(.9, .2, 359, .9)
        oklch.clamp().shouldBeSameInstanceAs(oklch)
    }
}
