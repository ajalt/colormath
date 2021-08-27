package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.RGB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GamutTest {
    @Test
    fun isInSRGBGamut() = forAll(
        row(RGB("#f5deb3"), true),
        row(RGB("#000"), true),
        row(RGB("#fff"), true),
        row(RGB(-0.01, 0.0, 0.0), false),
        row(RGB(0.0, -0.01, 0.0), false),
        row(RGB(0.0, 0.0, -0.01), false),
        row(RGB(1.1f, 1.1f, 1.1f), false),
    ) { c, ex ->
        c.isInSRGBGamut() shouldBe ex
    }
}
