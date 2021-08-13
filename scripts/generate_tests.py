"""
This script generates most of the test cases for color conversions.
It requires a new version of colour-science that includes Oklab support.
"""
import warnings

warnings.filterwarnings("ignore")

import colour
from colour.colorimetry import CCS_ILLUMINANTS
from colour.utilities.common import domain_range_scale
from colour.models import *
from functools import partial

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

illuminants = CCS_ILLUMINANTS['CIE 1931 2 Degree Standard Observer']

colour.RGB_COLOURSPACES['sRGB'].use_derived_transformation_matrices(True)


def convert_rgb(input, output, c):
    i = colour.RGB_COLOURSPACES[input]
    i.use_derived_transformation_matrices(True)
    o = colour.RGB_COLOURSPACES[output]
    o.use_derived_transformation_matrices(True)
    return RGB_to_RGB(c, i, o, apply_cctf_decoding=True, apply_cctf_encoding=True, is_12_bits_system=True)


def row(s1, v1, s2, v2):
    def f(n):
        s = f'{n:.8f}'.rstrip('0')
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


def compose(first, second):
    return lambda c: second(first(c))


def color_tests():
    tests = [
        ['XYZ', 'JzAzBz', XYZ_to_JzAzBz, []],
        ['XYZ', 'Oklab', XYZ_to_Oklab, []],
        ['XYZ', 'LUV', XYZ_to_Luv, []],
        ['XYZ', 'LAB', XYZ_to_Lab, []],
        ['XYZ', 'RGB', XYZ_to_sRGB, []],
        ['RGB', 'HSV', RGB_to_HSV, [(3, 360)]],
        ['RGB', 'HSL', RGB_to_HSL, [(3, 360)]],
        ['RGB', 'XYZ', sRGB_to_XYZ, []],
        ['RGB', 'LAB', compose(sRGB_to_XYZ, XYZ_to_Lab), []],
        ['RGB', 'LUV', compose(sRGB_to_XYZ, XYZ_to_Luv), []],
        ['RGB', 'CMYK', compose(RGB_to_CMY, CMY_to_CMYK), []],
        ['RGB', 'Oklab', compose(sRGB_to_XYZ, XYZ_to_Oklab), []],
        ['CMYK', 'RGB', compose(CMYK_to_CMY, CMY_to_RGB), []],
        ['HCL', 'LUV', LCHuv_to_Luv, [(2, 360)]],
        ['HSL', 'RGB', HSL_to_RGB, [(0, 360)]],
        ['HSL', 'HSV', compose(HSL_to_RGB, RGB_to_HSV), [(0, 360), (3, 360)]],
        ['HSV', 'RGB', HSV_to_RGB, [(0, 360)]],
        ['HSV', 'HSL', compose(HSV_to_RGB, RGB_to_HSL), [(0, 360), (3, 360)]],
        ['XYZ', 'JzAzBz', XYZ_to_JzAzBz, []],
        ['JzCzHz', 'JzAzBz', JCh_to_Jab, [(2, 360)]],
        ['LAB', 'XYZ', Lab_to_XYZ, [(0, 100), (1, 100), (2, 100)]],
        ['LAB50', 'XYZ50', partial(Lab_to_XYZ, illuminant=illuminants['D50']), [(5, 360)]],
        ['LAB', 'LCH', Lab_to_LCHab, [(i, 360 if i == 5 else 100) for i in range(6)]],
        ['LCH', 'LAB', LCHab_to_Lab, [(i, 360 if i == 2 else 100) for i in range(6)]],
        ['LUV', 'XYZ', Luv_to_XYZ, [(i, 100) for i in range(3)]],
        ['LUV', 'HCL', compose(Luv_to_LCHuv, LCHuv_to_HCL), [(i, 360 if i == 3 else 100) for i in range(6)]],
        ['Oklab', 'XYZ', Oklab_to_XYZ, []],
        ['Oklab', 'RGB', compose(Oklab_to_XYZ, XYZ_to_sRGB), []],
    ]

    for l, r, f, s in tests:
        print(f'    @Test\n'
              f'    fun {l}_to_{r}() = testColorConversions(')
        cases(l, r, f, scale=s)
        print('    )\n')


if __name__ == '__main__':
    color_tests()
