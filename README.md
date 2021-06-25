# Colormath

Colormath is a Kotlin Multiplatform library that allows you to convert between a number of color
models. Colormath can also parse and render CSS colors.

[Try it online](https://ajalt.github.io/colormath/tryit/)

## Supported color models

* [RGB](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-r-g-b/index.html)
* [CMYK](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-c-m-y-k/index.html)
* [HSL](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-h-s-l/index.html)
* [HSV](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-h-s-v/index.html)
* [HWB](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-h-w-b/index.html)
* [LAB](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-l-a-b/index.html)
* [LCH](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-l-c-h/index.html)
* [LUV](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-l-u-v/index.html)
* [XYZ](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-x-y-z/index.html)
* [ANSI-16 color codes](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-ansi16/index.html)
* [ANSI-256 color codes](https://ajalt.github.io/colormath/api/colormath/com.github.ajalt.colormath/-ansi256/index.html)

## Usage

### Conversion

Each color model is represented with a data class, and contains `.toXXX()` methods to convert to
other models.

All `Color` classes contain an `alpha` channel, which defaults to `1` (fully opaque) for color models
that don't support transparency (such as ANSI color codes).

```kotlin
> RGB("#adcdef").toHSV()
HSV(h=211, s=28, v=94)

> RGB(r=12, g=128, b=255, a=.5f).toCMYK()
CMYK(c=95, m=50, y=0, k=0, a=.5f)

> HSL(180, 50, 50).toHex()
"#40bfbf"
```

### CSS Parsing and rendering

You can parse most colors allowed by the CSS Color Module Levels 1 through 4.

```kotlin
> Color.fromCss("#ff009980")
RGB(r=255, g=0, b=153, a=.5)

> Color.fromCss("rgb(100%, 0%, 60%)")
RGB(r=255, g=0, b=153, a=1)

> Color.fromCss("rgb(1e2, .5e1, .5e0, +.25e2%)")
RGB(r=100, g=5, b=1, a=.25)

> Color.fromCss("hsl(.75turn, 60%, 70%)")
HSL(h=270, s=60, l=70, a=1)

> Color.fromCss("rebeccapurple").toHex()
"#663399"
```

You can also render any color in CSS `rgb` or `hsl` functional or whitespace notation

```kotlin
> RGB(255, 0, 128).toCssRgb()
"rgb(255, 0, 128)"

> RGB(255, 0, 128, .5f).toCssRgb(rgbStyle=PERCENT)
"rgb(100%, 0%, 50%, .5)"

> XYZ(25.0, 50.0, 75.0, .5f).toCssHsl(commas = false, hueUnit = RADIANS)
"hsl(3.1241rad 100% 44% / .5)"
``` 

## Installation

Colormath is distributed through [Maven Central](https://search.maven.org/artifact/com.github.ajalt.colormath/colormath/).

```groovy
dependencies {
   implementation("com.github.ajalt.colormath:colormath:2.1.0")
}
```

###### If you're using Maven instead of Gradle, use `<artifactId>colormath-jvm</artifactId>`

###### In version 2.0, the maven coordinates changed. Make sure you're using the new coordinates if you're updating from an older version.

#### Multiplatform

Colormath supports the following targets: `jvm`, `mingwX64`, `linuxX64`, `macosX64`, `ios`,
`watchos`, `tvos`, and `js` (for both NodeJS and Browsers). You'll need to use Gradle 6 or newer.

#### Snapshots

<details>
<summary>Snapshot builds are also available</summary>

<a href="https://oss.sonatype.org/content/repositories/snapshots/com/github/ajalt/colormath/colormath"><img src="https://img.shields.io/nexus/s/com.github.ajalt.colormath/colormath?color=blue&label=latest%20shapshot&server=https%3A%2F%2Foss.sonatype.org"/></a>

<p>
You'll need to add the Sonatype snapshots repository:

```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
```
</p>
</details>


## License

    Copyright 2018-2021 AJ Alt

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
