package com.github.ajalt.colormath

import kotlin.js.JsName
import kotlin.test.Test


class JzCzHzTest {
    @Test
    fun roundtrip() = roundtripTest(
        JzCzHz(0.01, 0.02, 0.03, 0.04),
        JzCzHz(0.01, 0.02, 0.03, 0.04f),
    )

    @Test
    @JsName("JzCzHz_to_JzAzBz")
    fun `JzCzHz to JzAzBz`() = testColorConversions(
        JzCzHz(0.00, 0.00, Double.NaN) to JzAzBz(0.0, 0.0, 0.0),
        JzCzHz(0.18, 0.18, 64.80) to JzAzBz(0.18, 0.07664027, 0.16286887),
        JzCzHz(0.40, 0.50, 216.00) to JzAzBz(0.4, -0.4045085, -0.29389263),
        JzCzHz(1.00, 1.00, 0.00) to JzAzBz(1.0, 1.0, -0.0),
    )
}
