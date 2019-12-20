# Changelog

## [Unreleased]
### Added
- All colors now have an `alpha` channel, defaulting to 1 (fully opaque). For colorspaces that support transparency, you can pass an alpha value into their constructors.
- `ColorMath.parseCssColor()` can parse all valid HTML/CSS colors, including rgb, hsl, and named colors. 
- `ColorMath.cssKeywordColors` is a map of HTML/CSS color names to RGB the colors they represent.

### Changed
- Switch rounding methods to use `kotlin.math` rounding, which can slightly change conversion results in some cases.
