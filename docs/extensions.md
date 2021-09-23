# Colormath extensions

Colormath provides extensions for converting to and from other platform's color representations.
Each set of extensions is published as a separate maven package.

## Android ColorInt

```kotlin
dependencies {
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-android-colorint:$colormathVersion")
}
```

[API docs][colorint]

These extensions convert between Android's packed ARGB integers, which are commonly annotated with `@ColorInt`.

This package supports Android API 16+.

```kotlin
val redPercent = RGBInt.fromColorInt(textView.currentTextColor).redFloat
val textColor = RGB.fromColorInt(textView.currentTextColor)
textView.highlightColor = textColor.toColorInt()
```

## Android Color objects

```kotlin
dependencies {
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-android-color:$colormathVersion")
}
```

[API docs][android-color]

These extensions convert between the color objects introduced in Android 26.

This package supports Android API 26+.

```kotlin
import android.graphics.ColorSpace
import android.graphics.Color as AndroidColor

val c: AndroidColor = RGB("#f0f").toAndroidColor()
val rgb: RGB = c.toColormathSRGB()
val lab = AndroidColor.valueOf(0f, 1f, 0f, 1f, ColorSpace.get(ColorSpace.Named.CIE_LAB)).toColormathColor()
```

## Jetpack Compose Color objects

```kotlin
dependencies {
    implementation("com.github.ajalt.colormath.extensions:colormath-ext-jetpack-compose:$colormathVersion")
}
```

[API docs][jetpack-compose]


These extensions convert between the color objects used in `androidx.compose`.

This package supports Android API 21+.

[android-color]:    api/colormath-ext-android-color/colormath-ext-android-color/com.github.ajalt.colormath.extensions.android.color/index.html
[colorint]:         api/colormath-ext-android-colorint/colormath-ext-android-colorint/com.github.ajalt.colormath.extensions.android.colorint/index.html
[jetpack-compose]:  api/colormath-ext-jetpack-compose/colormath-ext-jetpack-compose/com.github.ajalt.colormath.extensions.android.composecolor/index.html
