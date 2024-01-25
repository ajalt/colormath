package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.convertToSpaceTest
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.LABColorSpaces.LAB65
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab50
import com.github.ajalt.colormath.model.LCHabColorSpaces.LCHab65
import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class LABTest {
    @Test
    fun roundtrip() = roundtripTest(LAB(0.01, 0.02, 0.03, 0.04))

    @Test
    fun conversion() = convertToSpaceTest(LAB65, LCHab50, LCHab65, HSL, to = LAB50)

    @Test
    @JsName("LAB_to_XYZ")
    fun `LAB to XYZ`() = testColorConversions(
        LAB(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        LAB(18.00, 18.00, 18.00) to XYZ(0.0338789, 0.02518041, 0.0091147),
        LAB(40.00, 50.00, 60.00) to XYZ(0.18810403, 0.11250974, 0.00626937),
        LAB(100.00, 100.00, 100.00) to XYZ(1.64238784, 1.0, 0.13613222),
    )

    @Test
    @JsName("LAB_to_LCHab")
    fun `LAB to LCHab`() = testColorConversions(
        LAB(0.00, 0.00, 0.00) to LCHab(0.0, 0.0, Double.NaN),
        LAB(18.00, 18.00, 18.00) to LCHab(18.0, 25.45584412, 45.0),
        LAB(40.00, 50.00, 60.00) to LCHab(40.0, 78.10249676, 50.19442891),
        LAB(100.00, 100.00, 100.00) to LCHab(100.0, 141.42135624, 45.0),
        LAB50(100.00, 100.00, 100.00) to LCHab50(100.0, 141.42135624, 45.0),
    )
}
