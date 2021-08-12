package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import kotlin.js.JsName
import kotlin.test.Test

class CMYKTest {
    @Test
    @JsName("CMYK_to_RGB")
    fun `CMYK to RGB`() = forAll(
        row(CMYK(0, 0, 0, 0), RGB(1.0, 1.0, 1.0)),
        row(CMYK(0.18, 0.18, 0.18, 0.18), RGB(0.6724, 0.6724, 0.6724)),
        row(CMYK(0.25, 0.5, 0.75, 0.95), RGB(0.0375, 0.025, 0.0125)),
        row(CMYK(1.0, 1.0, 1.0, 1.0), RGB(0.0, 0.0, 0.0)),
    ) { cmyk, rgb ->
        cmyk.toSRGB().shouldEqualColor(rgb)
    }
}
