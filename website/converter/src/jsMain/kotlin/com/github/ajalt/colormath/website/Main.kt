package com.github.ajalt.colormath.website

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RGBColorSpaces.ACES
import com.github.ajalt.colormath.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.RGBColorSpaces.ACEScct
import com.github.ajalt.colormath.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.RGBColorSpaces.ADOBE_RGB
import com.github.ajalt.colormath.RGBColorSpaces.BT_2020
import com.github.ajalt.colormath.RGBColorSpaces.BT_709
import com.github.ajalt.colormath.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.RGBColorSpaces.DISPLAY_P3
import com.github.ajalt.colormath.RGBColorSpaces.LINEAR_SRGB
import com.github.ajalt.colormath.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.calculate.wcagContrastRatio
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.step
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.className
import org.jetbrains.compose.web.css.selectors.hover
import org.jetbrains.compose.web.css.selectors.plus
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.js.json
import kotlin.math.roundToInt

private val dark = RGB("#000d")
private val light = RGB("#e9ebfc")

private class Row(
    val name: String,
    val space: ColorSpace<*>,
    val step: Double = 0.1,
    val scale: Double = 1.0,
    val round: Boolean = false,
)

private val rows = listOf(
    Row("sRGB (0-255)", SRGB, 1.0, 255.0, round = true),
    Row("sRGB (0-1)", SRGB),
    Row("HSL", HSL),
    Row("HSV", HSV),
    Row("HWB", HWB),
    Row("LAB", LAB, step = 1.0),
    Row("LCh(ab)", LCHab, step = 1.0),
    Row("LUV", LUV, step = 1.0),
    Row("LCh(uv)", LCHuv, step = 1.0),
    Row("Oklab", Oklab),
    Row("Oklch", Oklch),
    Row("JzAzBz", JzAzBz),
    Row("JzCzHz", JzCzHz),
    Row("XYZ", XYZ),
    Row("HSLuv", HSLuv, step = 1.0),
    Row("HPLuv", HPLuv, step = 1.0),
    Row("Linear sRGB", LINEAR_SRGB),
    Row("ACES2065-1 RGB", ACES),
    Row("ACEScc RGB", ACEScc),
    Row("ACEScct RGB", ACEScct),
    Row("ACEScg RGB", ACEScg),
    Row("Adobe RGB 1998", ADOBE_RGB),
    Row("BT.2020 (REC.2020) RGB", BT_2020),
    Row("BT.709 (REC.709) RGB", BT_709),
    Row("DCI P3 RGB", DCI_P3),
    Row("Display P3 RGB", DISPLAY_P3),
    Row("ROMM RGB (ProPhoto)", ROMM_RGB),
    Row("CMYK", CMYK),
    Row("Ansi (16 color)", Ansi16, step = 1.0, round = true),
    Row("Ansi (256 color)", Ansi256, step = 1.0, round = true),
)

fun main() {
    renderComposable(rootElementId = "root") {
        Style {
            className("input") style {
                property("appearance", "textfield")
                backgroundColor(rgba(0, 0, 0, .26f))
                maxWidth(20.percent)
                marginRight(4.px)
                paddingLeft(1.cssRem)
                paddingRight(1.cssRem)
                marginBottom(15.px)
                width(100.percent)
                height(1.8.cssRem)
                color(Color("inherit"))
                fontSize(.8.cssRem)
                borderRadius(.1.cssRem)
            }
            className("input") + hover() style {
                backgroundColor(rgba(1f, 1f, 1f, .12f))
            }
            className("colorpicker") style {
                display(DisplayStyle.Block)
                marginBottom(15.px)
                borderRadius(.25.cssRem)
                width(100.percent)
                height(60.px)
            }
            className("dropdown") style {
                display(DisplayStyle.Block)
                width(100.percent)
                paddingLeft(.7.cssRem)
                paddingRight(.7.cssRem)
                height(1.8.cssRem)
                marginBottom(15.px)
            }
        }
        var color: Color by remember { mutableStateOf(Color.parse("rebeccapurple").toSRGB()) }
        var row: Row by remember { mutableStateOf(rows[0]) }

        Div({ style { padding(25.px) } }) {
            Div {
                Label(attrs = { style { fontSize((0.6).cssRem) } }) {
                    Text("Convert from: ")
                }
                Select({
                    classes("dropdown")
                    onInput { e ->
                        e.value?.let { name ->
                            row = rows.first { it.name == name }
                            color = row.space.convert(color)
                        }
                    }
                }) {
                    rows.forEach { Option(it.name) { Text(it.name) } }
                }
            }

            Div {
                Label(attrs = { style { fontSize((0.6).cssRem) } }) {
                    Text("Enter a color: ")
                }

                Div {
                    for (i in 0 until color.space.components.lastIndex) {
                        NumberInput(attrs = {
                            classes("input")
                            step(if (color.space.components[i].isPolar) 1.0 else row.step)
                            onInput { event ->
                                val a = color.toArray()
                                a[i] = ((event.value ?: 0f).toFloat() / row.scale).toFloat()
                                color = row.space.create(a)
                            }
                            value(fmt(color.toArray()[i], row))
                        })
                    }
                }
            }

            Div {
                Label(attrs = { style { fontSize((0.6).cssRem) } }) {
                    Text("Or pick one: ")
                }
                Input(InputType.Color, attrs = {
                    classes("colorpicker")
                    onInput { color = Color.parse(it.value) }
                    value(color.toSRGB().toHex())
                })
            }

            Div({
                style {
                    color(Color((if (color.wcagContrastRatio(dark) < 7) light else dark).toHex()))
                    marginLeft(1.25.em)
                    marginTop((-3.6).em)
                    marginBottom(3.6.em)
                }
                attr("pointer-events", "none")
            }) {
                Text(color.toSRGB().toHex())
            }

            Table {
                Tbody {
                    rows.forEach { row ->
                        Tr {
                            Td { Text(row.name) }
                            Td {
                                Text(row.space.convert(color).toArray().dropLast(1).joinToString {
                                    fmt(it, row)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun fmt(n: Number, row: Row): String {
    val nn = if (n.toDouble().isNaN()) 0.0 else n.toDouble()
    val number = (nn * row.scale).let { if (row.round) it.roundToInt() else it }
    return number.asDynamic().toLocaleString("en-us",
        json("useGrouping" to false, "maximumFractionDigits" to 5)
    ).unsafeCast<String>()
}
