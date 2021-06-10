package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import kotlin.js.JsName
import kotlin.test.Test

class LinearRGBTest {
    @Test
    @JsName("Linear_to_RGB")
    fun `Linear to RGB`() = forAll(
        row(LinearRGB(0.0, 0.0, 0.0), RGB(0, 0, 0)),
        row(LinearRGB(0.00242, 0.00242, 0.00242), RGB(8, 8, 8)),
        row(LinearRGB(0.00518, 0.00518, 0.00518), RGB(16, 16, 16)),
        row(LinearRGB(0.01444, 0.01444, 0.01444), RGB(32, 32, 32)),
        row(LinearRGB(0.05126, 0.05126, 0.05126), RGB(64, 64, 64)),
        row(LinearRGB(0.21586, 0.21586, 0.21586), RGB(128, 128, 128)),
        row(LinearRGB(1.0, 1.0, 1.0), RGB(255, 255, 255)),
    ) { linear, rgb ->
        linear should convertTo(rgb)
    }
}
