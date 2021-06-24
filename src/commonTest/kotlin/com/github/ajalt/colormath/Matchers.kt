package com.github.ajalt.colormath

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

fun convertTo(expected: Color) = object : Matcher<Color> {
    override fun test(value: Color): MatcherResult {
        return MatcherResult(
            value.toRGB().toRGBInt().argb == expected.toRGB().toRGBInt().argb,
            {
                val e = Expected("RGB(${expected.toRGB().toHex()})".show())
                val a = Actual("RGB(${value.toRGB().toHex()})".show())
                failure(e, a).message ?: intellijFormatError(e, a)
            },
            { "${expected.show().value} should not equal ${value.show().value}" }
        )
    }
}

fun Color.shouldEqualColor(expected: Color, tolerance: Double = 0.0005) {
    try {
        this::class shouldBe expected::class
        components().size shouldBe expected.components().size
        components().zip(expected.components()).forEach { (a, e) ->
            a shouldBe (e plusOrMinus tolerance.toFloat())
        }
    } catch (e: AssertionError) {
        println(this)
        throw e
    }
}
