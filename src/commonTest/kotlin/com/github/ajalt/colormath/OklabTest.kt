package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class OklabTest {
    @Test
    fun roundtrip() {
        Oklab(0.1, 0.011, 0.012, 0.04).let { it.toOklab() shouldBeSameInstanceAs it }
        Oklab(0.1, 0.011, 0.012, 0.04f).let { it.toSRGB().toOklab().shouldEqualColor(it) }
    }

    @Test
    @JsName("Oklab_to_XYZ")
    fun `Oklab to XYZ`() = forAll(
        row(Oklab(+1.000, +0.000, +0.000), XYZ(0.950, 1.000, 1.089)),
        row(Oklab(+0.450, +1.236, -0.019), XYZ(1.000, 0.000, 0.000)),
        row(Oklab(+0.922, -0.671, +0.263), XYZ(0.000, 1.000, 0.000)),
        row(Oklab(+0.153, -1.415, -0.449), XYZ(0.000, 0.000, 1.000)),
    ) { oklab, xyz ->
        oklab.toXYZ().shouldEqualColor(xyz, 0.002)
    }

    @Test
    @JsName("Oklab_to_RGB")
    fun `Oklab to RGB`() = forAll(
        row(Oklab(0.0, 0.0, 0.0), RGB(0.0, 0.0, 0.0)),
        row(Oklab(0.18, 0.0, 0.0), RGB(0.06871424, 0.0686901, 0.06865118)),
        row(Oklab(0.75, 0.25, 0.125), RGB(1.21849051, 0.2253556, 0.24573586)),
        row(Oklab(1.0, 0.0, 0.0), RGB(1.00018611, 0.99998017, 0.99964828)),
    ) { oklab, rgb ->
        oklab.toSRGB().shouldEqualColor(rgb)
    }
}
