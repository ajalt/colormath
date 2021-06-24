package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import kotlin.js.JsName
import kotlin.test.Test

class LUVTest {
    @Test
    @JsName("LUV_to_XYZ")
    fun `LUV to XYZ`() = forAll(
        row(LUV(000.0, 000.0, 000.0), XYZ(0.0, 0.0, 0.0)),
        row(LUV(000.0, 100.0, 100.0), XYZ(0.0, 0.0, 0.0)),
        row(LUV(000.0, 000.0, 100.0), XYZ(0.0, 0.0, 0.0)),
        row(LUV(000.0, 100.0, 000.0), XYZ(0.0, 0.0, 0.0)),
        row(LUV(100.0, 000.0, 000.0), XYZ(0.950470, 1.000000, 1.088830)),
        row(LUV(100.0, 100.0, 100.0), XYZ(1.133803, 1.000000, 0.124034)),
        row(LUV(075.0, 075.0, 075.0), XYZ(0.547378, 0.482781, 0.059881)),
        row(LUV(050.0, 050.0, 050.0), XYZ(0.208831, 0.184187, 0.022845)),
        row(LUV(41.5279, 96.8363, 17.7521), XYZ(0.206539, 0.121972, 0.051346)),
        row(LUV(55.1164, -37.5931, 44.1377), XYZ(0.142225, 0.230428, 0.104916)),
        row(LUV(29.8057, -10.9632, -65.0675), XYZ(0.078188, 0.061572, 0.280960)),
    ) { luv, xyz ->
        luv.toXYZ().shouldEqualColor(xyz)
    }

    @Test
    @JsName("LUV_to_LCH")
    fun `LUV to LCH`() = forAll(
        row(LUV(000.0, 000.0, 000.0), LCH(0.0, 0.0, 0.0)),
        row(LUV(000.0, 100.0, 100.0), LCH(0.0, 0.0, 0.0)),
        row(LUV(000.0, 000.0, 100.0), LCH(0.0, 0.0, 0.0)),
        row(LUV(000.0, 100.0, 000.0), LCH(0.0, 0.0, 0.0)),
        row(LUV(100.0, 000.0, 000.0), LCH(100.0, 0.0, 0.0)),
        row(LUV(100.0, 100.0, 100.0), LCH(100.0, 141.421, 45.0)),
        row(LUV(075.0, 075.0, 075.0), LCH(075.0, 106.066, 45.0)),
        row(LUV(050.0, 050.0, 050.0), LCH(050.0, 70.7107, 45.0)),
    ) { luv, lch ->
        luv.toLCH().shouldEqualColor(lch)
    }
}
