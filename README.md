# Colormath

Colormath is a Kotlin Multiplatform library for color manipulation and conversion.

Colormath can:

- Convert between color models and spaces
- Manipulate colors with transformations such as mixing and chromatic adaptation
- Calculate attributes such as WCAG contrast and perceptual color difference
- Generate gradients with custom interpolation methods and easing functions
- Parse and render colors as strings, including all representations from the CSS spec

```kotlin
// Create an sRGB color
val color = RGB("#ff23cc")

// Interpolate with another color
val mixed = color.interpolate(RGB(0.1, 0.4, 1), 0.5f)
// RGB("#8c45e6")

// Convert to a different color space
val lab = mixed.toLAB()
// LAB(46.3, 60.9, -70)

// Change the transparency
val labA = lab.copy(alpha = 0.25f)
// LAB(46.3, 60.9, -70, 0.25)

// Adapt white point
val lab50 = labA.convertTo(LAB50)
// LAB50(45, 55.1812, 72.5911, 0.25)

// Render as a css color string
println(lab50.formatCssString())
// "lab(45% 55.1812 -72.5911 / 0.25)"
```

## Documentation

The full documentation can be found on [the website](https://ajalt.github.io/colormath).

There are also some online examples:

- [Color space converter](https://ajalt.github.io/colormath/converter/)
- [Gradient generator](https://ajalt.github.io/colormath/gradient/)

## Installation

Colormath is distributed through [Maven Central](https://search.maven.org/artifact/com.github.ajalt.colormath/colormath/).

```groovy
dependencies {
    implementation("com.github.ajalt.colormath:colormath:3.3.0")
    
    // optional extensions for interop with other platforms
    //
    // android.graphics.Color
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-android-color:3.3.0")
    // androidx.annotation.ColorInt
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-android-colorint:3.3.0")
    // androidx.compose.ui.graphics.Color
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-jetpack-compose:3.3.0")
}
```

###### If you're using Maven instead of Gradle, use `<artifactId>colormath-jvm</artifactId>`

#### Multiplatform

Colormath supports the following targets: `jvm`, `mingwX64`, `linuxX64`, `macosX64`, `ios`,
`watchos`, `tvos`, and `js` (for both NodeJS and Browsers). You'll need to use Gradle 6 or newer.
Artifacts for `macosArm64` are also published, but not tested with CI.

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
