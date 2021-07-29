package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
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
    fun `LAB to XYZ`() = forAll(
        row(LAB(000.0, 000.0, 000.0), XYZ(0.0, 0.0, 0.0)),
        row(LAB(050.0, 050.0, 050.0), XYZ(0.28454, 0.18419, 0.03533)),
        row(LAB(075.0, 075.0, 075.0), XYZ(0.77563, 0.48278, 0.07476)),
        row(LAB(100.0, 100.0, 100.0), XYZ(1.64241, 1.00000, 0.13610)),
        row(LAB(100.0, 000.0, 000.0), XYZ(0.95047, 1.00000, 1.08883)),
        row(LAB(000.0, 100.0, 000.0), XYZ(0.0, 0.0, 0.0)),
        row(LAB(000.0, 000.0, 100.0), XYZ(0.0, 0.0, 0.0)),
        row(LAB50(50.0, 50.0, 50.0), XYZ50(0.288660, 0.184187, 0.026779)),
    ) { lab, xyz ->
        lab.toXYZ().shouldEqualColor(xyz)
    }

    @Test
    @JsName("LAB_to_LCH")
    fun `LAB to LCH`() = forAll(
        row(LAB(000.0, 000.0, 000.0), LCH(0.0, 0.0, 0.0)),
        row(LAB(050.0, 050.0, 050.0), LCH(50.00, 70.7107, 45.0000)),
        row(LAB(075.0, 075.0, 075.0), LCH(075.0, 106.0660, 45.0000)),
        row(LAB(100.0, 100.0, 100.0), LCH(100.0, 141.4214, 45.0000)),
        row(LAB(050.0, -80.0, -80.0), LCH(050.0, 113.1371, 225.0000)),
        row(LAB50(050.0, -80.0, -80.0), LCH50(050.0, 113.1371, 225.0000)),
    ) { lab, lch ->
        lab.toLCH().shouldEqualColor(lch)
    }
}
