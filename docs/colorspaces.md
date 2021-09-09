# Color Spaces

Colormath has a number of built-in color spaces and models.

!!! info "A note on terminology"
    A *color model* defines the components of a color. For example, RGB is a color model that
    defines colors with the additive combination of red, green, and blue light.

    A *color space* is a color model plus the details necessary to represent a color in that model. sRGB
    and DCI P3 both color spaces the use the RGB model, but they define different values for the exact
    colors of their RGB primaries.

## Built-in color models

### ANSI color codes

- [ANSI16][ANSI16]
- [ANSI256][ANSI256]

Based on the VGA color palette, there are models for [4-bit, 16 color codes][ANSI16]
and [8-bit, 256 color codes][ANSI256]

### CMYK

- [CMYK][CMYK]

Colormath's CMYK model uses device-independent conversions. Device CMYK profiles are not currently supported.

### HSL

- [HSL][HSL]

A cylindrical representation of sRGB using Hue, Saturation, and Lightness.

### HSV

- [HSV][HSV]

A cylindrical representation of sRGB using Hue, Saturation, and Value / brightness.

### HWB

- [HWB][HWB]

A cylindrical representation of sRGB using Hue, Whiteness, and Blackness.

### HSLuv and HPLuv

- [HSLuv][HSLuv]
- [HPLuv][HPLuv]

HSLuv and HPLuv are color spaces designed as a human friendly alternative to HSL.

## IC<sub>t</sub>C<sub>p</sub>

- [ICtCp][ICtCp]

IC<sub>t</sub>C<sub>p</sub> is a color space designed for high dynamic range and wide color gamut imagery.

## J<sub>z</sub>A<sub>z</sub>B<sub>z</sub> and J<sub>z</sub>C<sub>z</sub>H<sub>z</sub>

- [JzAzBz][JzAzBz]
- [JzCzHz][JzCzHz]

J<sub>z</sub>A<sub>z</sub>B<sub>z</sub> is a perceptually uniform space where euclidean distance predicts perceptual
difference. J<sub>z</sub>C<sub>z</sub>H<sub>z</sub> is its cylindrical representation.

## CIE L\*a\*b\* and LCH<sub>ab</sub>

- [LAB][LAB]
- [LCHab][LCHab]

LAB is a color model intended to be perceptually uniform. Its cylindrical representation is LCH<sub>ab</sub>.

### LAB and LCHab color spaces

- [LABColorSpaces][LABColorSpaces]
- [LCHabColorSpaces][LCHabColorSpaces]

LAB and LCHab models each have multiple color spaces that are defined in terms of a white point. The default white point
is D65.

## CIE L\*u\*v\* and LCH<sub>uv</sub>

- [LUV][LUV]
- [LCHuv][LCHuv]

LUV is a color model intended to be perceptually uniform. Its cylindrical representation is LCH<sub>uv</sub>.

### LUV and LCHuv color spaces

- [LUVColorSpaces][LUVColorSpaces]
- [LCHuvColorSpaces][LCHuvColorSpaces]

LUV and LCHuv models each have multiple color spaces that are defined in terms of a white point. The default white point
is D65.

## Oklab and Oklch
- [Oklab][Oklab]
- [Oklch][Oklch]

Oklab is a perceptual color space for image processing. Its cylindrical representation is Oklch.

## CIE XYZ
- [XYZ][XYZ]

The XYZ color model is common used as a profile connection space when converting between other models.

### XYZ color spaces

The XYZ model has multiple color spaces that are defined in terms of a white point. The default white point
is D65.

# RGB
- [RGB][RGB]

The RGB color model defines colors with the additive combination of reg, green, and blue lights. RGB
components are stored as floating point numbers in the range `[0, 1]`. You can also represent sRGB
colors as packed integers with [RGBInt][RGBInt], or create them from integers in the range `[0, 255]` 
with [RGB.from255()][from255].

## RGB color spaces

Colormath includes a number of built-in RGB color spaces. The default RGB space is sRGB.

- [sRGB][SRGB]
- [Linear sRGB][LINEAR_SRGB]
- [ACES][ACES]
- [ACEScc][ACEScc]
- [ACEScct][ACEScct]
- [ACEScg][ACEScg]
- [Adobe RGB][ADOBE_RGB]
- [BT.2020 / REC.2020][BT_2020]
- [BT.709 / REC.709][BT_709]
- [DCI P3][DCI_P3]
- [Display P3][DISPLAY_P3]
- [ROMM RGB / ProPhoto RGB][ROMM_RGB]


[ACES]:                 api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-a-c-e-s.html
[ACEScc]:               api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-a-c-e-scc.html
[ACEScct]:              api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-a-c-e-scct.html
[ACEScg]:               api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-a-c-e-scg.html
[ADOBE_RGB]:            api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-a-d-o-b-e_-r-g-b.html
[ANSI16]:               api/colormath/com.github.ajalt.colormath/-ansi16/index.html
[ANSI256]:              api/colormath/com.github.ajalt.colormath/-ansi256/index.html
[BT_2020]:              api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-b-t_2020.html
[BT_709]:               api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-b-t_709.html
[CMYK]:                 api/colormath/com.github.ajalt.colormath/-c-m-y-k/index.html
[DCI_P3]:               api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-d-c-i_-p3.html
[DISPLAY_P3]:           api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-d-i-s-p-l-a-y_-p3.html
[from255]:              api/colormath/com.github.ajalt.colormath/-r-g-b-color-space/from255.html
[HPLuv]:                api/colormath/com.github.ajalt.colormath/-h-p-luv/index.html
[HSL]:                  api/colormath/com.github.ajalt.colormath/-h-s-l/index.html
[HSLuv]:                api/colormath/com.github.ajalt.colormath/-h-s-luv/index.html
[HSV]:                  api/colormath/com.github.ajalt.colormath/-h-s-v/index.html
[HWB]:                  api/colormath/com.github.ajalt.colormath/-h-w-b/index.html
[ICtCp]:                api/colormath/com.github.ajalt.colormath/-i-ct-cp/index.html
[JzAzBz]:               api/colormath/com.github.ajalt.colormath/-jz-az-bz/index.html
[JzCzHz]:               api/colormath/com.github.ajalt.colormath/-jz-cz-hz/index.html
[LABColorSpaces]:       api/colormath/com.github.ajalt.colormath/-l-a-b-color-spaces/index.html
[LAB]:                  api/colormath/com.github.ajalt.colormath/-l-a-b/index.html
[LCHabColorSpaces]:     api/colormath/com.github.ajalt.colormath/-l-c-hab-color-spaces/index.html
[LCHab]:                api/colormath/com.github.ajalt.colormath/-l-c-hab/index.html
[LCHuvColorSpaces]:     api/colormath/com.github.ajalt.colormath/-l-c-huv-color-spaces/index.html
[LCHuv]:                api/colormath/com.github.ajalt.colormath/-l-c-huv/index.html
[LINEAR_SRGB]:          api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-l-i-n-e-a-r_-s-r-g-b.html
[LUVColorSpaces]:       api/colormath/com.github.ajalt.colormath/-l-u-v-color-spaces/index.html
[LUV]:                  api/colormath/com.github.ajalt.colormath/-l-u-v/index.html
[Oklab]:                api/colormath/com.github.ajalt.colormath/-oklab/index.html
[Oklch]:                api/colormath/com.github.ajalt.colormath/-oklch/index.html
[RGBInt]:               api/colormath/com.github.ajalt.colormath/-r-g-b-int/index.html
[RGB]:                  api/colormath/com.github.ajalt.colormath/-r-g-b/index.html
[ROMM_RGB]:             api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-r-o-m-m_-r-g-b.html
[SRGB]:                 api/colormath/com.github.ajalt.colormath/-r-g-b-color-spaces/-s-r-g-b.html
[XYZColorSpaces]:       api/colormath/com.github.ajalt.colormath/-x-y-z-color-spaces/index.html
[XYZ]:                  api/colormath/com.github.ajalt.colormath/-x-y-z/index.html
[xyY]:                  api/colormath/com.github.ajalt.colormath/xy-y/index.html
