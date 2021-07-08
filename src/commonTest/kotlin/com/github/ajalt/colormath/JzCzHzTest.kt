package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test


class JzCzHzTest {
    @Test
    fun roundtrip() {
        val jch = JzCzHz(0.01, 0.01, 0.01, 0.01)
        jch.toJzCzHz() shouldBeSameInstanceAs jch
        jch.toRGB().toJzCzHz().shouldEqualColor(jch)
    }
}
