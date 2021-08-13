package com.github.ajalt.colormath.website

import androidx.compose.runtime.*
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.Color
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.js.json

fun main() {
    var color: Color by mutableStateOf(Color.parse("rebeccapurple"))

    @Composable
    fun row(
        space: ColorSpace<*>,
        intScale: Int? = null,
    ) {
        P {
            Div(attrs = {
                style {
                    display(DisplayStyle.InlineBlock)
                    property("min-width", 5.em)
                }
            }) { Text(space.name) }
            for (i in 0 until space.components.lastIndex) {
                Input(InputType.Number, attrs = {
                    style {
                        property("max-width", 20.percent)
                        property("appearance", "textfield")
                        property("margin-right", 4.px)
                        property("padding-left", 1.cssRem)
                        property("padding-right", 1.cssRem)
                    }
                    classes("md-search__input") // class is part of mkdocs material
                    attr("step", if (intScale == null) "0.1" else "1")

                    onInput {
                        val a = space.convert(color).toArray()
                        a[i] = (it.value ?: 0).toFloat() / (intScale ?: 1)
                        color = space.create(a)
                    }
                    value(fmt(space.convert(color).toArray()[i] * (intScale ?: 1)))
                })
            }
        }
    }

    renderComposable(rootElementId = "root") {
        Div(attrs = { style { padding(25.px) } }) {
            P {
                Label("pickerInput", attrs = {
                    style {
                        property("min-width", 5.em)
                        display(DisplayStyle.InlineBlock)
                    }
                }) { Text("Pick Color") }
                Input(InputType.Color, attrs = {
                    id("pickerInput")
                    onInput { color = RGB(it.value) }
                    value(color.toSRGB().toHex())
                })
            }
            row(RGB, intScale = 255)
            listOf(HSL, HSV, HWB, LAB, LCHab, LUV, LCHuv, XYZ, Oklab, Oklch, CMYK).forEach { row(it) }
            row(Ansi16, intScale = 1)
            row(Ansi256, intScale = 1)

            Div(attrs = {
                style {
                    backgroundColor(Color(color.toSRGB().toHex()))
                    color(Color(if (color.toHSL().l > 40) "black" else "white"))
                    padding(25.px)
                    border(1.px, LineStyle.Solid, Color("#ced4da"))
                    borderRadius(.25.cssRem)
                }
            }) {
                Text(color.toSRGB().toHex())
            }
        }
    }
}

private fun fmt(number: Number): String {
    return number.asDynamic().toLocaleString("en-us",
        json("useGrouping" to false, "maximumFractionDigits" to 3)
    ).unsafeCast<String>()
}
