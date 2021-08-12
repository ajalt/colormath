package com.github.ajalt.colormath

import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

fun Color.shouldEqualColor(expected: Color, tolerance: Double = 5e-4, ignorePolar: Boolean = false) {
    try {
        this::class shouldBe expected::class
        val l = toArray()
        val r = expected.toArray()
        l.size shouldBe r.size
        for (i in l.indices) {
            if (ignorePolar && model.components[i].isPolar) continue
            l[i] shouldBe (r[i] plusOrMinus tolerance.toFloat())
        }
        val wp = (this.model as? WhitePointColorSpace<*>)?.whitePoint
        val wpEx = (expected.model as? WhitePointColorSpace<*>)?.whitePoint
        if (wp != null && wpEx != null) {
            wp shouldBe wpEx
        }
    } catch (e: AssertionError) {
        println("┌ ex ${expected.toSRGB().toHex()} $expected")
        println("└ ac ${this.toSRGB().toHex()} $this")
        throw e
    }
}
