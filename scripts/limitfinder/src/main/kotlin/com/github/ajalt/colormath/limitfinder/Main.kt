package com.github.ajalt.colormath.limitfinder

import com.github.ajalt.colormath.*

fun main() {
    val models = listOf(
        LCHuv,
        LAB,
        LCHab,
        LUV,
        Oklab,
        Oklch,
        XYZ,
        JzAzBz,
        JzCzHz,
    )
    for (model in models) {
        println("Computing sRGB range for ${model.name}")
        val mins = FloatArray(model.components.size - 1) { Float.MAX_VALUE }
        val maxs = FloatArray(model.components.size - 1) { Float.MIN_VALUE }
        for (r in 0..255) {
            for (g in 0..255) {
                for (b in 0..255) {
                    val c = model.convert(RGB(r, g, b)).toArray()
                    for (i in mins.indices) {
                        mins[i] = minOf(mins[i], c[i])
                        maxs[i] = maxOf(maxs[i], c[i])
                    }
                }
            }
        }

        model.components.dropLast(1).forEachIndexed { i, it ->
            println(" ${it.name}: [${mins[i]}, ${maxs[i]}]")
        }
        println()
    }
}
