package com.github.ajalt.colormath

import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test


class JzCzHzTest {
    @Test
    fun roundtrip() {
        JzCzHz(0.01, 0.02, 0.03, 0.04).let { it.toJzCzHz() shouldBeSameInstanceAs it }
        JzCzHz(0.01, 0.02, 0.03, 0.04f).let { it.toSRGB().toJzCzHz().shouldEqualColor(it) }
    }
}
