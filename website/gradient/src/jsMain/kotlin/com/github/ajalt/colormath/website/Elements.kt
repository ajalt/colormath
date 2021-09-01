@file:Suppress("FunctionName")

package com.github.ajalt.colormath.website

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLHRElement
import org.w3c.dom.ImageData

@Composable
fun Canvas(
    width: Int,
    height: Int,
    attrs: AttrBuilderContext<HTMLCanvasElement> = {},
    fallbackContent: ContentBuilder<HTMLCanvasElement>? = null,
) {
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = {
            attr("width", width.toString())
            attr("height", height.toString())
            attrs()
        },
        content = fallbackContent
    )
}
