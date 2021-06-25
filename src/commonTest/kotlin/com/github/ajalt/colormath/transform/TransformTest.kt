package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.convertTo
import com.github.ajalt.colormath.shouldEqualColor
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test

class TransformTest {
    @Test
    fun interpolate() = forAll(
        row(RGB(0, 0, 0), RGB(254, 254, 254), 0f, RGB(0, 0, 0)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), .5f, RGB(127, 127, 127)),
        row(RGB(0, 0, 0), RGB(254, 254, 254), 1f, RGB(254, 254, 254)),
        row(RGB(0, 0, 0), RGB(254, 254, 254).toXYZ(), 1f, RGB(254, 254, 254)),
    ) { c1, c2, a, ex ->
        c1.interpolate(c2, a).shouldEqualColor(ex)
    }

    @Test
    fun multiplyAlpha() = forAll(
        row(RGB(100, 100, 100, 1f), RGB(100, 100, 100, 1f)),
        row(RGB(100, 100, 100, 0.5f), RGB(50, 50, 50, 0.5f)),
        row(RGB(100, 100, 100, 0f), RGB(0, 0, 0, 0f)),
    ) { rgb, ex ->
       rgb.multiplyAlpha().shouldEqualColor(ex)
    }

    @Test
    fun divideAlpha() = forAll(
        row(RGB(100, 100, 100, 1f), RGB(100, 100, 100, 1f)),
        row(RGB(50, 50, 50, 0.5f), RGB(100, 100, 100, 0.5f)),
        row(RGB(100, 100, 100, 0f), RGB(100, 100, 100, 0f)),
    ) { rgb, ex ->
        rgb.divideAlpha().shouldEqualColor(ex)
    }
}
