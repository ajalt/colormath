package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import kotlin.js.JsName
import kotlin.test.Test

class ICtCpTest {
    @Test
    fun roundtrip() = roundtripTest(
        ICtCp(0.01, 0.011, 0.012, 0.04),
        ICtCp(0.01, 0.011, 0.012, 0.04f),
    )

    @Test
    @JsName("ICtCp_to_BT2020")
    fun `ICtCp to BT2020`() = testColorConversions(
        ICtCp(0.00, 0.00, 0.00) to BT_2020(0.0, 0.0, 0.0),
        ICtCp(0.08, 0.00, 0.00) to BT_2020(0.18328918, 0.18328918, 0.18328918),
        ICtCp(0.10, 0.01, -0.01) to BT_2020(0.27838044, 0.33323175, 0.41575777),
        ICtCp(0.15, 0.00, 0.00) to BT_2020(1.00106494, 1.00106494, 1.00106494),
    )

    @Test
    @JsName("ICtCp_to_sRGB")
    fun `ICtCp to sRGB`() = testColorConversions(
        ICtCp(0.00, 0.00, 0.00) to SRGB(0.0, 0.0, 0.0),
        ICtCp(0.08, 0.00, 0.00) to SRGB(0.24491111, 0.24491111, 0.24491111),
        ICtCp(0.10, 0.01, -0.01) to SRGB(0.28732999, 0.3950257, 0.47676618),
        ICtCp(0.15, 0.00, 0.00) to SRGB(1.00094629, 1.00094629, 1.00094629),
    )
}
