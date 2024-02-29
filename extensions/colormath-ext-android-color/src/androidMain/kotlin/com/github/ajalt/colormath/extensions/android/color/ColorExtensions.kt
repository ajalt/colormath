package com.github.ajalt.colormath.extensions.android.color

import android.graphics.ColorSpace
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
import android.graphics.Color as AndroidColor


/**
 * Convert this color to a Colormath [Color].
 *
 * If this color's space is built in to Colormath, the returned color will be in the space space.
 * Otherwise, this is equivalent to [toColormathColor].
 */
fun AndroidColor.toColormathColor(): Color {
    return when (colorSpace) {
        ColorSpace.get(ColorSpace.Named.SRGB) -> RGB(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.LINEAR_SRGB) -> LinearSRGB(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.BT709) -> BT709(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.BT2020) -> BT2020(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.DCI_P3) -> DCI_P3(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.DISPLAY_P3) -> DisplayP3(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.ADOBE_RGB) -> AdobeRGB(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.PRO_PHOTO_RGB) -> ROMM_RGB(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.ACES) -> ACES(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.ACESCG) -> ACEScg(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.CIE_XYZ) -> XYZ50(red(), green(), blue(), alpha())
        ColorSpace.get(ColorSpace.Named.CIE_LAB) -> LAB50(red(), green(), blue(), alpha())
        else -> toColormathSRGB()
    }
}

/**
 * Convert this color to a Colormath [SRGB] instance.
 */
fun AndroidColor.toColormathSRGB(): RGB {
    return SRGB.create(ColorSpace.connect(colorSpace).transform(components))
}

/**
 * Convert this color to an Android [Color][android.graphics.Color]
 */
fun Color.toAndroidColor(): AndroidColor {
    if (this is RGBInt) return AndroidColor.valueOf(argb.toInt())
    val s = when {
        space == SRGB -> ColorSpace.get(ColorSpace.Named.SRGB)
        space === LinearSRGB -> ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
        space === BT709 -> ColorSpace.get(ColorSpace.Named.BT709)
        space === BT2020 -> ColorSpace.get(ColorSpace.Named.BT2020)
        space === DCI_P3 -> ColorSpace.get(ColorSpace.Named.DCI_P3)
        space === DisplayP3 -> ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
        space === AdobeRGB -> ColorSpace.get(ColorSpace.Named.ADOBE_RGB)
        space === ROMM_RGB -> ColorSpace.get(ColorSpace.Named.PRO_PHOTO_RGB)
        space === ACES -> ColorSpace.get(ColorSpace.Named.ACES)
        space === ACEScg -> ColorSpace.get(ColorSpace.Named.ACESCG)
        space === XYZ50 -> ColorSpace.get(ColorSpace.Named.CIE_XYZ)
        space === LAB50 -> ColorSpace.get(ColorSpace.Named.CIE_LAB)
        else -> null
    }

    return if (s == null) {
        val (r, g, b, a) = toSRGB()
        AndroidColor.valueOf(r, g, b, a)
    } else {
        AndroidColor.valueOf(toArray(), s)
    }
}
