package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.js.JsName
import kotlin.test.Test

class CMYKTest {
    @Test
    @JsName("CMYK_to_RGB")
    fun `CMYK to RGB`() {
        forAll(
                row(CMYK(0, 0, 0, 0), RGB(255, 255, 255)),
                row(CMYK(0, 0, 0, 100), RGB(0, 0, 0)),
                row(CMYK(0, 100, 100, 0), RGB(255, 0, 0)),
                row(CMYK(100, 0, 100, 0), RGB(0, 255, 0)),
                row(CMYK(100, 100, 0, 0), RGB(0, 0, 255)),
                row(CMYK(0, 0, 100, 0), RGB(255, 255, 0)),
                row(CMYK(100, 0, 0, 0), RGB(0, 255, 255)),
                row(CMYK(0, 100, 0, 0), RGB(255, 0, 255)),
                row(CMYK(30, 0, 50, 22), RGB(139, 199, 99))
        ) { cmyk, rgb ->
            cmyk should convertTo(rgb)
        }
    }
}
