package com.github.ajalt.colormath

import kotlin.js.JsName
import kotlin.test.Test


class OklchTest {
    @Test
    fun roundtrip() = roundtripTest(Oklch(0.01, 0.02, 0.03, 0.04), intermediate = Oklab)

    @Test
    @JsName("Oklab_to_Oklch")
    fun `Oklab to Oklch`() = testColorConversions(
        Oklab(0.0, 0.0, 0.0) to Oklch(0.0, 0.0, Double.NaN),
        Oklab(0.18, 0.18, 0.18) to Oklch(0.18, 0.25455844, 45.0),
        Oklab(0.25, 0.5, 0.75) to Oklch(0.25, 0.90138782, 56.30993247),
        Oklab(1.0, 1.0, 1.0) to Oklch(1.0, 1.41421356, 45.0),
    )
}
