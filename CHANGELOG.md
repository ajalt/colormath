# Changelog

## Unreleased

### Added
- `RGBInt`, an inline class that stores `RGB` colors in a single Int. Create instances directly, or convert to it with `RGB.toRGBInt()`

### Changed

- All `Color` classes now store their colors channels as `Float`
- All `Color` classes now support HDR colors. Constructors no longer require that color channel values fall in SDR ranges.
- `RGB` now stores its color channels normalized to `[0, 1]` rather than `[0, 255]`. You can read the color channels in their old integer representations with `RGB.redInt`, `greenInt`, and `blueInt`.
- `HSV`, `HSL`, and `HWB` now store their rectangular channels (`s`, `v`, `l`, `w`, and `b`) normalized to `[0, 1]` rather than `[0, 100]`.
- `XYZ` channels are no longer multiplied by 100: their range is now `[-2, 2]` rather than `[-200, 200]`

## [2.1.0] - 2021-05-05
### Added
- Added CIE LUV colorspace support
- Added CIE LCH(uv) colorspace support
- Added HWB colorspace support
- Added `lab()` and `hwb()` support to `Color.fromCss`
- JS target now publishes IR format in addition to legacy jars

### Changed
- Updated Kotlin to 1.5.0
- `Color.fromCss` now clamps out-of-range values in accordance with the CSS Color Module Level 4 spec


## [2.0.0] - 2020-09-12
### Added
- Added multiplatform support.

### Changed
- Moved maven coordinates to `com.github.ajalt.colormath:colormath:2.0.0`.

### Deprecated
- Renamed `ConvertibleColor` to `Color`. The old name is left as a deprecated type alias.

## [1.4.1] - 2020-05-12
### Fixed
- Fix `RGB(255,255, 255).toLAB()` throwing an exception due to rounding precision.

## [1.4.0] - 2019-12-22
### Added
- `hueAsRad()`, `hueAsGrad()`, and `hueAsTurns()` extensions for `HSL` and `HSV` classes.
- `toHex()` now has a `renderAlpha` parameter that lets you render the color's alpha channel in the hex. By default the alpha will be added if it's < 1.
- `toCssRgb()` and `toCssHsl()` extensions to render colors as CSS color functions
- `RGB.toPackedInt()` to convert a color to a single integer.

### Changed
- `withNumberSign` parameter of `toHex` now defaults to `true`
- `ColorMath.parseCssColor()` renamed to `ConvertableColor.fromCss()` and `ColorMath.cssKeywordColors` to `CssColors.colorsByName`


## [1.3.0] - 2019-12-19
### Added
- All colors now have an `alpha` channel, defaulting to 1 (fully opaque). For colorspaces that support transparency, you can pass an alpha value into their constructors.
- `ColorMath.parseCssColor()` can parse all valid HTML/CSS colors, including rgb, hsl, and named colors.
- `ColorMath.cssKeywordColors` is a map of HTML/CSS color names to RGB the colors they represent.

### Changed
- Switch rounding methods to use `kotlin.math` rounding, which can slightly change conversion results in some cases.


## [1.2.0] - 2018-08-19
### Added
- Add ability to construct RGB instances from packed integers or Bytes
