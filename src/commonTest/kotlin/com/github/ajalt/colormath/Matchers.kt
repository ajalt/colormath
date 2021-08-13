package com.github.ajalt.colormath

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

fun <T : Color, U : Color> testColorConversions(
    vararg rows: Pair<T, U>,
    tolerance: Double = 5e-5,
    ignorePolar: Boolean = false,
    testInverse: Boolean = true,
) {
    val pairs = rows.map { row(it.first, it.second) }
    val inverse = if (testInverse) rows.map { row(it.second, it.first) } else emptyList()
    forAll(*(pairs + inverse).toTypedArray()) { l, r ->
        r.space.convert(l).shouldEqualColor(r, tolerance, ignorePolar)
    }
}

fun Color.shouldEqualColor(expected: Color, tolerance: Double = 5e-4, ignorePolar: Boolean = false) {
    try {
        this::class shouldBe expected::class
        space shouldBe expected.space

        val l = toArray()
        val r = expected.toArray()
        l.size shouldBe r.size
        for (i in l.indices) {
            if (ignorePolar && space.components[i].isPolar) continue
            l[i] shouldBe (r[i] plusOrMinus tolerance.toFloat())
        }
    } catch (e: AssertionError) {
        println("┌ ex ${expected.toSRGB().toHex()} $expected")
        println("└ ac ${this.toSRGB().toHex()} $this")
        throw e
    }
}
