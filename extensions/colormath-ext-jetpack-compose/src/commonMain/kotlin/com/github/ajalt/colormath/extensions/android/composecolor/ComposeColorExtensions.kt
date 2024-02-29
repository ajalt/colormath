package com.github.ajalt.colormath.extensions.android.composecolor

import androidx.compose.ui.graphics.colorspace.ColorSpaces
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.LABColorSpaces.LAB50
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ACES
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.BT709
import com.github.ajalt.colormath.model.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.model.RGBInt
import com.github.ajalt.colormath.model.SRGB
import com.github.ajalt.colormath.model.XYZColorSpaces.XYZ50
import androidx.compose.ui.graphics.Color as ComposeColor


/**
 * Convert this color to a Colormath [Color] instance.
 */
fun ComposeColor.toColormathColor(): Color {
    return when (colorSpace) {
        ColorSpaces.Srgb -> SRGB(red, green, blue, alpha)
        ColorSpaces.Aces -> ACES(red, green, blue, alpha)
        ColorSpaces.Acescg -> ACEScg(red, green, blue, alpha)
        ColorSpaces.AdobeRgb -> AdobeRGB(red, green, blue, alpha)
        ColorSpaces.Bt2020 -> BT2020(red, green, blue, alpha)
        ColorSpaces.Bt709 -> BT709(red, green, blue, alpha)
        ColorSpaces.CieLab -> LAB50(red, green, blue, alpha)
        ColorSpaces.CieXyz -> XYZ50(red, green, blue, alpha)
        ColorSpaces.DciP3 -> DCI_P3(red, green, blue, alpha)
        ColorSpaces.DisplayP3 -> DisplayP3(red, green, blue, alpha)
        ColorSpaces.LinearSrgb -> LinearSRGB(red, green, blue, alpha)
        ColorSpaces.ProPhotoRgb -> ROMM_RGB(red, green, blue, alpha)
        else -> convert(ColorSpaces.Srgb).let { SRGB(it.red, it.green, it.blue, it.alpha) }
    }
}

/**
 * Convert this color to a Colormath [SRGB] instance.
 */
fun ComposeColor.toColormathSRGB(): RGB {
    return convert(ColorSpaces.Srgb).let { SRGB(it.red, it.green, it.blue, it.alpha) }
}

/**
 * Convert this color to a Jetpack Compose [Color][androidx.compose.ui.graphics.Color] instance.
 */
fun Color.toComposeColor(): ComposeColor {
    if (this is RGBInt) return ComposeColor(argb.toInt())
    val s = when {
        space == SRGB -> ColorSpaces.Srgb
        space === ACES -> ColorSpaces.Aces
        space === ACEScg -> ColorSpaces.Acescg
        space === AdobeRGB -> ColorSpaces.AdobeRgb
        space === BT2020 -> ColorSpaces.Bt2020
        space === BT709 -> ColorSpaces.Bt709
        space === LAB50 -> ColorSpaces.CieLab
        space === XYZ50 -> ColorSpaces.CieXyz
        space === DCI_P3 -> ColorSpaces.DciP3
        space === DisplayP3 -> ColorSpaces.DisplayP3
        space === LinearSRGB -> ColorSpaces.LinearSrgb
        space === ROMM_RGB -> ColorSpaces.ProPhotoRgb
        else -> null
    }

    return if (s == null) {
        val (r, g, b, a) = toSRGB()
        ComposeColor(r, g, b, a)
    } else {
        val (r, g, b, a) = toArray()
        ComposeColor(r, g, b, a, s)
    }
}
