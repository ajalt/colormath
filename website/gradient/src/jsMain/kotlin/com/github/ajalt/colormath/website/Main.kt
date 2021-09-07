package com.github.ajalt.colormath.website

import androidx.compose.runtime.*
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.transform.EasingFunctions
import com.github.ajalt.colormath.transform.InterpolationMethods
import com.github.ajalt.colormath.transform.interpolator
import com.github.ajalt.colormath.transform.sequence
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.className
import org.jetbrains.compose.web.css.selectors.hover
import org.jetbrains.compose.web.css.selectors.plus
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.random.Random

private val spaces = listOf(RGB, LAB, LUV, Oklab, JzAzBz)

@Composable
fun row(space: ColorSpace<*>, colors: List<Color>, spline: Boolean, easing: String) {
    Div(attrs = { classes("row") }) {
        Div(attrs = { style { width(5.em) } }) {
            Text(space.name)
        }
        Canvas(width = 600, height = 50, attrs = { style { borderRadius(4.px) } }) {
            DomSideEffect(Triple(spline, colors, easing)) { updateCanvas(it, space, spline, colors, easing) }
        }
    }
}

private val easings = mapOf(
    "linear" to EasingFunctions.linear(),
    "ease" to EasingFunctions.ease(),
    "ease-in" to EasingFunctions.easeIn(),
    "ease-in-out" to EasingFunctions.easeInOut(),
    "ease-out" to EasingFunctions.easeOut(),
    "midpoint 25%" to EasingFunctions.midpoint(.25),
    "midpoint 75%" to EasingFunctions.midpoint(.75),
)

fun main() {
    renderComposable(rootElementId = "root") {
        Style {
            className("input") style {
                backgroundColor(rgba(0, 0, 0, .26f))
                color(Color("inherit"))
                fontSize(.8.cssRem)
                borderRadius(.1.cssRem)
                height(1.8.cssRem)
                paddingLeft(1.cssRem)
            }
            className("input") + hover() style {
                backgroundColor(rgba(1f, 1f, 1f, .12f))
            }
            className("numberinput") style {
                width(3.cssRem)
            }
            className("select") style {
                fontSize(.8.cssRem)
                borderRadius(.1.cssRem)
                height(1.8.cssRem)
                margin(.8.cssRem)
            }
            className("btn") style {
                paddingRight(1.cssRem)
            }
            className("row") style {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                flexWrap(FlexWrap.Wrap)
                marginTop(4.px)
            }
            className("pickers") style {
                display(DisplayStyle.Flex)
                flexWrap(FlexWrap.Wrap)
                justifyContent(JustifyContent.SpaceBetween)
                width(600.px)
            }
            className("colorpicker") style {
                width(40.px)
                height(30.px)
            }
            className("config") style {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.SpaceBetween)
                flexWrap(FlexWrap.Wrap)
                marginLeft(4.em)
            }
        }
        var colors: List<Color> by remember { mutableStateOf(listOf(RGB("#33d"), RGB("#eef"))) }
        var spline by remember { mutableStateOf(false) }
        var easing by remember { mutableStateOf("linear") }

        Div(attrs = {
            classes("config")
            style { property("margin-bottom", 8.px) }
        }) {
            Div {
                NumberInput(min = 2, max = 7) {
                    classes("input", "numberinput")
                    value(colors.size.toString())
                    onInput {
                        val n = it.value?.toInt() ?: return@onInput
                        colors = colors.take(n) + List(n - colors.size) { randColor() }
                    }
                    style { margin(1.em) }
                }
                Label { Text("number of stops") }
            }
            Div {
                Button(attrs = {
                    classes("input", "btn")
                    onClick { colors = colors.map { randColor() } }
                }) { Text("randomize colors") }
            }
            Div {
                CheckboxInput(checked = spline) {
                    onInput { spline = it.value }
                    style { margin(1.em) }
                }
                Label { Text("spline interpolation") }
            }
            Div {
                Select(attrs = {
                    classes("select")
                    onInput { it.value?.let { s -> easing = s } }
                }) {
                    easings.keys.forEach { Option(it) { Text(it) } }
                }
                Label { Text("easing function") }
            }
        }

        Div(attrs = { classes("row") }) {
            Div(attrs = { style { width(5.em) } })
            Div(attrs = { classes("pickers") }) {
                for ((i, color) in colors.withIndex()) {
                    Input(InputType.Color, attrs = {
                        classes("colorpicker")
                        onInput { e -> colors = colors.toMutableList().also { it[i] = Color.parse(e.value) } }
                        value(color.toSRGB().toHex())
                    })
                }
            }
        }

        spaces.forEach { row(it, colors, spline, easing) }
    }
}

private fun randf(a: Int, b: Int): Double = a + (b - a) * Random.nextDouble()
private fun randColor() = LCHab(randf(10, 90), randf(10, 90), randf(0, 359)).toSRGB().clamp()

private fun updateCanvas(
    canvas: HTMLCanvasElement,
    space: ColorSpace<*>,
    spline: Boolean,
    colors: List<Color>,
    easing: String,
) {
    val lerp = space.interpolator {
        colors.forEach { stop(it) }
        method = if (spline) InterpolationMethods.monotoneSpline(true) else InterpolationMethods.linear()
        easings[easing]?.let { this.easing = it }
    }
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    lerp.sequence(canvas.width).forEachIndexed { x, color ->
        ctx.fillStyle = color.toSRGB().toHex()
        ctx.fillRect(x.toDouble(), 0.0, 1.0, canvas.height.toDouble())
    }
}

