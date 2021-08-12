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
        row(SRGB(0.0, 0.0, 0.0), ACES(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ACES(0.02721178, 0.02721178, 0.02721178)),
        row(SRGB(0.25, 0.5, 0.75), ACES(0.19676816, 0.22893858, 0.4807652)),
        row(SRGB(1.0, 1.0, 1.0), ACES(1.0, 1.0, 1.0)),
        row(ACES(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(ACES(0.18, 0.18, 0.18), SRGB(0.46135613, 0.46135613, 0.46135613)),
        row(ACES(0.25, 0.5, 0.75), SRGB(-2.92911117, 0.76442919, 0.90379292)),
        row(ACES(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun ACESccTest() = doTest(
        row(SRGB(0.0, 0.0, 0.0), ACEScc(-0.35844749, -0.35844749, -0.35844749)),
        row(SRGB(0.18, 0.18, 0.18), ACEScc(0.25801228, 0.25801228, 0.25801228)),
        row(SRGB(0.25, 0.5, 0.75), ACEScc(0.38559204, 0.42481837, 0.4943421)),
        row(SRGB(1.0, 1.0, 1.0), ACEScc(0.55479452, 0.55479452, 0.55479452)),
        row(ACEScc(0.0, 0.0, 0.0), SRGB(0.01531972, 0.01531972, 0.01531972)),
        row(ACEScc(0.18, 0.18, 0.18), SRGB(0.10335595, 0.10335595, 0.10335595)),
        row(ACEScc(0.25, 0.5, 0.75), SRGB(-14.78210598, 0.72752276, 2.93978015)),
        row(ACEScc(1.0, 1.0, 1.0), SRGB(9.98190805, 9.98190805, 9.98190805)),
    )

    @Test
    fun ACEScctTest() = doTest(
        row(SRGB(0.0, 0.0, 0.0), ACEScct(0.07290553, 0.07290553, 0.07290553)),
        row(SRGB(0.18, 0.18, 0.18), ACEScct(0.25801228, 0.25801228, 0.25801228)),
        row(SRGB(0.25, 0.5, 0.75), ACEScct(0.38559204, 0.42481837, 0.4943421)),
        row(SRGB(1.0, 1.0, 1.0), ACEScct(0.55479452, 0.55479452, 0.55479452)),
        row(ACEScct(0.0, 0.0, 0.0), SRGB(-0.08936606, -0.08936606, -0.08936606)),
        row(ACEScct(0.18, 0.18, 0.18), SRGB(0.10335595, 0.10335595, 0.10335595)),
        row(ACEScct(0.25, 0.5, 0.75), SRGB(-14.78210598, 0.72752276, 2.93978015)),
        row(ACEScct(1.0, 1.0, 1.0), SRGB(9.98190805, 9.98190805, 9.98190805)),
    )

    @Test
    fun ACEScgTest() = doTest(
        row(SRGB(0.0, 0.0, 0.0), ACEScg(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ACEScg(0.02721178, 0.02721178, 0.02721178)),
        row(SRGB(0.25, 0.5, 0.75), ACEScg(0.12812043, 0.2063003, 0.47992257)),
        row(SRGB(1.0, 1.0, 1.0), ACEScg(1.0, 1.0, 1.0)),
        row(ACEScg(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(ACEScg(0.18, 0.18, 0.18), SRGB(0.46135613, 0.46135613, 0.46135613)),
        row(ACEScg(0.25, 0.5, 0.75), SRGB(0.25650325, 0.75492906, 0.90293703)),
        row(ACEScg(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun ADOBE_RGBTest() = doTest(
        row(SRGB(0.0, 0.0, 0.0), ADOBE_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ADOBE_RGB(0.1942107, 0.1942107, 0.1942107)),
        row(SRGB(0.25, 0.5, 0.75), ADOBE_RGB(0.34674071, 0.4961037, 0.73614257)),
        row(SRGB(1.0, 1.0, 1.0), ADOBE_RGB(1.0, 1.0, 1.0)),
        row(ADOBE_RGB(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(ADOBE_RGB(0.18, 0.18, 0.18), SRGB(0.16419367, 0.16419367, 0.16419367)),
        row(ADOBE_RGB(0.25, 0.5, 0.75), SRGB(-0.26405475, 0.5039929, 0.76401618)),
        row(ADOBE_RGB(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun BT_2020Test() = doTest(
        row(SRGB(0.0, 0.0, 0.0), BT_2020(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_2020(0.11784851, 0.11784851, 0.11784851)),
        row(SRGB(0.25, 0.5, 0.75), BT_2020(0.3319963, 0.44097427, 0.69642135)),
        row(SRGB(1.0, 1.0, 1.0), BT_2020(1.0, 1.0, 1.0)),
        row(BT_2020(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(BT_2020(0.18, 0.18, 0.18), SRGB(0.24167749, 0.24167749, 0.24167749)),
        row(BT_2020(0.25, 0.5, 0.75), SRGB(-0.82351434, 0.56552977, 0.79951101)),
        row(BT_2020(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun BT_709Test() = doTest(
        row(SRGB(0.0, 0.0, 0.0), BT_709(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_709(0.11808925, 0.11808925, 0.11808925)),
        row(SRGB(0.25, 0.5, 0.75), BT_709(0.18869271, 0.45018853, 0.7216247)),
        row(SRGB(1.0, 1.0, 1.0), BT_709(1.0, 1.0, 1.0)),
        row(BT_709(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(BT_709(0.18, 0.18, 0.18), SRGB(0.24145733, 0.24145733, 0.24145733)),
        row(BT_709(0.25, 0.5, 0.75), SRGB(0.3097387, 0.54645807, 0.77574057)),
        row(BT_709(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun DCI_P3Test() = doTest(
        row(SRGB(0.0, 0.0, 0.0), DCI_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DCI_P3(0.25002501, 0.25002501, 0.25002501)),
        row(SRGB(0.25, 0.5, 0.75), DCI_P3(0.36301815, 0.54697769, 0.76310162)),
        row(SRGB(1.0, 1.0, 1.0), DCI_P3(1.0, 1.0, 1.0)),
        row(DCI_P3(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(DCI_P3(0.18, 0.18, 0.18), SRGB(0.10961308, 0.10961308, 0.10961308)),
        row(DCI_P3(0.25, 0.5, 0.75), SRGB(0.07107989, 0.45008315, 0.73708242)),
        row(DCI_P3(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun DISPLAY_P3Test() = doTest(
        row(SRGB(0.0, 0.0, 0.0), DISPLAY_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DISPLAY_P3(0.18, 0.18, 0.18)),
        row(SRGB(0.25, 0.5, 0.75), DISPLAY_P3(0.31300491, 0.49410464, 0.7301505)),
        row(SRGB(1.0, 1.0, 1.0), DISPLAY_P3(1.0, 1.0, 1.0)),
        row(DISPLAY_P3(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(DISPLAY_P3(0.18, 0.18, 0.18), SRGB(0.18, 0.18, 0.18)),
        row(DISPLAY_P3(0.25, 0.5, 0.75), SRGB(0.12407597, 0.50734577, 0.77112741)),
        row(DISPLAY_P3(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
    )

    @Test
    fun ROMM_RGBTest() = doTest(
        row(SRGB(0.0, 0.0, 0.0), ROMM_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ROMM_RGB(0.13502697, 0.13502697, 0.13502697)),
        row(SRGB(0.25, 0.5, 0.75), ROMM_RGB(0.37381189, 0.41499641, 0.6666124)),
        row(SRGB(1.0, 1.0, 1.0), ROMM_RGB(1.0, 1.0, 1.0)),
        row(ROMM_RGB(0.0, 0.0, 0.0), SRGB(0.0, 0.0, 0.0)),
        row(ROMM_RGB(0.18, 0.18, 0.18), SRGB(0.23654583, 0.23654583, 0.23654583)),
        row(ROMM_RGB(0.25, 0.5, 0.75), SRGB(-2.86956015, 0.61325653, 0.82261984)),
        row(ROMM_RGB(1.0, 1.0, 1.0), SRGB(1.0, 1.0, 1.0)),
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
        l.convertTo(r.model).shouldEqualColor(r, 1e-5)
        r.convertTo(l.model).shouldEqualColor(l, 1e-5)
    }
}

/* Test values generated with github.com/colour-science using the following script:

import colour

test_cases = [
    [0, 0, 0],
    ...
]

names_to_spaces = [
    ['ACES2065-1', 'ACES'],
    ...
]

def convert_rgb(input, output, c):
    i = colour.RGB_COLOURSPACES[input]
    i.use_derived_transformation_matrices(True)
    o = colour.RGB_COLOURSPACES[output]
    o.use_derived_transformation_matrices(True)
    return RGB_to_RGB(c, i, o, apply_cctf_decoding=True, apply_cctf_encoding=True, is_12_bits_system=True)

def row(s1, v11, v12, v13, s2, v21, v22, v23):
    def f(n):
        s = f'{n:.8f}'.rstrip('0')
        return s + '0' if s.endswith('.') else s
    print(f'        row({s1}({f(v11)}, {f(v12)}, {f(v13)}), {s2}({f(v21)}, {f(v22)}, {f(v23)})),')


def cases(s1, s2, f):
    for v11, v12, v13 in test_cases:
        (v21, v22, v23) = f([v11, v12, v13])
        row(s1, v11, v12, v13, s2, v21, v22, v23)

def rgb_tests():
    for (name, space) in names_to_spaces:
        print(f'    @Test\n'
              f'    fun {space}Test() = doTest(')
        cases('SRGB', space, partial(convert_rgb, 'sRGB', name))
        cases(space, 'SRGB', partial(convert_rgb, name, 'sRGB'))
        print('    )\n')
 */
