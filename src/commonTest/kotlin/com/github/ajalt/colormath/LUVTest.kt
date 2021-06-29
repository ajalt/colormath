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
    @JsName("LUV_to_HCL")
    fun `LUV to HCL`() = forAll(
        row(LUV(100.0, 100.0, 100.0), HCL(45.0, 141.421, 100.0)),
        row(LUV(075.0, 075.0, 075.0), HCL(45.0, 106.066, 075.0)),
        row(LUV(050.0, 050.0, 050.0), HCL(45.0, 70.7107, 050.0)),
    ) { luv, lch ->
        luv.toHCL().shouldEqualColor(lch)
    }
}
