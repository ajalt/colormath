package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.RGB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

// test cases from https://www.w3.org/TR/css-color-5/#colorcontrast
class ContrastTest {
    @Test
    fun wcagLuminance() = forAll(
        row(RGB("#f5deb3"), 0.749),
        row(RGB("#d2b48c"), 0.482),
        row(RGB("#a0522d"), 0.137),
        row(RGB("#b22222"), 0.107),
    ) { c, ex ->
        c.wcagLuminance() shouldBe (ex.toFloat() plusOrMinus 0.001f)
    }

    @Test
    fun wcagContrastRatio() = forAll(
        row(RGB("#d2b48c"), 1.501),
        row(RGB("#a0522d"), 4.273),
        row(RGB("#b22222"), 5.081),
    ) { c, ex ->
        RGB("#f5deb3").wcagContrastRatio(c) shouldBe (ex.toFloat() plusOrMinus 0.001f)
    }

    @Test
    fun mostContrasting() {
        RGB("#f5deb3").mostContrasting(
            RGB("#d2b48c"), RGB("#a0522d"), RGB("#b22222"), RGB("#d2691e")
        ) shouldBe RGB("#b22222")
    }

    @Test
    fun firstWithContrastOrNull() = forAll(
        row(4.5, RGB("#006400")),
        row(5.8, RGB("#800000")),
        row(9.9, null),
    ) { r, ex ->
        RGB("#f5deb3").firstWithContrastOrNull(
            RGB("#ffe4c4"), RGB("#b8860b"), RGB("#808000"), RGB("#a0522d"), RGB("#006400"), RGB("#800000"),
            targetContrast = r.toFloat()
        ) shouldBe ex
    }

    @Test
    fun firstWithContrast() {
        RGB("#f5deb3").firstWithContrast(
            RGB("#ffe4c4"), RGB("#b8860b"), targetContrast = 99f
        ) shouldBe RGB("#000")
    }
}
