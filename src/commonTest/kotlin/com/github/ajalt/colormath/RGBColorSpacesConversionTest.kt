@file:Suppress("TestFunctionName")

package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.ACES
import com.github.ajalt.colormath.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.RGBColorSpaces.ACEScct
import com.github.ajalt.colormath.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.RGBColorSpaces.ADOBE_RGB
import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.RGBColorSpaces.BT_709
import com.github.ajalt.colormath.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.RGBColorSpaces.DISPLAY_P3
import com.github.ajalt.colormath.RGBColorSpaces.LINEAR_SRGB
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import kotlin.test.Test

class RGBColorSpacesConversionTest {
    @Test
    fun ACESTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ACES(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to ACES(0.02721178, 0.02721178, 0.02721178),
        SRGB(0.25, 0.5, 0.75) to ACES(0.19676816, 0.22893858, 0.4807652),
        SRGB(1.0, 1.0, 1.0) to ACES(1.0, 1.0, 1.0),
        ACES(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        ACES(0.18, 0.18, 0.18) to SRGB(0.46135613, 0.46135613, 0.46135613),
        ACES(0.25, 0.5, 0.75) to SRGB(-2.92911117, 0.76442919, 0.90379292),
        ACES(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun ACESccTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ACEScc(-0.35844749, -0.35844749, -0.35844749),
        SRGB(0.18, 0.18, 0.18) to ACEScc(0.25801228, 0.25801228, 0.25801228),
        SRGB(0.25, 0.5, 0.75) to ACEScc(0.38559204, 0.42481837, 0.4943421),
        SRGB(1.0, 1.0, 1.0) to ACEScc(0.55479452, 0.55479452, 0.55479452),
        ACEScc(0.0, 0.0, 0.0) to SRGB(0.01531972, 0.01531972, 0.01531972),
        ACEScc(0.18, 0.18, 0.18) to SRGB(0.10335595, 0.10335595, 0.10335595),
        ACEScc(0.25, 0.5, 0.75) to SRGB(-14.78210598, 0.72752276, 2.93978015),
        ACEScc(1.0, 1.0, 1.0) to SRGB(9.98190805, 9.98190805, 9.98190805),
    )

    @Test
    fun ACEScctTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ACEScct(0.07290553, 0.07290553, 0.07290553),
        SRGB(0.18, 0.18, 0.18) to ACEScct(0.25801228, 0.25801228, 0.25801228),
        SRGB(0.25, 0.5, 0.75) to ACEScct(0.38559204, 0.42481837, 0.4943421),
        SRGB(1.0, 1.0, 1.0) to ACEScct(0.55479452, 0.55479452, 0.55479452),
        ACEScct(0.0, 0.0, 0.0) to SRGB(-0.08936606, -0.08936606, -0.08936606),
        ACEScct(0.18, 0.18, 0.18) to SRGB(0.10335595, 0.10335595, 0.10335595),
        ACEScct(0.25, 0.5, 0.75) to SRGB(-14.78210598, 0.72752276, 2.93978015),
        ACEScct(1.0, 1.0, 1.0) to SRGB(9.98190805, 9.98190805, 9.98190805),
    )

    @Test
    fun ACEScgTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ACEScg(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to ACEScg(0.02721178, 0.02721178, 0.02721178),
        SRGB(0.25, 0.5, 0.75) to ACEScg(0.12812043, 0.2063003, 0.47992257),
        SRGB(1.0, 1.0, 1.0) to ACEScg(1.0, 1.0, 1.0),
        ACEScg(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        ACEScg(0.18, 0.18, 0.18) to SRGB(0.46135613, 0.46135613, 0.46135613),
        ACEScg(0.25, 0.5, 0.75) to SRGB(0.25650325, 0.75492906, 0.90293703),
        ACEScg(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun ADOBE_RGBTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ADOBE_RGB(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to ADOBE_RGB(0.1942107, 0.1942107, 0.1942107),
        SRGB(0.25, 0.5, 0.75) to ADOBE_RGB(0.34674071, 0.4961037, 0.73614257),
        SRGB(1.0, 1.0, 1.0) to ADOBE_RGB(1.0, 1.0, 1.0),
        ADOBE_RGB(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        ADOBE_RGB(0.18, 0.18, 0.18) to SRGB(0.16419367, 0.16419367, 0.16419367),
        ADOBE_RGB(0.25, 0.5, 0.75) to SRGB(-0.26405475, 0.5039929, 0.76401618),
        ADOBE_RGB(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun BT_2020Test() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to BT_2020(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to BT_2020(0.11784851, 0.11784851, 0.11784851),
        SRGB(0.25, 0.5, 0.75) to BT_2020(0.3319963, 0.44097427, 0.69642135),
        SRGB(1.0, 1.0, 1.0) to BT_2020(1.0, 1.0, 1.0),
        BT_2020(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        BT_2020(0.18, 0.18, 0.18) to SRGB(0.24167749, 0.24167749, 0.24167749),
        BT_2020(0.25, 0.5, 0.75) to SRGB(-0.82351434, 0.56552977, 0.79951101),
        BT_2020(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun BT_709Test() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to BT_709(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to BT_709(0.11808925, 0.11808925, 0.11808925),
        SRGB(0.25, 0.5, 0.75) to BT_709(0.18869271, 0.45018853, 0.7216247),
        SRGB(1.0, 1.0, 1.0) to BT_709(1.0, 1.0, 1.0),
        BT_709(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        BT_709(0.18, 0.18, 0.18) to SRGB(0.24145733, 0.24145733, 0.24145733),
        BT_709(0.25, 0.5, 0.75) to SRGB(0.3097387, 0.54645807, 0.77574057),
        BT_709(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun DCI_P3Test() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to DCI_P3(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to DCI_P3(0.25002501, 0.25002501, 0.25002501),
        SRGB(0.25, 0.5, 0.75) to DCI_P3(0.36301815, 0.54697769, 0.76310162),
        SRGB(1.0, 1.0, 1.0) to DCI_P3(1.0, 1.0, 1.0),
        DCI_P3(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        DCI_P3(0.18, 0.18, 0.18) to SRGB(0.10961308, 0.10961308, 0.10961308),
        DCI_P3(0.25, 0.5, 0.75) to SRGB(0.07107989, 0.45008315, 0.73708242),
        DCI_P3(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun DISPLAY_P3Test() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to DISPLAY_P3(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to DISPLAY_P3(0.18, 0.18, 0.18),
        SRGB(0.25, 0.5, 0.75) to DISPLAY_P3(0.31300491, 0.49410464, 0.7301505),
        SRGB(1.0, 1.0, 1.0) to DISPLAY_P3(1.0, 1.0, 1.0),
        DISPLAY_P3(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        DISPLAY_P3(0.18, 0.18, 0.18) to SRGB(0.18, 0.18, 0.18),
        DISPLAY_P3(0.25, 0.5, 0.75) to SRGB(0.12407597, 0.50734577, 0.77112741),
        DISPLAY_P3(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun ROMM_RGBTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to ROMM_RGB(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to ROMM_RGB(0.13502697, 0.13502697, 0.13502697),
        SRGB(0.25, 0.5, 0.75) to ROMM_RGB(0.37381189, 0.41499641, 0.6666124),
        SRGB(1.0, 1.0, 1.0) to ROMM_RGB(1.0, 1.0, 1.0),
        ROMM_RGB(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        ROMM_RGB(0.18, 0.18, 0.18) to SRGB(0.23654583, 0.23654583, 0.23654583),
        ROMM_RGB(0.25, 0.5, 0.75) to SRGB(-2.86956015, 0.61325653, 0.82261984),
        ROMM_RGB(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun LINEAR_SRGBTest() = testColorConversions(
        SRGB(0.0, 0.0, 0.0) to LINEAR_SRGB(0.0, 0.0, 0.0),
        SRGB(0.18, 0.18, 0.18) to LINEAR_SRGB(0.02721178, 0.02721178, 0.02721178),
        SRGB(0.25, 0.5, 0.75) to LINEAR_SRGB(0.05087609, 0.21404114, 0.52252155),
        SRGB(1.0, 1.0, 1.0) to LINEAR_SRGB(1.0, 1.0, 1.0),
        LINEAR_SRGB(0.0, 0.0, 0.0) to SRGB(0.0, 0.0, 0.0),
        LINEAR_SRGB(0.18, 0.18, 0.18) to SRGB(0.46135613, 0.46135613, 0.46135613),
        LINEAR_SRGB(0.25, 0.5, 0.75) to SRGB(0.53709873, 0.73535698, 0.88082502),
        LINEAR_SRGB(1.0, 1.0, 1.0) to SRGB(1.0, 1.0, 1.0),
    )

    @Test
    fun unchanged() = testColorConversions(
        SRGB(0.25, 0.5, 0.75) to SRGB(0.25, 0.5, 0.75),
    )

    @Test
    fun RGBToRGBConverter() {
        val actual = SRGB.converterTo(DCI_P3).convert(SRGB(0.25, 0.5, 0.75))
        actual.shouldEqualColor(DCI_P3(0.3630869292770936, 0.5469812989512097, 0.7630916001628543))
    }
}
