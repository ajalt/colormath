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
        row(SRGB(0.18, 0.18, 0.18), ACES(0.02721308122643932, 0.027211046633354406, 0.027210335602502887)),
        row(SRGB(0.25, 0.5, 0.75), ACES(0.19678324500889952, 0.2289421536926496, 0.48074932724176234)),
        row(SRGB(1.0, 1.0, 1.0), ACES(1.0000477835339145, 0.9999730147016741, 0.9999468851788477)),
        row(ACES(0.18, 0.18, 0.18), SRGB(0.46132241042593264, 0.4613774606322875, 0.4613721443127156)),
        row(ACES(0.25, 0.5, 0.75), SRGB(-2.9292238799360937, 0.764411899918073, 0.9038085929975802)),
        row(ACES(1.0, 1.0, 1.0), SRGB(0.9999311064174999, 1.0000435829901335, 1.000032720879997)),
    )

    @Test
    fun ACESccTest() = doTest(
        row(SRGB(0, 0, 0), ACEScc(-0.35844748858447484, -0.35844748858447484, -0.35844748858447484)),
        row(SRGB(0.18, 0.18, 0.18), ACEScc(0.25801945895635026, 0.25800980357538333, 0.2580079648285931)),
        row(SRGB(0.25, 0.5, 0.75), ACEScc(0.38560775665409364, 0.4248202155041655, 0.49433939911807)),
        row(SRGB(1.0, 1.0, 1.0), ACEScc(0.5548016969056033, 0.5547920415246365, 0.5547902027778462)),
        row(ACEScc(0.18, 0.18, 0.18), SRGB(0.10334561105973844, 0.10336249382051074, 0.10336086341434031)),
        row(ACEScc(0.25, 0.5, 0.75), SRGB(-14.787274677934594, 0.7274217006928043, 2.939825340933999)),
        row(ACEScc(1.0, 1.0, 1.0), SRGB(9.981252621753029, 9.982322683947553, 9.982219345635277)),
    )

    @Test
    fun ACEScctTest() = doTest(
        row(SRGB(0, 0, 0), ACEScct(0.0729055341958355, 0.0729055341958355, 0.0729055341958355)),
        row(SRGB(0.18, 0.18, 0.18), ACEScct(0.25801945895635026, 0.25800980357538333, 0.2580079648285931)),
        row(SRGB(0.25, 0.5, 0.75), ACEScct(0.38560775665409364, 0.4248202155041655, 0.49433939911807)),
        row(SRGB(1.0, 1.0, 1.0), ACEScct(0.5548016969056033, 0.5547920415246365, 0.5547902027778462)),
        row(ACEScct(0.18, 0.18, 0.18), SRGB(0.10334561105973844, 0.10336249382051074, 0.10336086341434031)),
        row(ACEScct(0.25, 0.5, 0.75), SRGB(-14.787274677934594, 0.7274217006928043, 2.939825340933999)),
        row(ACEScct(1.0, 1.0, 1.0), SRGB(9.981252621753029, 9.982322683947553, 9.982219345635277)),
    )

    @Test
    fun ACEScgTest() = doTest(
        row(SRGB(0, 0, 0), ACEScg(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ACEScg(0.02721415254093488, 0.027210961750139144, 0.02721035414626606)),
        row(SRGB(0.25, 0.5, 0.75), ACEScg(0.12814489370041512, 0.2063049261794094, 0.4799068372856589)),
        row(SRGB(1.0, 1.0, 1.0), ACEScg(1.0000871530444024, 0.9999698953462959, 0.9999475666396902)),
        row(ACEScg(0.18, 0.18, 0.18), SRGB(0.46132241047933115, 0.46137746061731205, 0.4613721443017012)),
        row(ACEScg(0.25, 0.5, 0.75), SRGB(0.2564051790563611, 0.7549302543351386, 0.902956050327242)),
        row(ACEScg(1.0, 1.0, 1.0), SRGB(0.999931106526602, 1.0000435829595364, 1.0000327208574928)),
    )

    @Test
    fun ADOBE_RGBTest() = doTest(
        row(SRGB(0, 0, 0), ADOBE_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ADOBE_RGB(0.19422044072724304, 0.19420771523158886, 0.19420494490510687)),
        row(SRGB(0.25, 0.5, 0.75), ADOBE_RGB(0.3467889724938196, 0.49611004756735094, 0.7361285784390393)),
        row(SRGB(1.0, 1.0, 1.0), ADOBE_RGB(1.0000501640328603, 0.9999846398585318, 0.9999703753174652)),
        row(ADOBE_RGB(0.18, 0.18, 0.18), SRGB(0.1641818661625097, 0.16420066285704923, 0.164200895191797)),
        row(ADOBE_RGB(0.25, 0.5, 0.75), SRGB(-0.2642556468387574, 0.5039836789783473, 0.7640317326398893)),
        row(ADOBE_RGB(1.0, 1.0, 1.0), SRGB(0.9999431737990735, 1.000033644078124, 1.0000347623273682)),
    )

    @Test
    fun BT_2020Test() = doTest(
        row(SRGB(0, 0, 0), BT_2020(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_2020(0.11785733717751706, 0.1178455543475625, 0.11784327394171673)),
        row(SRGB(0.25, 0.5, 0.75), BT_2020(0.3320351887473708, 0.44097970398615094, 0.6964093254681359)),
        row(SRGB(1.0, 1.0, 1.0), BT_2020(1.0000446639870062, 0.999985014189281, 0.9999734697854361)),
        row(BT_2020(0.18, 0.18, 0.18), SRGB(0.24165811664875764, 0.2416897462467052, 0.24168669170530882)),
        row(BT_2020(0.25, 0.5, 0.75), SRGB(-0.8237584120894074, 0.5655213474120463, 0.7995256016338627)),
        row(BT_2020(1.0, 1.0, 1.0), SRGB(0.9999311065266016, 1.0000435829595367, 1.0000327208574926)),
    )

    @Test
    fun BT_709Test() = doTest(
        row(SRGB(0, 0, 0), BT_709(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), BT_709(0.1181060207001848, 0.11808484694595853, 0.11808353083965209)),
        row(SRGB(0.25, 0.5, 0.75), BT_709(0.1887947006114332, 0.45019111259715894, 0.7216111033342679)),
        row(SRGB(1.0, 1.0, 1.0), BT_709(1.0000848777128561, 0.9999776869818047, 0.999971024280631)),
        row(BT_709(0.18, 0.18, 0.18), SRGB(0.241437970114263, 0.24146957624025417, 0.24146652396559742)),
        row(BT_709(0.25, 0.5, 0.75), SRGB(0.3096739907618317, 0.546463816314688, 0.7757568112556653)),
        row(BT_709(1.0, 1.0, 1.0), SRGB(0.9999311065266016, 1.0000435829595367, 1.0000327208574926)),
    )

    @Test
    fun DCI_P3Test() = doTest(
        row(SRGB(0, 0, 0), DCI_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DCI_P3(0.25003892240166337, 0.2500214048833673, 0.2500198183512899)),
        row(SRGB(0.25, 0.5, 0.75), DCI_P3(0.3630869292770936, 0.5469812989512097, 0.7630916001628543)),
        row(SRGB(1.0, 1.0, 1.0), DCI_P3(1.0000556549318484, 0.9999855918670102, 0.9999792463734345)),
        row(DCI_P3(0.18, 0.18, 0.18), SRGB(0.10960233018706472, 0.1096198800373229, 0.10961818520882077)),
        row(DCI_P3(0.25, 0.5, 0.75), SRGB(0.07088640904630464, 0.45008172106569905, 0.7370964029711294)),
        row(DCI_P3(1.0, 1.0, 1.0), SRGB(0.9999311065266013, 1.0000435829595367, 1.0000327208574926)),
    )

    @Test
    fun DISPLAY_P3Test() = doTest(
        row(SRGB(0, 0, 0), DISPLAY_P3(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), DISPLAY_P3(0.18001303735385202, 0.17999628672705628, 0.17999474365537396)),
        row(SRGB(0.25, 0.5, 0.75), DISPLAY_P3(0.3130689861031088, 0.49410846902359445, 0.730139418681195)),
        row(SRGB(1.0, 1.0, 1.0), DISPLAY_P3(1.0000585293970805, 0.9999833297746569, 0.9999764023677425)),
        row(DISPLAY_P3(0.18, 0.18, 0.18), SRGB(0.1799846540604278, 0.18000970805259822, 0.18000728853223769)),
        row(DISPLAY_P3(0.25, 0.5, 0.75), SRGB(0.12394064057359408, 0.5073451140740632, 0.7711423435475175)),
        row(DISPLAY_P3(1.0, 1.0, 1.0), SRGB(0.9999311065266016, 1.0000435829595367, 1.0000327208574926)),
    )

    @Test
    fun ROMM_RGBTest() = doTest(
        row(SRGB(0, 0, 0), ROMM_RGB(0.0, 0.0, 0.0)),
        row(SRGB(0.18, 0.18, 0.18), ROMM_RGB(0.13504548557827814, 0.13502209413742708, 0.13504354556583129)),
        row(SRGB(0.25, 0.5, 0.75), ROMM_RGB(0.37387757963303164, 0.41498375313521907, 0.6667017004256504)),
        row(SRGB(1.0, 1.0, 1.0), ROMM_RGB(1.0001370913350256, 0.9999638560172013, 1.0001227237432664)),
        row(ROMM_RGB(0.18, 0.18, 0.18), SRGB(0.23650224263334807, 0.2365681565127628, 0.23651973482387884)),
        row(ROMM_RGB(0.25, 0.5, 0.75), SRGB(-2.8704674295237815, 0.6132853760486358, 0.8225336113484779)),
        row(ROMM_RGB(1.0, 1.0, 1.0), SRGB(0.9998422622267643, 1.0000807809600885, 0.9999055602023273)),
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
    o = colour.RGB_COLOURSPACES[output]
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
