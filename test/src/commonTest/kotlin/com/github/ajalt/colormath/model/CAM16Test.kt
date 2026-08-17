package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.companionTest
import com.github.ajalt.colormath.roundtripTest
import com.github.ajalt.colormath.shouldBeFloat
import com.github.ajalt.colormath.shouldEqualColor
import com.github.ajalt.colormath.testColorConversions
import kotlin.js.JsName
import kotlin.test.Test

class CAM16Test {
    @Test
    fun roundtrip() = roundtripTest(CAM16(30.0, 20.0, 40.0, 0.5))

    @Test
    fun companion() = companionTest(CAM16, CAM16ColorSpaces.CAM16Default)

    // Expected values generated with material-color-utilities' Cam16.java
    @[Test JsName("RGB_to_CAM16")]
    fun `RGB to CAM16`() = testColorConversions(
        RGB("#ff0000") to CAM16(46.445185, 113.357887, 27.408225),
        RGB("#00ff00") to CAM16(79.331577, 108.410061, 142.139894),
        RGB("#0000ff") to CAM16(25.465629, 87.230694, 282.788180),
        RGB("#ffff00") to CAM16(94.745557, 75.509488, 111.051140),
        RGB("#00ffff") to CAM16(85.234446, 58.954409, 196.544900),
        RGB("#ff00ff") to CAM16(55.256242, 107.400226, 334.635185),
        RGB("#000000") to CAM16(0.000000, 0.000000, 0.000000),
        RGB("#123456") to CAM16(15.288623, 29.908670, 255.347283),
        RGB("#fa8072") to CAM16(60.522543, 53.759847, 25.589078),
        RGB("#6750a4") to CAM16(31.399821, 47.856526, 298.980997),
        RGB("#1b6ef3") to CAM16(39.436565, 69.625010, 268.986801),
        RGB("#008772") to CAM16(38.619551, 42.233502, 178.866549),
        RGB("#9a4058") to CAM16(31.992232, 47.952230, 5.702380),
        RGB("#f0e68c") to CAM16(86.394528, 36.008669, 105.605745),
        tolerance = 0.05,
    )

    // The hue of these near-achromatic colors is extremely sensitive to the small differences
    // between colormath's sRGB matrices and the ones material-color-utilities derives its expected
    // values with, so it's excluded from the comparison.
    @[Test JsName("RGB_to_CAM16_achromatic")]
    fun `RGB to CAM16 achromatic`() = testColorConversions(
        RGB("#ffffff") to CAM16(100.000000, 2.869035, 209.491959),
        RGB("#808080") to CAM16(43.472528, 1.896022, 209.493826),
        tolerance = 0.05,
        ignorePolar = true,
    )

    @[Test JsName("CAM16_derived_dimensions")]
    fun `CAM16 derived dimensions`() {
        val cam = RGB("#ff0000").toCAM16()
        cam.q.shouldBeFloat(105.988792f, 0.01)
        cam.m.shouldBeFloat(89.494082f, 0.01)
        cam.s.shouldBeFloat(91.889775f, 0.01)
        cam.jStar.shouldBeFloat(59.584819f, 0.01)
        cam.aStar.shouldBeFloat(43.297655f, 0.01)
        cam.bStar.shouldBeFloat(22.451259f, 0.01)
    }

    @[Test JsName("CAM16_custom_viewing_conditions")]
    fun `CAM16 custom viewing conditions`() {
        val space = CAM16ColorSpace(CAM16ViewingConditions(backgroundLstar = 20.0))
        val rgb = RGB("#6750a4")
        space.convert(rgb).toSRGB().shouldEqualColor(rgb, 1e-4)
    }
}
