package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class CMYKTest {
    @Test
    @JsName("CMYK_to_RGB")
    fun `CMYK to RGB`() = testColorConversions(
        CMYK(0.00, 0.00, 0.00, 0.00) to RGB(1.0, 1.0, 1.0),
        CMYK(0.18, 0.18, 0.18, 0.18) to RGB(0.6724, 0.6724, 0.6724),
        CMYK(0.40, 0.50, 0.60, 0.70) to RGB(0.18, 0.15, 0.12),
        CMYK(100, 100, 100, 100) to RGB(0.0, 0.0, 0.0),
        testInverse = false
    )
}
