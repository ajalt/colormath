package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.BT2020
import kotlin.js.JsName
import kotlin.test.Test

class ICtCpTest {
    @Test
    fun roundtrip() = roundtripTest(ICtCp(0.01, 0.011, 0.012, 0.04))

    @Test
    @JsName("ICtCp_to_BT2020")
    fun `ICtCp to BT2020`() = testColorConversions(
        ICtCp(0.00, 0.00, 0.00) to BT2020(0.0, 0.0, 0.0),
        ICtCp(0.08, 0.00, 0.00) to BT2020(0.41300407, 0.41300407, 0.41300407),
        ICtCp(0.10, 0.01, -0.01) to BT2020(0.51900627, 0.57112792, 0.64131823),
        ICtCp(0.15, 0.00, 0.00) to BT2020(1.00052666, 1.00052666, 1.00052666),
    )

    @Test
    @JsName("ICtCp_to_sRGB")
    fun `ICtCp to sRGB`() = testColorConversions(
        ICtCp(0.00, 0.00, 0.00) to SRGB(0.0, 0.0, 0.0),
        ICtCp(0.08, 0.00, 0.00) to SRGB(0.46526684, 0.46526684, 0.46526684),
        ICtCp(0.10, 0.01, -0.01) to SRGB(0.52319301, 0.6175152, 0.68473126),
        ICtCp(0.15, 0.00, 0.00) to SRGB(1.00046799, 1.00046799, 1.00046799),
    )
}
