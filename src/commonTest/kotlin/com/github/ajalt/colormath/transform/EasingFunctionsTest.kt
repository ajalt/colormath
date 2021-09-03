package com.github.ajalt.colormath.transform

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EasingFunctionsTest {
    @Test
    fun cubicBezier() = forAll(
        row(0, 1, 1, 1, 1e-9, 0.0),
        row(0.3, -1, 0.7, 2, 0.0, 0.0),
        row(0.3, -1, 0.7, 2, 0.2, -0.1752580),
        row(0.3, -1, 0.7, 2, 0.4, 0.22073628),
        row(0.3, -1, 0.7, 2, 0.6, 0.77926371),
        row(0.3, -1, 0.7, 2, 0.8, 1.17525804),
        row(0.3, -1, 0.7, 2, 1.0, 1.0),
    ) { x1, y1, x2, y2, t, ex ->
        EasingFunctions.cubicBezier(x1, y1, x2, y2)(t) shouldBe (ex plusOrMinus 1e-5)
    }
}
