package com.github.ajalt.colormath

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
        XYZ(0.01, 0.02, 0.03, 0.04f).let { it.toRGB().toXYZ().shouldEqualColor(it) }
    }

    @Test
    @JsName("XYZ_to_RGB")
    fun `XYZ to RGB`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), RGB(0, 0, 0)),
        row(XYZ(0.250, 0.250, 0.250), RGB(149, 134, 131)),
        row(XYZ(0.500, 0.500, 0.500), RGB(204, 183, 180)),
        row(XYZ(0.750, 0.750, 0.750), RGB(244, 219, 215)),
        row(XYZ(0.950, 1.000, 1.088), RGB(255, 255, 255)),
    ) { xyz, rgb ->
        xyz should convertTo(rgb)
    }

    @Test
    @JsName("XYZ_to_RGB_HDR")
    fun `XYZ to RGB_HDR`() {
        XYZ(1.0, 1.0, 1.0).toRGB().shouldEqualColor(RGB(1.08516, 0.97692, 0.95881), 0.00005)
    }

    @Test
    @JsName("XYZ_to_LAB")
    fun `XYZ to LAB`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), LAB(0.0, 0.0, 0.0)),
        row(XYZ(0.250, 0.250, 0.250), LAB(57.075, 5.379, 3.524)),
        row(XYZ(0.500, 0.500, 0.500), LAB(76.069, 6.777, 4.440)),
        row(XYZ(0.750, 0.750, 0.750), LAB(89.393, 7.758, 5.082)),
        row(XYZ(1.000, 1.000, 1.000), LAB(100.0, 8.539, 5.594)),
        row(XYZ(1.000, 0.000, 0.000), LAB(0.0, 439.573, 0.0)),
        row(XYZ(0.000, 1.000, 0.000), LAB(100.000, -431.034, 172.414)),
        row(XYZ(0.000, 0.000, 1.000), LAB(0.000, 0.000, -166.820)),
        row(XYZ(.95047, 1.0000, 1.08883), LAB(100.0, 0.0, 0.0)),
    ) { xyz, lab ->
        xyz.toLAB().shouldEqualColor(lab)
    }

    @Test
    @JsName("XYZ_to_LUV")
    fun `XYZ to LUV`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(1.000, 0.000, 0.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(0.000, 0.000, 1.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(0.000, 1.000, 0.000), LUV(100.0000, -257.1918, 171.1628)),
        row(XYZ(0.95047, 1.0000, 1.08883), LUV(100.0, 0.0, 0.0)),
        row(XYZ(0.250, 0.250, 0.250), LUV(57.0754, 9.4131, 3.9680)),
        row(XYZ(0.500, 0.500, 0.500), LUV(76.0693, 12.5457, 5.2885)),
        row(XYZ(0.750, 0.750, 0.750), LUV(89.3930, 14.7431, 6.2149)),
        row(XYZ(1.000, 1.000, 1.000), LUV(100.0000, 16.4924, 6.9523)),
    ) { xyz, luv ->
        xyz.toLUV().shouldEqualColor(luv)
    }

    @Test
    @JsName("XYZ_to_Oklab")
    fun `XYZ to Oklab`() = forAll(
        row(XYZ(0.950, 1.000, 1.089), Oklab(1.000, +0.000, +0.000)),
        row(XYZ(1.000, 0.000, 0.000), Oklab(0.450, +1.236, -0.019)),
        row(XYZ(0.000, 1.000, 0.000), Oklab(0.922, -0.671, +0.263)),
        row(XYZ(0.000, 0.000, 1.000), Oklab(0.153, -1.415, -0.449)),
        row(XYZ(0.000, 0.000, 1.000).adaptTo(XYZ65), Oklab(0.153, -1.415, -0.449)),
    ) { xyz, oklab ->
        xyz.toOklab().shouldEqualColor(oklab)
    }

    @Test
    @JsName("XYZ_to_JzAzBz")
    fun `XYZ to JzAzBz`() = forAll(
        row(XYZ(0.20654008, 0.12197225, 0.05136952), JzAzBz(0.00535, +0.00924, +0.00526)),
        row(XYZ(0.14222010, 0.23042768, 0.10495772), JzAzBz(0.00619, -0.00608, +0.00534)),
        row(XYZ(0.96907232, 1.00000000, 1.12179215), JzAzBz(0.01766, +0.00064, -0.00052)),
        row(XYZ(0.96907232, 1.00000000, 1.12179215).adaptTo(XYZ65), JzAzBz(0.01766, +0.00064, -0.00052)),
    ) { xyz, jab ->
        xyz.toJzAzBz().shouldEqualColor(jab, 0.00001)
    }
}
