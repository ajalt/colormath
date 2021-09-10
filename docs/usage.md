# Usage

## Creating colors

Instances of a color are constructed by invoking their color model.

```kotlin
LAB(50, 75, 100)
```

For models with multiple color spaces, you can also invoke the specific color space, or create your own:

```kotlin
LAB50(50, 75, 100) // Uses the D50 illuminant
val LAB55 = LabColorSpace(Illuminant.D55)
LAB55(10, 20, 30)
```

You can optionally specify an alpha value.

```kotlin
LAB(l=0, a=0, b=0, alpha=0.5)
```

If you don't specify an alpha value, it will default to `NaN`. This makes it possible to distinguish
unspecified alpha values, which can be important for operations like interpolation. 

sRGB colors can also be constructed from hex strings or integers. All of the following are equivalent:

```kotlin
RGB(0.2, 0.4, 0.6)
SRGB(0.2, 0.4, 0.6)
RGB.from255(51, 102, 153)
RGB("#369")
RGB("#336699")
RGBInt(0x336699u).toSRGB()
```

You can find the full list of built-in color spaces [here][colorspaces].

## Converting colors

You can convert a color to another color space with any of the [Color.to*()][Color] functions:

```kotlin
RGB("#111").toHSL()
XYZ(.1, .2, .3).toOklab()
```

You can also convert to a specific color space with [convertTo]:

```kotlin
RGB("#111").convertTo(LinearSRGB)
RGB("#222").convertTo(LAB)
RGB("#333").convertTo(LAB50)
```

If you need to convert multiple colors from one RGB color space to another, you can use an
[RGBToRGBConverter], which is more efficient than using `convertTo` multiple times:

```kotlin
val srgbColors: List<RGB> = listOf(/*...*/)
val converter = SRGB.converterTo(ACES)
val acesColors = srgbColors.map { converter.convert(it) } 
```

When converting to polar spaces like `HSL`, the hue is undefined for grayscale colors. When that's the case,
the hue value will be `NaN`.

## Color transforms

You can create generic transforms for colors with [Color.map][Color.map]. Colormath includes several
built-in transforms.

### Mix

Mix two colors based on a fraction of each, with a syntax that's similar to the CSS [color-mix][color-mix] function.

```kotlin
val purple = LCHab(29, 66, 327)
val plum = LCHab(73, 37, 324)
val mixed = LCHab.mix(purple, .8, plum, .3)
```

!!! note
    If the amount of the two colors adds up to less than one, the resulting mix will be partially transparent. 

### Interpolate

You can also interpolate between two colors. This is similar to `mix`, but takes a single parameter
`t` indicating the amount to interpolate between the two colors, with `t=0` returning the first
color, and `t=1` returning the second.

```kotlin
val color1 = RGB("#000")
val color2 = RGB("#444")
color1.interpolate(color2, t=0.25)
// RGB("#111")
```

### Premultiply alpha

Colormath colors aren't normally stored with alpha premultiplied. You can do so with
[multiplyAlpha], and revert the operation with [divideAlpha].

```kotlin
val color = RGB(1, 1, 1, alpha = 0.25)
color.multiplyAlpha()
// RGB(0.25, 0.25, 0.25, alpha=0.25)
```

## Color calculations

### Color gamut

You can check if a color is within the sRGB gamut with [isInSRGBGamut].

```kotlin
XYZ(.5, .7, 1).isInSRGBGamut() // true
ICtCp(0, .5, .5).isInSRGBGamut() // false
```

### Color contrast

You can calculate the relative luminance of a color, or the contrast between two colors according to
the [Web Content Accessibility Guidelines][wcagcontrast].

```kotlin
RGB("#f3a").wcagLuminance() // 0.26529932
RGB("#aaa").wcagContrastRatio(RGB("#fff")) // 2.323123
```

You can also select the most contrasting color from a list of colors, similar to the CSS `color-contrast` function.

```kotlin
val wheat = RGB("#f5deb3")
val tan = RGB("#d2b48c")
val sienna = RGB("#a0522d")
val accent = RGB("#b22222")
wheat.mostContrasting(tan, sienna, accent) // returns accent
```

In addition to [mostContrasting], you can use [firstWithContrast] or [firstWithContrastOrNull],
depending on your use case.

### Color difference

Colormath includes several formulas for computing the relative perceptual difference between two colors.

- [euclideanDistance]
- [differenceCIE76]
- [differenceCIE94]
- [differenceCIE2000]
- [differenceCMC]
- [differenceEz]

## Gradients and Interpolation

For gradients and advanced interpolation, you can use the [interpolator] builder. 

```kotlin
// You can interpolate in any color space.
val interp = Oklab.interpolator { 
    // Color stops can be specified in a different space than the interpolator
    stop(RGB("#00f"))
    stop(RGB("#fff"))
    stop(RGB("#000"))
}

// Get a single color
interp.interpolate(0.25)

// Or a sequence of colors to draw a gradient
for ((x, color) in interp.sequence(canvas.width).withIndex()) {
    canvas.drawRect(x=x, y=0, w=1, h=canvas.height, color)
}
```

### Interpolation method

Interpolators use linear interpolation by default. Colormath also includes an implementation of
monotone spline interpolation, which produces smoother gradients.

```kotlin
LCHab.interpolator { 
    method = InterpolationMethods.monotoneSpline()
    // ...
}
```

### Easing functions

Where the interpolation method changes the path the gradient takes through a color space, an
easing function changes the speed that the path is traversed.

[EasingFunctions] includes all the CSS easing functions, as well as an easing
function to set the midpoint of the gradient between two stops.


```kotlin
LCHab.interpolator {
    // Set the easing function for all components
    easing = EasingFunctions.easeInOut()
    // Override the easing function for a specific component
    componentEasing("h", EasingFunctions.linear())
    
    stop(RGB("#00f")) {
        // Override the easing function between this stop and the next
        easing = EasingFunctions.midpoint(.25)
    }
    stop(RGB("#fff"))
    stop(RGB("#000"))
}
```

### Component adjustment

You can make adjustments to the values of a component prior to interpolation. The CSS standard calls this a "fixup". 

#### Alpha adjustment

By default, if any color stops have an alpha value specified, any other stop with an unspecified
alpha will have their alphas set to 1.

#### Hue adjustment

When interpolating in a cylindrical space like LCH<sub>ab</sub>, there are multiple ways to
interpolate the hue (do you travel clockwise or counterclockwise around the hue circle?). You can
pick a strategy from [HueAdjustments], which contains all the methods defined in the CSS standard. By
default, [HueAdjustments.shorter][shorter] is used.

```kotlin
LCHab.interpolator {
    // set the adjustment for the hue
    componentAdjustment("h", HueAdjustments.longer)
    // disable the default alpha adjustment
    componentAdjustment("alpha") { it }
    
    // ...
}
```

## Chromatic adaptation

When converting between color spaces that use different white points, the color is automatically
adapted using Von Kries' method with the CIECAM02 CAT matrix. If you'd like to perform chromatic
adaptation using a different matrix (such as Bradford's), you can convert the color to XYZ and use
[adaptTo].

```kotlin
val bradfords = floatArrayOf(
     0.8951f,  0.2664f, -0.1614f,
    -0.7502f,  1.7135f,  0.0367f,
     0.0389f, -0.0685f,  1.0296f,
)
// adapt this color to LAB with a D50 whitepoint using bradford's matrix
RGB("#f3a").toXYZ().adaptTo(XYZ50, bradfords).toLAB()
// LAB50(l=59.029217, a=79.97541, b=-14.047905)
```

If you want to adapt multiple colors at once based on a source white color, you can use
[createChromaticAdapter].

```kotlin
val sourceWhite : Color = bitmap.getWhitestPixel()
val adapter = RGBInt.createChromaticAdapter(sourceWhite)
val pixels : IntArray = bitmap.getArgbPixels()
adapter.adaptAll(pixels)
bitmap.setPixels(pixels)
```

## Parsing color strings

You can create a `Color` instance from any CSS color string using [parse] and [parseOrNull].

```kotlin
Color.parse("red") // RGB(r=1.0, g=0.0, b=0.0)
Color.parse("rgb(51 102 51 / 40%)") // RGB(r=0.2, g=0.4, b=0.2, alpha=0.4)
Color.parse("hwb(200grad 20% 45%)") // HWB(h=180.0, w=0.2, b=0.45)
```

## Rendering colors as strings

You can also render any `Color` as a CSS color string with [formatCssString]. Colormath
supports more color spaces than CSS, so formatting `formatCssString` will produce a `color()` style
string with a dashed identifier name based on the color space. 

You can also use [formatCssStringOrNull] which will return null when called on a color space that
isn't built in to CSS.

To render a color as a hex string, convert it to sRGB and use [toHex].

```kotlin
RGB(.2, 0, 1, alpha = .5).formatCssString() // "rgb(51 0 255 / 0.5)"
LCHab50(50, 10, 180).formatCssString() // "lch(50% 10 180)"
ROMM_RGB(.1, .2, .4).formatCssString() // "color(prophoto-rgb 0.1 0.2 0.4)"
RGB(.2, .4, .6).toHex() // "#336699"
```

!!! caution
    The CSS `lab`, `lch`, and `xyz` functions specify colors with the [D50] illuminant. Colormath's
    default constructors for those color spaces use [D65], so they will be subject to chromatic
    adaptation before rendering. To avoid this, use the D50 versions of the constructors: [LAB50],
    [LCHab50], and [XYZ50]

[Color.map]:                api/colormath/com.github.ajalt.colormath.transform/map.html
[Color]:                    api/colormath/com.github.ajalt.colormath/-color/index.html
[D50]:                      api/colormath/com.github.ajalt.colormath/-illuminant/-d50.html
[D65]:                      api/colormath/com.github.ajalt.colormath/-illuminant/-d65.html
[EasingFunctions]:          api/colormath/com.github.ajalt.colormath.transform/-easing-functions/index.html
[HueAdjustments]:           api/colormath/com.github.ajalt.colormath.transform/-hue-adjustments/index.html
[LAB50]:                    api/colormath/com.github.ajalt.colormath.model/-l-a-b-color-spaces/-l-a-b50.html
[LCHab50]:                  api/colormath/com.github.ajalt.colormath.model/-l-c-hab-color-spaces/-l-c-hab50.html
[RGBToRGBConverter]:        api/colormath/com.github.ajalt.colormath.transform/-r-g-b-to-r-g-b-converter/index.html
[XYZ50]:                    api/colormath/com.github.ajalt.colormath.model/-x-y-z-color-spaces/-x-y-z50.html
[adaptTo]:                  api/colormath/com.github.ajalt.colormath.model/-x-y-z/adapt-to.html
[color-mix]:                https://www.w3.org/TR/css-color-5/#color-mix
[colorspaces]:              colorspaces.md
[convertTo]:                api/colormath/com.github.ajalt.colormath/convert-to.html
[createChromaticAdapter]:   api/colormath/com.github.ajalt.colormath.transform/create-chromatic-adapter.html
[differenceCIE2000]:        api/colormath/com.github.ajalt.colormath.calculate/difference-c-i-e2000.html
[differenceCIE76]:          api/colormath/com.github.ajalt.colormath.calculate/difference-c-i-e76.html
[differenceCIE94]:          api/colormath/com.github.ajalt.colormath.calculate/difference-c-i-e94.html
[differenceCMC]:            api/colormath/com.github.ajalt.colormath.calculate/difference-c-m-c.html
[differenceEz]:             api/colormath/com.github.ajalt.colormath.calculate/difference-ez.html
[divideAlpha]:              api/colormath/com.github.ajalt.colormath.transform/divide-alpha.html
[euclideanDistance]:        api/colormath/com.github.ajalt.colormath.calculate/euclidean-distance.html
[firstWithContrastOrNull]:  api/colormath/com.github.ajalt.colormath.calculate/first-with-contrast-or-null.html
[firstWithContrast]:        api/colormath/com.github.ajalt.colormath.calculate/first-with-contrast.html
[formatCssStringOrNull]:    api/colormath/com.github.ajalt.colormath/format-css-string-or-null.html
[formatCssString]:          api/colormath/com.github.ajalt.colormath/format-css-string.html
[interpolator]:             api/colormath/com.github.ajalt.colormath.transform/interpolator.html
[isInSRGBGamut]:            api/colormath/com.github.ajalt.colormath.calculate/is-in-s-r-g-b-gamut.html
[mostContrasting]:          api/colormath/com.github.ajalt.colormath.calculate/most-contrasting.html
[multiplyAlpha]:            api/colormath/com.github.ajalt.colormath.transform/multiply-alpha.html
[parseOrNull]:              api/colormath/com.github.ajalt.colormath/parse-or-null.html
[parse]:                    api/colormath/com.github.ajalt.colormath/parse.html
[shorter]:                  api/colormath/com.github.ajalt.colormath.transform/-hue-adjustments/shorter.html
[toHex]:                    api/colormath/com.github.ajalt.colormath.model/-r-g-b/to-hex.html
[wcagcontrast]:             https://www.w3.org/TR/WCAG21/#dfn-contrast-ratio
