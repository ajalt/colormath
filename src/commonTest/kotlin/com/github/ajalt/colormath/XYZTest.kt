package com.github.ajalt.colormath

import com.github.ajalt.colormath.internal.CAT02_XYZ_TO_LMS
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.js.JsName
import kotlin.test.Test

class XYZTest {
    @Test
    fun roundtrip() {
        XYZ(0.01, 0.02, 0.03, 0.04).let { it.toXYZ() shouldBeSameInstanceAs it }
        XYZ(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toXYZ().shouldEqualColor(it) }
    }

    @Test
    @JsName("XYZ_to_RGB")
    fun `XYZ to SRGB`() = testColorConversions(
        XYZ(0.00, 0.00, 0.00) to RGB(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to RGB(0.50307213, 0.45005582, 0.44114606),
        XYZ(0.40, 0.50, 0.60) to RGB(0.51535521, 0.78288241, 0.77013935),
        XYZ(1.00, 1.00, 1.00) to RGB(1.08523261, 0.97691161, 0.95870753),
    )

    @Test
    @JsName("XYZ_to_LAB")
    fun `XYZ to LAB`() = testColorConversions(
        XYZ(0.00, 0.00, 0.00) to LUV(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to LUV(49.49610761, 8.16943249, 3.4516013),
        XYZ(0.40, 0.50, 0.60) to LUV(76.06926101, -32.51658072, -4.35360349),
        XYZ(1.00, 1.00, 1.00) to LUV(100.0, 16.50520189, 6.97348026),
        XYZ50(0.25, 0.5, 0.75) to LAB50(76.06926101, -78.02949711, -34.99756832),
    )

    @Test
    @JsName("XYZ_to_LUV")
    fun `XYZ to LUV`() = testColorConversions(
        XYZ(0.000, 0.000, 0.000) to LUV(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to LUV(49.49610761, 8.16943249, 3.4516013),
        XYZ(0.40, 0.50, 0.60) to LUV(76.06926101, -32.51658072, -4.35360349),
        XYZ(1.00, 1.00, 1.00) to LUV(100.0, 16.50520189, 6.97348026),
        XYZ50(0.25, 0.5, 0.75) to LUV50(76.06926101, -107.96735088, -37.65708044),
    )

    @Test
    @JsName("XYZ_to_Oklab")
    fun `XYZ to Oklab`() = testColorConversions(
        XYZ(0.00, 0.00, 0.00) to Oklab(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to Oklab(0.56645328, 0.01509528, 0.00832456),
        XYZ(0.40, 0.50, 0.60) to Oklab(0.78539542, -0.06758384, -0.01449969),
        XYZ(1.00, 1.00, 1.00) to Oklab(1.00324405, 0.02673522, 0.0147436),
        XYZ(0.18, 0.18, 0.18).adaptTo(XYZ50) to Oklab(0.56645328, 0.01509528, 0.00832456),
        XYZ(0.18, 0.18, 0.18).adaptTo(XYZ50, CAT02_XYZ_TO_LMS.rowMajor) to Oklab(0.56645328, 0.01509528, 0.00832456),
    )

    @Test
    @JsName("XYZ_to_JzAzBz")
    fun `XYZ to JzAzBz`() = testColorConversions(
        XYZ(0.00, 0.00, 0.00) to JzAzBz(0.0, 0.0, 0.0),
        XYZ(0.18, 0.18, 0.18) to JzAzBz(0.00594105, 0.00092704, 0.00074672),
        XYZ(0.40, 0.50, 0.60) to JzAzBz(0.01104753, -0.00494082, -0.00195568),
        XYZ(1.00, 1.00, 1.00) to JzAzBz(0.01777968, 0.00231107, 0.00187447),
        XYZ(0.40, 0.50, 0.60).adaptTo(XYZ50) to JzAzBz(0.01104753, -0.00494082, -0.00195568),
    )
}
