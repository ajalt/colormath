package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class LABTest {
    @Test
    fun roundtrip() {
        LAB(0.01, 0.02, 0.03, 0.04).let { it.toLAB() shouldBeSameInstanceAs it }
        LAB(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toLAB().shouldEqualColor(it) }
    }

    @Test
    @JsName("LAB_to_XYZ")
    fun `LAB to XYZ`() = testColorConversions(
        LAB(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        LAB(18.00, 18.00, 18.00) to XYZ(0.0338789, 0.02518041, 0.0091147),
        LAB(40.00, 50.00, 60.00) to XYZ(0.18810403, 0.11250974, 0.00626937),
        LAB(100.00, 100.00, 100.00) to XYZ(1.64238784, 1.0, 0.13613222),
    )

    @Test
    @JsName("LAB_to_LCH")
    fun `LAB to LCH`() = testColorConversions(
        LAB(0.00, 0.00, 0.00) to LCH(0.0, 0.0, 0.0),
        LAB(18.00, 18.00, 18.00) to LCH(18.0, 25.45584412, 45.0),
        LAB(40.00, 50.00, 60.00) to LCH(40.0, 78.10249676, 50.19442891),
        LAB(100.00, 100.00, 100.00) to LCH(100.0, 141.42135624, 45.0),
        LAB50(100.00, 100.00, 100.00) to LCH50(100.0, 141.42135624, 45.0),
    )
}
