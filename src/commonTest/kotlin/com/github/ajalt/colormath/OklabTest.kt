package com.github.ajalt.colormath

import kotlin.js.JsName
import kotlin.test.Test

class OklabTest {
    @Test
    fun roundtrip() = roundtripTest(
        Oklab(0.1, 0.011, 0.012, 0.04),
        Oklab(0.1, 0.011, 0.012, 0.04f),
    )

    @Test
    @JsName("Oklab_to_XYZ")
    fun `Oklab to XYZ`() = testColorConversions(
        Oklab(0.00, 0.00, 0.00) to XYZ(0.0, 0.0, 0.0),
        Oklab(0.18, 0.18, 0.18) to XYZ(0.02802839, 0.00274835, -0.0037864),
        Oklab(0.40, 0.50, 0.60) to XYZ(0.43551106, 0.02244794, -0.15905992),
        Oklab(1.00, 1.00, 1.00) to XYZ(4.80596605, 0.47125258, -0.6492456),
    )

    @Test
    @JsName("Oklab_to_RGB")
    fun `Oklab to RGB`() = testColorConversions(
        Oklab(0.00, 0.00, 0.00) to RGB(0.0, 0.0, 0.0),
        Oklab(0.18, 0.18, 0.18) to RGB(0.3291339, -0.28640902, -0.03880515),
        Oklab(0.40, 0.50, 0.60) to RGB(1.17887364, -4.99505886, -1.91827315),
        Oklab(1.00, 1.00, 1.00) to RGB(3.22136291, -49.10991381, -6.65383232),
        tolerance = 5e-2
    )
}
