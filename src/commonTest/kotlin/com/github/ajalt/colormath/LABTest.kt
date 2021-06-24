package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import kotlin.js.JsName
import kotlin.test.Test

class LABTest {
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
    ) { lab, xyz ->
        lab.toXYZ().shouldEqualColor(xyz)
    }
}
