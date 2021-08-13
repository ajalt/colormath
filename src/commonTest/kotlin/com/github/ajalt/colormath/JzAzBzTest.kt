package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class JzAzBzTest {
    @Test
    fun roundtrip() {
        JzAzBz(0.01, 0.011, 0.012, 0.04).let { it.toJzAzBz() shouldBeSameInstanceAs it }
        JzAzBz(0.01, 0.011, 0.012, 0.04f).let { it.toSRGB().toJzAzBz().shouldEqualColor(it) }
    }

    @Test
    @JsName("JzAzBz_to_XYZ")
    fun `JzAzBz to XYZ`() = testColorConversions(
        XYZ(0.00, 0.00, 0.00) to JzAzBz(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to JzAzBz(0.00594105, 0.00092704, 0.00074672),
        XYZ(0.40, 0.50, 0.60) to JzAzBz(0.01104753, -0.00494082, -0.00195568),
        XYZ(1.00, 1.00, 1.00) to JzAzBz(0.01777968, 0.00231107, 0.00187447),
    )
}
