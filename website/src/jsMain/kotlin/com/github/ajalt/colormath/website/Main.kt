package com.github.ajalt.colormath.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.events.WrappedEvent
import org.jetbrains.compose.web.renderComposable
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.CssColors
import com.github.ajalt.colormath.RGB
import kotlinx.browser.document
import kotlin.js.json

fun main() {
    var color: Color by mutableStateOf(CssColors.rebeccapurple)

    @Composable
    fun row(
        title: String,
        vararg a: Pair<Number, (Double) -> Color>,
        int: Boolean = false,
    ) {
        P {
            Div(attrs = {
                style {
                    display(DisplayStyle.InlineBlock)
                    property("min-width", value(5.em))
                }
            }) { Text(title) }
            for ((get, set) in a) {
                Input(InputType.Number, value = fmt(get), attrs = {
                    style {
                        property("max-width", value(20.percent))
                        property("appearance", value("textfield"))
                        property("margin-right", value(4.px))
                        property("padding-left", value(1.rem))
                        property("padding-right", value(1.rem))
                    }
                    classes("md-search__input") // class is part of mkdocs material
                    attr("step", if (int) "1" else "0.1")
                    onChange { color = set(it.stringValue.toDouble()) }
                    onTextInput { }
                })
            }
        }
    }

    renderComposable(rootElementId = "root") {
        Div(attrs = { style { padding(25.px) } }) {
            P {
                Label("pickerInput", attrs = {
                    style {
                        property("min-width", value(5.em))
                        display(DisplayStyle.InlineBlock)
                    }
                }) { Text("Pick Color") }
                Input(InputType.Color, value = color.toHex(), attrs = {
                    id("pickerInput")
                    onChange { color = RGB(it.stringValue) }
                })
            }
            row(
                "RGB",
                color.toRGB().r to { color.toRGB().copy(r = it.toInt()) },
                color.toRGB().g to { color.toRGB().copy(g = it.toInt()) },
                color.toRGB().b to { color.toRGB().copy(b = it.toInt()) },
                int = true,
            )
            row(
                "CMYK",
                color.toCMYK().c to { color.toCMYK().copy(c = it.toInt()) },
                color.toCMYK().m to { color.toCMYK().copy(m = it.toInt()) },
                color.toCMYK().y to { color.toCMYK().copy(y = it.toInt()) },
                color.toCMYK().k to { color.toCMYK().copy(k = it.toInt()) },
                int = true,
            )
            row(
                "HSL",
                color.toHSL().h to { color.toHSL().copy(h = it.toInt()) },
                color.toHSL().s to { color.toHSL().copy(s = it.toInt()) },
                color.toHSL().l to { color.toHSL().copy(l = it.toInt()) },
                int = true,
            )
            row(
                "HSV",
                color.toHSV().h to { color.toHSV().copy(h = it.toInt()) },
                color.toHSV().s to { color.toHSV().copy(s = it.toInt()) },
                color.toHSV().v to { color.toHSV().copy(v = it.toInt()) },
                int = true,
            )
            row(
                "HWB",
                color.toHWB().h to { color.toHWB().copy(h = it.toFloat()) },
                color.toHWB().w to { color.toHWB().copy(w = it.toFloat()) },
                color.toHWB().b to { color.toHWB().copy(b = it.toFloat()) },
            )
            row(
                "LAB",
                color.toLAB().l to { color.toLAB().copy(l = it) },
                color.toLAB().a to { color.toLAB().copy(a = it) },
                color.toLAB().b to { color.toLAB().copy(b = it) },
            )
            row(
                "LCH",
                color.toLCH().l to { color.toLCH().copy(l = it.toFloat()) },
                color.toLCH().c to { color.toLCH().copy(c = it.toFloat()) },
                color.toLCH().h to { color.toLCH().copy(h = it.toFloat()) },
            )
            row(
                "LUV",
                color.toLUV().l to { color.toLUV().copy(l = it.toFloat()) },
                color.toLUV().u to { color.toLUV().copy(u = it.toFloat()) },
                color.toLUV().v to { color.toLUV().copy(v = it.toFloat()) },
            )
            row(
                "XYZ",
                color.toXYZ().x to { color.toXYZ().copy(x = it) },
                color.toXYZ().y to { color.toXYZ().copy(y = it) },
                color.toXYZ().z to { color.toXYZ().copy(z = it) },
            )
            row(
                "Ansi-16",
                color.toAnsi16().code to { color.toAnsi16().copy(code = it.toInt()) },
                int = true,
            )
            row(
                "Ansi-256",
                color.toAnsi256().code to { color.toAnsi256().copy(code = it.toInt()) },
                int = true,
            )
            Div(attrs = {
                style {
                    backgroundColor(color.toHex())
                    color(if (color.toHSL().l > 40) "black" else "white")
                    padding(25.px)
                    border(1.px, LineStyle.Solid, Color("#ced4da"))
                    borderRadius(.25.rem)
                }
            }) {
                Text(color.toHex())
            }
        }
    }
}

private fun fmt(number: Number): String {
    return number.asDynamic().toLocaleString("en-us",
        json("useGrouping" to false, "maximumFractionDigits" to 3)
    ).unsafeCast<String>()
}

private val WrappedEvent.stringValue get() = nativeEvent.target.asDynamic().value as String
