package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

fun <T : Color, U : Color> testColorConversions(
    vararg rows: Pair<T, U>,
    tolerance: Double = 5e-4,
    ignorePolar: Boolean = false,
) = forAll(*(rows.map { row(it.first, it.second) } + rows.map { row(it.second, it.first) }).toTypedArray()) { l, r ->
    r.model.convert(l).shouldEqualColor(r, tolerance, ignorePolar)
}

fun Color.shouldEqualColor(expected: Color, tolerance: Double = 5e-4, ignorePolar: Boolean = false) {
    try {
        this::class shouldBe expected::class
        model shouldBe expected.model

        val l = toArray()
        val r = expected.toArray()
        l.size shouldBe r.size
        for (i in l.indices) {
            if (ignorePolar && model.components[i].isPolar) continue
            l[i] shouldBe (r[i] plusOrMinus tolerance.toFloat())
        }
    } catch (e: AssertionError) {
        println("┌ ex ${expected.toSRGB().toHex()} $expected")
        println("└ ac ${this.toSRGB().toHex()} $this")
        throw e
    }
}
