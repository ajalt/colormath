@file:Suppress("FunctionName")

package com.github.ajalt.colormath.website

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageData

@Composable
fun Canvas(
    attrs: AttrBuilderContext<HTMLCanvasElement> = {},
    fallbackContent: ContentBuilder<HTMLCanvasElement>? = null,
) {
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = attrs,
        content = fallbackContent
    )
}

inline fun HTMLCanvasElement.edit2dImageData(block: ImageData.() -> Unit) {
    val ctx = getContext("2d") as CanvasRenderingContext2D
    val imageData = ctx.createImageData(width.toDouble(), height.toDouble())
    imageData.block()
    ctx.putImageData(imageData, 0.0, 0.0)
}
