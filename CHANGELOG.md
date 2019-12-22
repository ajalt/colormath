# Changelog

## [Unreleased]
### Added 
- `toHex()` now has a `renderAlpha` parameter that lets you render the color's alpha channel in the hex. By default the alpha will be added if it's < 1.  

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
