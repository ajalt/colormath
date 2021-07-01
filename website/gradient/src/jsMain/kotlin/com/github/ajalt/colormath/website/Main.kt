@file:NoLiveLiterals // work around some compose bug

package com.github.ajalt.colormath.website

import androidx.compose.runtime.*
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.transform.interpolator
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.HTMLCanvasElement

fun main() {
    var color1: Color by mutableStateOf(Color.parse("rebeccapurple"))
    var color2: Color by mutableStateOf(Color.parse("aliceblue"))

    @Composable
    fun row(model: ColorModel<*>) {
        Div {
            H4(attrs = {
                style { property("margin-bottom", 0.px) }
            }) { Text(model.name) }
            Canvas(attrs = {
                this.attr("width", "800")
                this.attr("height", "80")

                style {
                    marginTop(16.px)
                    borderRadius(4.px)
                }
            }) {
                DomSideEffect(color1 to color2) { updateCanvas(it, model.convert(color1), model.convert(color2)) }
            }
        }
    }

    renderComposable(rootElementId = "root") {

        Div(attrs = { style { margin(16.px) } }) {
            Div {
                Input(InputType.Color, attrs = {
                    value(color1.toRGB().toHex())
                    onInput { color1 = RGB(it.value) }
                })
                Input(InputType.Color, attrs = {
                    value(color2.toRGB().toHex())
                    onInput { color2 = RGB(it.value) }
                })
            }
        }

        row(RGB)
        row(LAB)
        row(LUV)
        row(Oklab)
    }
}

private fun updateCanvas(canvas: HTMLCanvasElement, color1: Color, color2: Color) {
    val lerp = RGB.interpolator(color1, color2)
    canvas.edit2dImageData {
        repeat(width) { x ->
            repeat(height) { y ->
                data.setColor(x, y, width, lerp.interpolate(x / width.toFloat()))
            }
        }
    }
}

private fun Uint8ClampedArray.setColor(x: Int, y: Int, width: Int, rgb: RGB) {
    val i = (y * width + x) * 4
    with(asDynamic()) {
        this[i] = rgb.redInt
        this[i + 1] = rgb.greenInt
        this[i + 2] = rgb.blueInt
        this[i + 3] = rgb.alphaInt
    }
}
