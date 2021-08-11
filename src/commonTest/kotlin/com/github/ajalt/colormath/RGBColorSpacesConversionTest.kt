@file:Suppress("TestFunctionName")

package com.github.ajalt.colormath

import com.github.ajalt.colormath.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.RGBColorSpaces.ACES
import com.github.ajalt.colormath.RGBColorSpaces.ACEScct
import com.github.ajalt.colormath.RGBColorSpaces.ADOBE_RGB
import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.RGBColorSpaces.BT_709
import com.github.ajalt.colormath.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.RGBColorSpaces.DISPLAY_P3
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import io.kotest.data.Row2
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import kotlin.test.Test

class RGBColorSpacesConversionTest {
    @Test
    fun ACESTest() = doTest(
        row(SRGB(0, 0, 0), ACES(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ACES(0.027211780951381354, 0.027211780951381347, 0.027211780951381354)),
        row(SRGB(0.25, 0.5, 0.75), ACES(0.19676816133432307, 0.2289385804650927, 0.4807652025663621)),
        row(SRGB(1.0, 1.0, 1.0), ACES(1.0, 0.9999999999999998, 0.9999999999999998)),
        row(ACES(0.18, 0.18, 0.18), SRGB(0.46135612950044164, 0.46135612950044164, 0.46135612950044164)),
        row(ACES(0.25, 0.5, 0.75), SRGB(-2.9291111700776002, 0.7644291942658412, 0.9037929227146047)),
        row(ACES(1.0, 1.0, 1.0), SRGB(0.9999999999999999, 0.9999999999999999, 1.0000000000000002)),
    )

    @Test
    fun ACESccTest() = doTest(
        row(SRGB(0, 0, 0), ACEScc(-0.35844748858447484, -0.35844748858447484, -0.35844748858447484)),
        row(SRGB(0.18, 0.18, 0.18), ACEScc(0.25801228259869213, 0.2580122825986921, 0.2580122825986921)),
        row(SRGB(0.25, 0.5, 0.75), ACEScc(0.38559203723692914, 0.42481836720288085, 0.4943420990218987)),
        row(SRGB(1.0, 1.0, 1.0), ACEScc(0.5547945205479452, 0.5547945205479452, 0.5547945205479452)),
        row(ACEScc(0.18, 0.18, 0.18), SRGB(0.1033559519996119, 0.10335595199961184, 0.10335595199961187)),
        row(ACEScc(0.25, 0.5, 0.75), SRGB(-14.782105977691305, 0.7275227591403709, 2.939780147900026)),
        row(ACEScc(1.0, 1.0, 1.0), SRGB(9.981908050622966, 9.98190805062296, 9.981908050622962)),
    )

    @Test
    fun ACEScctTest() = doTest(
        row(SRGB(0, 0, 0), ACEScct(0.0729055341958355, 0.0729055341958355, 0.0729055341958355)),
        row(SRGB(0.18, 0.18, 0.18), ACEScct(0.25801228259869213, 0.2580122825986921, 0.2580122825986921)),
        row(SRGB(0.25, 0.5, 0.75), ACEScct(0.38559203723692914, 0.42481836720288085, 0.4943420990218987)),
        row(SRGB(1.0, 1.0, 1.0), ACEScct(0.5547945205479452, 0.5547945205479452, 0.5547945205479452)),
        row(ACEScct(0.18, 0.18, 0.18), SRGB(0.1033559519996119, 0.10335595199961184, 0.10335595199961187)),
        row(ACEScct(0.25, 0.5, 0.75), SRGB(-14.782105977691305, 0.7275227591403709, 2.939780147900026)),
        row(ACEScct(1.0, 1.0, 1.0), SRGB(9.981908050622966, 9.98190805062296, 9.981908050622962)),
    )

    @Test
    fun ACEScgTest() = doTest(
        row(SRGB(0, 0, 0), ACEScg(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ACEScg(0.027211780951381367, 0.027211780951381347, 0.02721178095138135)),
        row(SRGB(0.25, 0.5, 0.75), ACEScg(0.12812043373403795, 0.2063002955817013, 0.4799225724728858)),
        row(SRGB(1.0, 1.0, 1.0), ACEScg(1.0000000000000004, 0.9999999999999997, 0.9999999999999998)),
        row(ACEScg(0.18, 0.18, 0.18), SRGB(0.46135612950044175, 0.4613561295004415, 0.46135612950044164)),
        row(ACEScg(0.25, 0.5, 0.75), SRGB(0.25650324766203353, 0.7549290645119907, 0.9029370307481546)),
        row(ACEScg(1.0, 1.0, 1.0), SRGB(1.0000000000000002, 0.9999999999999997, 0.9999999999999999)),
    )

    @Test
    fun ADOBE_RGBTest() = doTest(
        row(SRGB(0, 0, 0), ADOBE_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ADOBE_RGB(0.19421069833538995, 0.1942106983353899, 0.1942106983353899)),
        row(SRGB(0.25, 0.5, 0.75), ADOBE_RGB(0.3467407108034278, 0.49610369844871743, 0.7361425710870978)),
        row(SRGB(1.0, 1.0, 1.0), ADOBE_RGB(1.0000000000000002, 0.9999999999999999, 1.0)),
        row(ADOBE_RGB(0.18, 0.18, 0.18), SRGB(0.16419367274420552, 0.16419367274420557, 0.16419367274420557)),
        row(ADOBE_RGB(0.25, 0.5, 0.75), SRGB(-0.2640547458136307, 0.5039928957642429, 0.7640161834334724)),
        row(ADOBE_RGB(1.0, 1.0, 1.0), SRGB(0.9999999999999997, 0.9999999999999999, 0.9999999999999999)),
    )

    @Test
    fun BT_2020Test() = doTest(
        row(SRGB(0, 0, 0), BT_2020(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_2020(0.11784851454635895, 0.11784851454635892, 0.11784851454635895)),
        row(SRGB(0.25, 0.5, 0.75), BT_2020(0.3319962997501304, 0.4409742721083145, 0.6964213543123972)),
        row(SRGB(1.0, 1.0, 1.0), BT_2020(1.0, 0.9999999999999998, 1.0)),
        row(BT_2020(0.18, 0.18, 0.18), SRGB(0.24167749024381147, 0.2416774902438117, 0.24167749024381163)),
        row(BT_2020(0.25, 0.5, 0.75), SRGB(-0.8235143410463007, 0.5655297736347598, 0.7995110128924696)),
        row(BT_2020(1.0, 1.0, 1.0), SRGB(0.9999999999999994, 1.0000000000000002, 0.9999999999999999)),
    )

    @Test
    fun BT_709Test() = doTest(
        row(SRGB(0, 0, 0), BT_709(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_709(0.11808925451327973, 0.11808925451327973, 0.11808925451327973)),
        row(SRGB(0.25, 0.5, 0.75), BT_709(0.18869271351412634, 0.45018852940390686, 0.721624704062679)),
        row(SRGB(1.0, 1.0, 1.0), BT_709(1.0, 1.0, 1.0)),
        row(BT_709(0.18, 0.18, 0.18), SRGB(0.24145732933239766, 0.24145732933239766, 0.24145732933239766)),
        row(BT_709(0.25, 0.5, 0.75), SRGB(0.3097386979865703, 0.5464580719250025, 0.7757405707927303)),
        row(BT_709(1.0, 1.0, 1.0), SRGB(0.9999999999999999, 0.9999999999999999, 0.9999999999999999)),
    )

    @Test
    fun DCI_P3Test() = doTest(
        row(SRGB(0, 0, 0), DCI_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DCI_P3(0.250025007276923, 0.250025007276923, 0.25002500727692295)),
        row(SRGB(0.25, 0.5, 0.75), DCI_P3(0.3630181542777526, 0.5469776917353686, 0.7631016198330283)),
        row(SRGB(1.0, 1.0, 1.0), DCI_P3(1.0000000000000002, 1.0, 0.9999999999999999)),
        row(DCI_P3(0.18, 0.18, 0.18), SRGB(0.10961307972908302, 0.1096130797290831, 0.1096130797290831)),
        row(DCI_P3(0.25, 0.5, 0.75), SRGB(0.07107989058626049, 0.45008315297018736, 0.7370824168433339)),
        row(DCI_P3(1.0, 1.0, 1.0), SRGB(0.9999999999999997, 0.9999999999999999, 0.9999999999999999)),
    )

    @Test
    fun DISPLAY_P3Test() = doTest(
        row(SRGB(0, 0, 0), DISPLAY_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DISPLAY_P3(0.18000000000000005, 0.17999999999999997, 0.18)),
        row(SRGB(0.25, 0.5, 0.75), DISPLAY_P3(0.3130049131269912, 0.49410463759125406, 0.7301505039875678)),
        row(SRGB(1.0, 1.0, 1.0), DISPLAY_P3(1.0000000000000002, 0.9999999999999997, 0.9999999999999999)),
        row(DISPLAY_P3(0.18, 0.18, 0.18), SRGB(0.17999999999999994, 0.18, 0.18)),
        row(DISPLAY_P3(0.25, 0.5, 0.75), SRGB(0.12407596556950828, 0.507345768652414, 0.7711274073481098)),
        row(DISPLAY_P3(1.0, 1.0, 1.0), SRGB(0.9999999999999997, 0.9999999999999999, 0.9999999999999999)),
    )

    @Test
    fun ROMM_RGBTest() = doTest(
        row(SRGB(0, 0, 0), ROMM_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ROMM_RGB(0.13502697455007262, 0.13502697455007256, 0.1350269745500726)),
        row(SRGB(0.25, 0.5, 0.75), ROMM_RGB(0.3738118889813621, 0.41499641118962166, 0.6666124030861101)),
        row(SRGB(1.0, 1.0, 1.0), ROMM_RGB(1.0000000000000004, 0.9999999999999999, 0.9999999999999999)),
        row(ROMM_RGB(0.18, 0.18, 0.18), SRGB(0.236545832956084, 0.23654583295608422, 0.23654583295608417)),
        row(ROMM_RGB(0.25, 0.5, 0.75), SRGB(-2.869560149852153, 0.6132565250342326, 0.8226198381165032)),
        row(ROMM_RGB(1.0, 1.0, 1.0), SRGB(0.9999999999999992, 1.0000000000000002, 0.9999999999999999)),
    )

    @Test
    fun unchanged() = doTest(
        row(SRGB(0.25, 0.5, 0.75), SRGB(0.25, 0.5, 0.75)),
    )

    @Test
    fun RGBToRGBConverter() {
        val actual = SRGB.converterTo(DCI_P3).convert(SRGB(0.25, 0.5, 0.75))
        actual.shouldEqualColor(DCI_P3(0.3630869292770936, 0.5469812989512097, 0.7630916001628543))
    }

    private fun doTest(vararg rows: Row2<RGB, RGB>) = forAll(*rows) { l, r ->
        l.convertTo(r.model).shouldEqualColor(r, 1e-3)
        r.convertTo(l.model).shouldEqualColor(l, 1e-3)
    }
}

/* Test values generated with github.com/colour-science/ using the following script:

import colour

test_cases = [
    [0, 0, 0],
    ...
]

names_to_spaces = [
    ['ACES2065-1', 'ACES'],
    ...
]

def convert(input, output, c):
    i = colour.RGB_COLOURSPACES[input]
    i.use_derived_transformation_matrices(True)
    o = colour.RGB_COLOURSPACES[output]
    o.use_derived_transformation_matrices(True)
    return RGB_to_RGB(c, i, o, apply_cctf_decoding=True, apply_cctf_encoding=True, is_12_bits_system=True)

def main():
    for (name, space) in names_to_spaces:
        print(f"    @Test\n"
              f"    fun {space}Test() = doTest(")
        for r, g, b in test_cases:
            (rr, gg, bb) = convert('sRGB', name, [r, g, b])
            print(f"        row(SRGB({r}, {g}, {b}), {space}({rr}, {gg}, {bb})),")

        for r, g, b in test_cases:
            (rr, gg, bb) = convert(name, 'sRGB', [r, g, b])
            if rr != 0 and g != 0 and b != 0:
                print(f"        row({space}({r}, {g}, {b}), SRGB({rr}, {gg}, {bb})),")

        print("    )\n")
 */
