package com.github.ajalt.colormath

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.tables.row
import org.junit.Test

class CMYKTest {
    @Test
    fun `CMYK to RGB`() {
        forall(
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
            cmyk.toRGB() shouldBe rgb
        }
    }
}
