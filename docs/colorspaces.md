# Color Spaces

Colormath has a number of built-in color spaces and models.

!!! info "A note on terminology"
    A *color model* defines the components of a color. For example, RGB is a color model that
    defines colors with the additive combination of red, green, and blue light.

    A *color space* is a color model plus the details necessary to represent a color in that model. sRGB
    and DCI P3 are both color spaces the use the RGB model, but they define different values for the exact
    colors of their RGB primaries.

## Built-in color models

## RGB

- [RGB][RGB]

| Component | Description | Range    |
|-----------|-------------|----------|
| r         | red         | `[0, 1]` |
| g         | green       | `[0, 1]` |
| b         | blue        | `[0, 1]` |

The RGB color model defines colors with the additive combination of reg, green, and blue lights. RGB
components are stored as floating point numbers in the range `[0, 1]`. You can also represent sRGB
colors as packed integers with [RGBInt][RGBInt], or create them from integers in the range `[0, 255]`
with [RGB.from255()][from255].

### RGB color spaces

Colormath includes a number of built-in RGB color spaces, and you can define your own with
[RGBColorSpace]. The default RGB space is sRGB.

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

## HSL

- [HSL][HSL]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
| s         | saturation                                | `[0, 1]`   |
| l         | lightness                                 | `[0, 1]`   |

A cylindrical representation of sRGB using Hue, Saturation, and Lightness.

## HSV

- [HSV][HSV]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
| s         | saturation                                | `[0, 1]`   |
| v         | value                                     | `[0, 1]`   |

A cylindrical representation of sRGB using Hue, Saturation, and Value / brightness.

## HWB

- [HWB][HWB]

| Component | Description  | Range      |
|-----------|--------------|------------|
| h         | hue, degrees | `[0, 360)` |
| w         | whiteness    | `[0, 1]`   |
| b         | blackness    | `[0, 1]`   |

A cylindrical representation of sRGB using Hue, Whiteness, and Blackness.

## HSLuv and HPLuv

- [HSLuv][HSLuv]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
| s         | saturation                                | `[0, 100]` |
| l         | lightness                                 | `[0, 100]` |

- [HPLuv][HPLuv]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |
| p         | percentage saturation                     | `[0, 100]` |
| l         | lightness                                 | `[0, 100]` |

HSLuv and HPLuv are color spaces designed as a human friendly alternative to HSL.

## IC<sub>t</sub>C<sub>p</sub>

- [ICtCp][ICtCp]

| Component     | Description          | Range         |
|---------------|----------------------|---------------|
| I             | intensity            | `[0, 1]`      |
| C<sub>t</sub> | Tritan (blue-yellow) | `[-0.5, 0.5]` |
| C<sub>p</sub> | Protan (red-green)   | `[-0.5, 0.5]` |
  
IC<sub>t</sub>C<sub>p</sub> is a color space designed for high dynamic range and wide color gamut imagery.

## J<sub>z</sub>A<sub>z</sub>B<sub>z</sub> and J<sub>z</sub>C<sub>z</sub>H<sub>z</sub>

- [JzAzBz][JzAzBz]

| Component     | Description | Range     |
|---------------|-------------|-----------|
| J<sub>z</sub> | lightness   | `[0, 1]`  |
| A<sub>z</sub> | green-red   | `[-1, 1]` |
| B<sub>z</sub> | blue-yellow | `[-1, 1]` |

- [JzCzHz][JzCzHz]

| Component     | Description                               | Range      |
|---------------|-------------------------------------------|------------|
| J<sub>z</sub> | lightness                                 | `[0, 1]`   |
| C<sub>z</sub> | chroma                                    | `[-1, 1]`  |
| H<sub>z</sub> | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |

J<sub>z</sub>A<sub>z</sub>B<sub>z</sub> is a perceptually uniform space where euclidean distance
predicts perceptual difference. J<sub>z</sub>C<sub>z</sub>H<sub>z</sub> is its cylindrical
representation.

## CIE L\*a\*b\* and LCH<sub>ab</sub>

- [LAB][LAB]

| Component | Description | Range         |
|-----------|-------------|---------------|
| L         | lightness   | `[0, 100]`    |
| a*        | green-red   | `[-125, 125]` |
| b*        | blue-yellow | `[-125, 125]` |

- [LCHab][LCHab]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| L         | lightness                                 | `[0, 100]` |
| c         | chroma                                    | `[0, 150]` |
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |

LAB is a color model intended to be perceptually uniform. Its cylindrical representation is LCH<sub>ab</sub>.

### LAB and LCHab color spaces

- [LABColorSpaces][LABColorSpaces]
- [LCHabColorSpaces][LCHabColorSpaces]

LAB and LCHab models each have multiple color spaces that are defined relative to a white point. The
default white point is D65.

## CIE L\*u\*v\* and LCH<sub>uv</sub>

- [LUV][LUV]

| Component  | Description  | Range         |
| ---------- | ------------ | ------------- |
| L          | lightness    | `[0, 100]`    |
| u          |              | `[-100, 100]` |
| v          |              | `[-100, 100]` |

- [LCHuv][LCHuv]

| Component  | Description                               | Range      |
| ---------- | ----------------------------------------- | ---------- |
| L          | lightness                                 | `[0, 100]` |
| c          | chroma                                    | `[0, 100]` |
| h          | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |

LUV is a color model intended to be perceptually uniform. Its cylindrical representation is LCH<sub>uv</sub>.

### LUV and LCHuv color spaces

- [LUVColorSpaces][LUVColorSpaces]
- [LCHuvColorSpaces][LCHuvColorSpaces]

LUV and LCHuv models each have multiple color spaces that are defined relative to a white point. The
default white point is D65.

## Oklab and Oklch

- [Oklab][Oklab]

| Component | Description | Range         |
|-----------|-------------|---------------|
| L         | lightness   | `[0, 1]`      |
| a         | green-red   | `[-0.4, 0.4]` |
| b         | blue-yellow | `[-0.4, 0.4]` |


- [Oklch][Oklch]

| Component | Description                               | Range      |
|-----------|-------------------------------------------|------------|
| L         | lightness                                 | `[0, 1]`   |
| c         | chroma                                    | `[0, 0.4]` |
| h         | hue, degrees, `NaN` for monochrome colors | `[0, 360)` |

Oklab is a perceptual color space for image processing. Its cylindrical representation is Oklch.

## CIE XYZ

- [XYZ][XYZ]

| Component | Range    |
|-----------|----------|
| X         | `[0, 1]` |
| Y         | `[0, 1]` |
| Z         | `[0, 1]` |

The XYZ color model is common used as a profile connection space when converting between other models.

### XYZ color spaces

- [XYZColorSpaces]

The XYZ model has multiple color spaces that are defined relative to a white point. The default
white point is D65.


## ANSI color codes

- [ANSI16][ANSI16]
- [ANSI256][ANSI256]

Based on the VGA color palette, there are models for [4-bit, 16 color codes][ANSI16]
and [8-bit, 256 color codes][ANSI256]

## CMYK

- [CMYK][CMYK]

| Component | Description | Range    |
|-----------|-------------|----------|
| c         | cyan        | `[0, 1]` |
| m         | magenta     | `[0, 1]` |
| y         | yellow      | `[0, 1]` |
| k         | key / black | `[0, 1]` |

Colormath's CMYK model uses device-independent conversions. Device CMYK profiles are not currently
supported.


[ACES]:                 api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-a-c-e-s.html
[ACEScc]:               api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-a-c-e-scc.html
[ACEScct]:              api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-a-c-e-scct.html
[ACEScg]:               api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-a-c-e-scg.html
[ADOBE_RGB]:            api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-adobe-r-g-b.html
[ANSI16]:               api/colormath/com.github.ajalt.colormath.model/-ansi16/index.html
[ANSI256]:              api/colormath/com.github.ajalt.colormath.model/-ansi256/index.html
[BT_2020]:              api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-b-t2020.html
[BT_709]:               api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-b-t709.html
[CMYK]:                 api/colormath/com.github.ajalt.colormath.model/-c-m-y-k/index.html
[DCI_P3]:               api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-d-c-i_-p3.html
[DISPLAY_P3]:           api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-display-p3.html
[HPLuv]:                api/colormath/com.github.ajalt.colormath.model/-h-p-luv/index.html
[HSL]:                  api/colormath/com.github.ajalt.colormath.model/-h-s-l/index.html
[HSLuv]:                api/colormath/com.github.ajalt.colormath.model/-h-s-luv/index.html
[HSV]:                  api/colormath/com.github.ajalt.colormath.model/-h-s-v/index.html
[HWB]:                  api/colormath/com.github.ajalt.colormath.model/-h-w-b/index.html
[ICtCp]:                api/colormath/com.github.ajalt.colormath.model/-i-ct-cp/index.html
[JzAzBz]:               api/colormath/com.github.ajalt.colormath.model/-jz-az-bz/index.html
[JzCzHz]:               api/colormath/com.github.ajalt.colormath.model/-jz-cz-hz/index.html
[LABColorSpaces]:       api/colormath/com.github.ajalt.colormath.model/-l-a-b-color-spaces/index.html
[LAB]:                  api/colormath/com.github.ajalt.colormath.model/-l-a-b/index.html
[LCHabColorSpaces]:     api/colormath/com.github.ajalt.colormath.model/-l-c-hab-color-spaces/index.html
[LCHab]:                api/colormath/com.github.ajalt.colormath.model/-l-c-hab/index.html
[LCHuvColorSpaces]:     api/colormath/com.github.ajalt.colormath.model/-l-c-huv-color-spaces/index.html
[LCHuv]:                api/colormath/com.github.ajalt.colormath.model/-l-c-huv/index.html
[LINEAR_SRGB]:          api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-linear-s-r-g-b.html
[LUVColorSpaces]:       api/colormath/com.github.ajalt.colormath.model/-l-u-v-color-spaces/index.html
[LUV]:                  api/colormath/com.github.ajalt.colormath.model/-l-u-v/index.html
[Oklab]:                api/colormath/com.github.ajalt.colormath.model/-oklab/index.html
[Oklch]:                api/colormath/com.github.ajalt.colormath.model/-oklch/index.html
[RGBColorSpace]:        api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-space.html
[RGBInt]:               api/colormath/com.github.ajalt.colormath.model/-r-g-b-int/index.html
[RGB]:                  api/colormath/com.github.ajalt.colormath.model/-r-g-b/index.html
[ROMM_RGB]:             api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-r-o-m-m_-r-g-b.html
[SRGB]:                 api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-spaces/-s-r-g-b.html
[XYZColorSpaces]:       api/colormath/com.github.ajalt.colormath.model/-x-y-z-color-spaces/index.html
[XYZ]:                  api/colormath/com.github.ajalt.colormath.model/-x-y-z/index.html
[from255]:              api/colormath/com.github.ajalt.colormath.model/-r-g-b-color-space/from255.html
[xyY]:                  api/colormath/com.github.ajalt.colormath.model/xy-y/index.html
