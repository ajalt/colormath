# Colormath

Colormath is a library that allows you to convert between a number of color spaces.

## Supported color spaces

* RGB
* CMYK
* HSL
* HSV
* XYZ
* LAB
* ANSI-16 color codes
* ANSI-256 color codes

Most conversions run in a single step. If there isn't a formula for a
direct conversion between two color spaces, the color will first be
converted to another form such as RGB and then to final color space.

## Usage

Each color space is represented with a data class, and contains
`.toXXX()` methods to convert to other spaces.

```kotlin
> RGB("#adcdef").toHSV()
HSV(h=211, s=28, v=94)

> RGB(12, 128, 255).toCMYK()
CMYK(c=95, m=50, y=0, k=0)

> HSL(180, 50, 50).toHex(withNumberSign = true)
"#40bfbf"
```

## API Documentation

API docs are [hosted on JitPack](https://jitpack.io/com/github/ajalt/colormath/1.2.0/javadoc/).

## Installation

Colormath is distributed through Maven Central,
[Jcenter](https://bintray.com/ajalt/maven/colormath) and
[Jitpack](https://jitpack.io/#ajalt/colormath).

```groovy
dependencies {
   compile 'com.github.ajalt:colormath:1.2.0'
}
```

## License

    Copyright 2018 AJ Alt

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
