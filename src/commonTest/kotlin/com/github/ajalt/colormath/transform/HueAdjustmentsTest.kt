package com.github.ajalt.colormath.transform

import io.kotest.data.Row4
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HueAdjustmentsTest {
    @Test
    fun shorter() = doTest(
        HueAdjustments.shorter,
        row(10, 350, 370, 350),
        row(350, 10, 350, 370),
        row(0, 10, 0, 10),
        row(0, 0, 0, 0),
    )

    @Test
    fun longer() = doTest(
        HueAdjustments.longer,
        row(10, 20, 370, 20),
        row(20, 10, 20, 370),
        row(0, 350, 0, 350),
        row(0, 0, 0, 0),
    )

    @Test
    fun increasing() = doTest(
        HueAdjustments.increasing,
        row(20, 10, 20, 370),
        row(350, 10, 350, 370),
        row(0, 10, 0, 10),
        row(0, 0, 0, 0),
    )

    @Test
    fun decreasing() = doTest(
        HueAdjustments.decreasing,
        row(10, 20, 370, 20),
        row(10, 350, 370, 350),
        row(10, 0, 10, 0),
        row(0, 0, 0, 0),
    )

    private fun doTest(adj: HueAdjustment, vararg rows: Row4<Int, Int, Int, Int>) {
        forAll(*rows) { a1, a2, ex1, ex2 ->
            adj(a1.toFloat(), a2.toFloat()) shouldBe (ex1.toFloat() to ex2.toFloat())
        }
    }
}
