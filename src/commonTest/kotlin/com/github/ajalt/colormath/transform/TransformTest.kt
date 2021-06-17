package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.convertTo
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test

class TransformTest {
    @Test
    fun blend() = forAll(
        row(RGB(0, 0, 0), RGB(254, 254, 254), 0f, RGB(0, 0, 0)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), .5f, RGB(127, 127, 127)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), 1f, RGB(254, 254, 254)),
        row(RGB(0, 0, 0), RGB(254, 254, 254).toXYZ(), 1f, RGB(254, 254, 254)),
    ) { c1, c2, a, ex ->
        val actual = c1.blend(c2, a)
        actual.shouldBeTypeOf<RGB>()
        actual should convertTo(ex)
    }
}
