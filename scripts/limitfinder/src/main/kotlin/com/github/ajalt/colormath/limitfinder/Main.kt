package com.github.ajalt.colormath.limitfinder

import com.github.ajalt.colormath.*

fun main() {
    val colors = listOf(
        HCL(0f, 0f, 0f),
        LAB(0f, 0f, 0f),
        LCH(0f, 0f, 0f),
        LUV(0f, 0f, 0f),
        Oklab(0f, 0f, 0f),
        Oklch(0f, 0f, 0f),
        XYZ(0f, 0f, 0f),
    )
    for (color in colors) {
        println("Computing sRGB range for ${color.model.name}")
        val mins = FloatArray(color.model.components.size - 1) { Float.MAX_VALUE }
        val maxs = FloatArray(color.model.components.size - 1) { Float.MIN_VALUE }
        for (r in 0..255) {
            for (g in 0..255) {
                for (b in 0..255) {
                    val c = color.model.convert(RGB(r, g, b)).toArray()
                    for (i in mins.indices) {
                        mins[i] = minOf(mins[i], c[i])
                        maxs[i] = maxOf(maxs[i], c[i])
                    }
                }
            }
        }

        color.model.components.dropLast(1).forEachIndexed { i, it ->
            println(" ${it.name}: [${mins[i]}, ${maxs[i]}]")
        }
        println()
    }
}
