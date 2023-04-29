# Changelog

## 3.3.0
### Added
- The core module now publishes multiplatform targets for `iosSimulatorArm64`, `tvosSimulatorArm64`, and `watchosSimulatorArm64`. 
- The Jetpack Compose extensions module `colormath-ext-jetpack-compose` now publishes multiplatform targets for JVM and iOS in addition to the existing android target.

### Changed
- Updated Kotlin to 1.8

## 3.2.1
### Changed
- Updated Kotlin to 1.7.10

## 3.2.0
### Added
- `hueOr` extension to colors like `HSV` that returns the color's hue or a fallback value if the hue is undefined.

### Changed
- Updated Kotlin to 1.6.0

## 3.1.1
### Fixed
- Fix shared immutability for background threads on Kotlin Native

## 3.1.0
### Added 
- Optional modules with extensions for converting between Colormath colors and other platform representations.

### Changed
- Unspecified alpha values now default to fully opaque

## 3.0.0
### Added
- New color models: `Oklab`, `Oklch`, `HWB`, `HPLuv`, `HSLuv`, `LCHab`, `LCHuv`, `JzAzBz`, `JzCzHz`, `ICtCp`
- New RGB color spaces: `Linear sRGB`, `ACES`, `ACEScc`, `ACEScct`, `ACEScg`, `Adobe RGB`, `BT.2020`, `BT.709`, `DCI P3`, `Display P3`, `ProPhoto`
- Other color spaces: `LABColorSpace`, `LCHabColorSpace`, `LCHuvColorSpace`, `LUVColorSpace`, `XYZColorSpace`
- WCAG contrast: `wcagLuminance`, `wcagContrastRatio`, `mostContrasting`, `firstWithContrast`
- Color difference: `euclideanDistance`, `differenceCIE76`, `differenceCIE94`, `differenceCIE2000`, `differenceCMC`, `differenceEz`
- Transforms: `Color.map`, `mix`, `multiplyAlpha`, `divideAlpha`, `createChromaticAdapter`, `RGBColorSpace.converterTo`
- Interpolation: `Color.interpolate`, `ColorSpace.interpolator`
- Color metadata: `ColorSpace`, `Color.space`, `Color.toArray`, `ColorSpace.create`
- CSS parsing and rendering now support all CSS color strings
- `RGBInt`: an inline class that stores `RGB` colors packed in a single Int. Create instances directly, or convert to it with `RGB.toRGBInt()`
- Publish `macosArm64` target.

### Changed
- All `Color` classes now store their color components as `Float`
- `RGB`, `XYZ`, HSV`, `HSL`, and `HWB` now store their rectangular components normalized to `[0, 1]`.
- Renamed `Color.fromCss` to `Color.parse`.
- All `Color` constructors now use `alpha` as name of their final parameter.
- Replace `toCssRgb` and `toCssHsl` with `fromatCssString` that supports all color models.
- All color models moved from the package `com.github.ajalt.colormath` to `com.github.ajalt.colormath.model`
- Updated Kotlin to 1.5.30

### Removed
- Removed the previously deprecated `ConvertibleColor` typealias.
- Removed the `CssColors` object. Use `Color.fromCss` instead. 
- Removed `Ansi16` companion object color constants.
- Removed `Color.toHex()`. Use `RGB.toHex()` instead.

## 2.1.0
### Added
- Added CIE LUV colorspace support
- Added CIE LCH(uv) colorspace support
- Added HWB colorspace support
- Added `lab()` and `hwb()` support to `Color.fromCss`
- JS target now publishes IR format in addition to legacy jars

### Changed
- Updated Kotlin to 1.5.0
- `Color.fromCss` now clamps out-of-range values in accordance with the CSS Color Module Level 4 spec


## 2.0.0
### Added
- Added multiplatform support.

### Changed
- Moved maven coordinates to `com.github.ajalt.colormath:colormath:2.0.0`.

### Deprecated
- Renamed `ConvertibleColor` to `Color`. The old name is left as a deprecated type alias.

## 1.4.1
### Fixed
- Fix `RGB(255,255, 255).toLAB()` throwing an exception due to rounding precision.

## 1.4.0
### Added
- `hueAsRad()`, `hueAsGrad()`, and `hueAsTurns()` extensions for `HSL` and `HSV` classes.
- `toHex()` now has a `renderAlpha` parameter that lets you render the color's alpha channel in the hex. By default the alpha will be added if it's < 1.
- `toCssRgb()` and `toCssHsl()` extensions to render colors as CSS color functions
- `RGB.toPackedInt()` to convert a color to a single integer.

### Changed
- `withNumberSign` parameter of `toHex` now defaults to `true`
- `ColorMath.parseCssColor()` renamed to `ConvertableColor.fromCss()` and `ColorMath.cssKeywordColors` to `CssColors.colorsByName`


## 1.3.0
### Added
- All colors now have an `alpha` channel, defaulting to 1 (fully opaque). For colorspaces that support transparency, you can pass an alpha value into their constructors.
- `ColorMath.parseCssColor()` can parse all valid HTML/CSS colors, including rgb, hsl, and named colors.
- `ColorMath.cssKeywordColors` is a map of HTML/CSS color names to RGB the colors they represent.

### Changed
- Switch rounding methods to use `kotlin.math` rounding, which can slightly change conversion results in some cases.


## 1.2.0
### Added
- Add ability to construct RGB instances from packed integers or Bytes
