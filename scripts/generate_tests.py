"""
This script generates some of the color conversion test cases. It requires the `colour-science` library.
"""

import warnings

warnings.filterwarnings("ignore")

import colour
from colour.colorimetry import CCS_ILLUMINANTS
from colour.utilities.common import domain_range_scale
from colour.models import *
from functools import partial
from oklab import XYZ_to_Oklab, Oklab_to_XYZ

test_cases = [
    [0, 0, 0],
    [0.18, 0.18, 0.18],
    [0.4, 0.5, 0.6],
    [1.0, 1.0, 1.0],
]

cmyk_test_cases = [
    [0, 0, 0, 0],
    [0.18, 0.18, 0.18, 0.18],
    [0.4, 0.5, 0.6, 0.7],
    [1.0, 1.0, 1.0, 1.0],
]

ictcp_test_cases = [
    [0, 0, 0],
    [0.08, 0, 0],
    [0.1, 0.01, -0.01],
    [0.15, 0, 0],
]

illuminants = CCS_ILLUMINANTS['CIE 1931 2 Degree Standard Observer']

colour.RGB_COLOURSPACES['sRGB'].use_derived_transformation_matrices(True)


def convert_rgb(input, output, c, decode=True, encode=True):
    i = colour.RGB_COLOURSPACES[input]
    i.use_derived_transformation_matrices(True)
    o = colour.RGB_COLOURSPACES[output]
    o.use_derived_transformation_matrices(True)
    return RGB_to_RGB(c, i, o, apply_cctf_decoding=decode, apply_cctf_encoding=encode, is_12_bits_system=True)


def row(s1, v1, s2, v2):
    def f(n):
        s = f'{n:.8f}'.rstrip('0').replace('nan', 'NaN')
        return s + '0' if s.endswith('.') else s

    c1 = ', '.join(f'{v:.2f}' for v in v1)
    c2 = ', '.join(f(v) for v in v2)

    print(f'        {s1}({c1}) to {s2}({c2}),')


def cases(s1, s2, f, tests=test_cases, scale=()):
    for v in tests:
        with domain_range_scale('1'):
            v = v + list(f(v))
        for i, s in scale:
            v[i] *= s
        row(s1, v[:int(len(v) / 2 + 0.5)], s2, v[int(len(v) / 2 + 0.5):])


def rgb_tests():
    names_to_spaces = [
        ['ACES2065-1', 'ACES'],
        ['ACEScc', 'ACEScc'],
        ['ACEScct', 'ACEScct'],
        ['ACEScg', 'ACEScg'],
        ['Adobe RGB (1998)', 'ADOBE_RGB'],
        ['ITU-R BT.2020', 'BT_2020'],
        ['ITU-R BT.709', 'BT_709'],
        ['DCI-P3', 'DCI_P3'],
        ['Display P3', 'DISPLAY_P3'],
        ['ROMM RGB', 'ROMM_RGB'],
    ]

    for (name, space) in names_to_spaces:
        print(f'    @Test\n'
              f'    fun {space}Test() = doTest(')
        cases('SRGB', space, partial(convert_rgb, 'sRGB', name))
        cases(space, 'SRGB', partial(convert_rgb, name, 'sRGB'))

        print('    )\n')


def LCHuv_to_HCL(lchuv):
    return [lchuv[2], lchuv[1], lchuv[0]]


def compose(*fns):
    def f(c):
        val = c
        for fn in fns:
            val = fn(val)
        return val

    return f


def color_tests():
    tests = [
        ['XYZ', 'JzAzBz', XYZ_to_JzAzBz],
        ['XYZ', 'Oklab', XYZ_to_Oklab],
        ['XYZ', 'LUV', XYZ_to_Luv],
        ['XYZ', 'LAB', XYZ_to_Lab],
        ['XYZ', 'RGB', XYZ_to_sRGB],
        ['RGB', 'HSV', RGB_to_HSV, [(3, 360)]],
        ['RGB', 'HSL', RGB_to_HSL, [(3, 360)]],
        ['RGB', 'XYZ', sRGB_to_XYZ],
        ['RGB', 'LAB', compose(sRGB_to_XYZ, XYZ_to_Lab)],
        ['RGB', 'LUV', compose(sRGB_to_XYZ, XYZ_to_Luv)],
        ['RGB', 'CMYK', compose(RGB_to_CMY, CMY_to_CMYK)],
        ['RGB', 'Oklab', compose(sRGB_to_XYZ, XYZ_to_Oklab)],
        ['CMYK', 'RGB', compose(CMYK_to_CMY, CMY_to_RGB), [], cmyk_test_cases],
        ['HCL', 'LUV', LCHuv_to_Luv, [(2, 360)]],
        ['HSL', 'RGB', HSL_to_RGB, [(0, 360)]],
        ['HSL', 'HSV', compose(HSL_to_RGB, RGB_to_HSV), [(0, 360), (3, 360)]],
        ['HSV', 'RGB', HSV_to_RGB, [(0, 360)]],
        ['HSV', 'HSL', compose(HSV_to_RGB, RGB_to_HSL), [(0, 360), (3, 360)]],
        ['JzCzHz', 'JzAzBz', JCh_to_Jab, [(2, 360)]],
        ['LAB', 'XYZ', Lab_to_XYZ, [(0, 100), (1, 100), (2, 100)]],
        ['LAB50', 'XYZ50', partial(Lab_to_XYZ, illuminant=illuminants['D50']), [(5, 360)]],
        ['LAB', 'LCH', Lab_to_LCHab, [(i, 360 if i == 5 else 100) for i in range(6)]],
        ['LCH', 'LAB', LCHab_to_Lab, [(i, 360 if i == 2 else 100) for i in range(6)]],
        ['LUV', 'XYZ', Luv_to_XYZ, [(i, 100) for i in range(3)]],
        ['LUV', 'HCL', compose(Luv_to_LCHuv, LCHuv_to_HCL), [(i, 360 if i == 3 else 100) for i in range(6)]],
        ['Oklab', 'XYZ', Oklab_to_XYZ],
        ['Oklab', 'RGB', compose(Oklab_to_XYZ, XYZ_to_sRGB)],
        ['ICtCp', 'BT_2020', compose(ICTCP_to_RGB, partial(eotf_inverse_BT2020, is_12_bits_system=True)), [], ictcp_test_cases],
        ['ICtCp', 'SRGB', compose(ICTCP_to_RGB, partial(convert_rgb, 'ITU-R BT.2020', 'sRGB', decode=False)), [], ictcp_test_cases],
    ]

    for test in tests:
        l, r, f = test[:3]
        s = test[3] if len(test) > 3 else []
        c = test[4] if len(test) > 4 else test_cases
        print(f'    @Test\n'
              f'    fun {l}_to_{r}() = testColorConversions(')
        cases(l, r, f, scale=s, tests=c)
        print('    )\n')


if __name__ == '__main__':
    rgb_tests()
    color_tests()
