package com.github.ajalt.colormath.website

import androidx.compose.runtime.*
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.transform.interpolator
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.RangeInput
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.json

private val rectColors = listOf(RGB, LAB, LUV, Oklab)

fun main() {
    var colors by mutableStateOf(listOf(RGB(0.0, 0.0, 0.5), Color.parse("aliceblue")))

    @Composable
    fun row(space: ColorSpace<*>) {
        Div(attrs = {
            style {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                flexWrap(FlexWrap.Wrap)
                margin(4.px)
            }
        }) {
            Div(attrs = {
                style {
                    width(5.em)
                }
            }) { Text(space.name) }
            Canvas(attrs = {
                this.attr("width", "600")
                this.attr("height", "50")

                style {
                    borderRadius(4.px)
                }
            }) {
                DomSideEffect(colors) { updateCanvas(it, space.convert(colors[0]), space.convert(colors[1])) }
            }
        }
    }

    renderComposable(rootElementId = "root") {

        Div(attrs = {
            style {
                display(DisplayStyle.Flex)
                flexWrap(FlexWrap.Wrap)
                justifyContent(JustifyContent.SpaceEvenly)
                property("margin-bottom", 8.px)
            }
        }) {
            for ((i, color) in colors.withIndex()) {
                Div {
                    for (j in 0 until color.space.components.lastIndex) {
                        val component = color.space.components[j]
                        val value = color.toArray()[j]
                        Div(attrs = {
                            style {
                                display(DisplayStyle.Flex)
                                flexWrap(FlexWrap.Wrap)
                            }
                        }) {
                            Div(attrs = { style { width(2.em) } }) { Text(component.name) }
                            Div(attrs = { style { width(3.em) } }) { Text(fmt(value)) }
                            Div {
                                RangeInput(value,
                                    min = 0,
                                    max = 1,
                                    step = 0.01,
                                    attrs = {
                                        onInput {
                                            val array = color.toArray().apply { set(j, (it.value ?: 0).toFloat()) }
                                            val new = color.space.create(array)
                                            colors = if (i == 0) listOf(new, colors[1]) else listOf(colors[0], new)
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }

        rectColors.forEach { row(it) }
    }
}

private fun updateCanvas(canvas: HTMLCanvasElement, color1: Color, color2: Color) {
    val lerp = color1.space.interpolator(color1, color2)
    canvas.edit2dImageData {
        // draw the first row
        for (x in 0 until width) {
            data.setColor(x, 0, width, lerp.interpolate(x / width.toFloat()))
        }
        // copy the first row to the rest of the canvas
        for (y in 0 until height) {
            data.asDynamic().copyWithin(y * width * 4, 0, width * 4)
        }
    }
}

private fun Uint8ClampedArray.setColor(x: Int, y: Int, width: Int, color: Color) {
    val rgb = color.toSRGB()
    val i = (y * width + x) * 4
    with(asDynamic()) {
        this[i] = rgb.redInt
        this[i + 1] = rgb.greenInt
        this[i + 2] = rgb.blueInt
        this[i + 3] = rgb.alphaInt
    }
}

private fun fmt(number: Number): String {
    return number.asDynamic().toLocaleString("en-us",
        json("useGrouping" to false, "maximumFractionDigits" to 3)
    ).unsafeCast<String>()
}
