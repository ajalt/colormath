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
    fun `XYZ to SRGB`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), RGB(0, 0, 0)),
        row(XYZ(0.18, 0.18, 0.18), RGB(0.5030721320819881, 0.4500558169506224, 0.44114606149436336)),
        row(XYZ(0.25, 0.5, 0.75), RGB(-4.294700681990402, 0.8686733619936667, 0.856816547915403)),
        row(XYZ(1.0, 1.0, 1.0), RGB(1.0852326140993236, 0.9769116137895114, 0.9587075265920816)),
    ) { xyz, rgb ->
        xyz.toSRGB().shouldEqualColor(rgb)
    }

    @Test
    @JsName("XYZ_to_LAB")
    fun `XYZ to LAB`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), LAB(0.0, 0.0, 0.0)),
        row(XYZ(0.18, 0.18, 0.18), LAB(49.496107610119594, 4.8224578263568745, 3.1660684136282757)),
        row(XYZ(0.25, 0.5, 0.75), LAB(76.06926101415557, -76.48948023609003, -17.877278345905225)),
        row(XYZ(1.0, 1.0, 1.0), LAB(100.0, 8.541043556166471, 5.607416217267436)),
        row(XYZ50(0.25, 0.5, 0.75), LAB50(76.06926101415557, -78.02949711723284, -34.997568320103454)),
    ) { xyz, lab ->
        xyz.toLAB().shouldEqualColor(lab)
    }

    @Test
    @JsName("XYZ_to_LUV")
    fun `XYZ to LUV`() = forAll(
        row(XYZ(0.000, 0.000, 0.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(1.000, 0.000, 0.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(0.000, 0.000, 1.000), LUV(0.0, 0.0, 0.0)),
        row(XYZ(0.18, 0.18, 0.18), LUV(49.496107610119594, 8.169432489052687, 3.4516012955320456)),
        row(XYZ(0.25, 0.5, 0.75), LUV(76.06926101415557, -96.74413203429684, -18.11665019809977)),
        row(XYZ(1.0, 1.0, 1.0), LUV(100.0, 16.505201890627916, 6.973480263782111)),
        row(XYZ50(0.25, 0.5, 0.75), LUV50(76.06926101415557, -107.96735088745898, -37.65708044295732)),
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
        row(XYZ(0.000, 0.000, 1.000).adaptTo(XYZ50), Oklab(0.153, -1.415, -0.449)),
        row(XYZ(0.000, 0.000, 1.000).adaptTo(XYZ50, CAT02_XYZ_TO_LMS.rowMajor), Oklab(0.153, -1.415, -0.449)),
    ) { xyz, oklab ->
        xyz.toOklab().shouldEqualColor(oklab)
    }

    @Test
    @JsName("XYZ_to_JzAzBz")
    fun `XYZ to JzAzBz`() = forAll(
        row(XYZ(0.20654008, 0.12197225, 0.05136952), JzAzBz(0.00535, +0.00924, +0.00526)),
        row(XYZ(0.14222010, 0.23042768, 0.10495772), JzAzBz(0.00619, -0.00608, +0.00534)),
        row(XYZ(0.96907232, 1.00000000, 1.12179215), JzAzBz(0.01766, +0.00064, -0.00052)),
        row(XYZ(0.96907232, 1.00000000, 1.12179215).adaptTo(XYZ50), JzAzBz(0.01766, +0.00064, -0.00052)),
    ) { xyz, jab ->
        xyz.toJzAzBz().shouldEqualColor(jab)
    }
}
