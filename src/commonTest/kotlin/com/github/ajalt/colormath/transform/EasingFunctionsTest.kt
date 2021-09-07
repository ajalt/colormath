package com.github.ajalt.colormath.transform

import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
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
        EasingFunctions.cubicBezier(x1, y1, x2, y2).ease(t.toFloat()).toDouble() shouldBe (ex plusOrMinus 1e-5)
    }

    @Test
    fun builtInEasings() = forAll(
        row(EasingFunctions.ease(), 0.2, 0.2952443),
        row(EasingFunctions.ease(), 0.8, 0.9756253),
        row(EasingFunctions.easeIn(), 0.2, 0.0622820),
        row(EasingFunctions.easeIn(), 0.8, 0.6916339),
        row(EasingFunctions.easeOut(), 0.2, 0.3083660),
        row(EasingFunctions.easeOut(), 0.8, 0.9377179),
        row(EasingFunctions.easeInOut(), 0.2, 0.0816598),
        row(EasingFunctions.easeInOut(), 0.8, 0.9183401),
    ) { fn, t, ex ->
        fn.ease(t.toFloat()).toDouble() shouldBe (ex plusOrMinus 1e-5)
    }
}
