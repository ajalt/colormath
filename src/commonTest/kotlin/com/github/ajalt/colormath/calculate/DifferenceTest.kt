package com.github.ajalt.colormath.calculate

import com.github.ajalt.colormath.LAB
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DifferenceTest {
    private val c1 = LAB(100.00000000, 21.57210357, 272.22819350)

    @Test
    fun cie76() {
        forAll(
            row(LAB(100.00000000, 426.67945353, 72.39590835), 451.713301974),
            row(LAB(100.00000000, 74.05216981, 276.45318193), 52.6498611564),
            row(LAB(100.00000000, 8.32281957, -73.58297716), 346.064891718),
        ) { c, ex ->
            val d = c1.differenceCIE76(c)
            d.toDouble() shouldBe (ex plusOrMinus 1e-4)
            c1.euclideanDistance(c).toDouble() shouldBe (ex plusOrMinus 1e-4)
        }
    }

    @Test
    fun cie94() = forAll(
        row(LAB(100.00000000, 426.67945353, 72.39590835), 83.77922550, false),
        row(LAB(100.00000000, 74.05216981, 276.45318193), 10.05393195, false),
        row(LAB(100.00000000, 8.32281957, -73.58297716), 57.53545370, false),
        row(LAB(100.00000000, 426.67945353, 72.39590835), 88.33555305, true),
        row(LAB(100.00000000, 74.05216981, 276.45318193), 10.61265789, true),
        row(LAB(100.00000000, 8.32281957, -73.58297716), 60.36868726, true),
    ) { c, ex, textiles ->
        c1.differenceCIE94(c, textiles).toDouble() shouldBe (ex plusOrMinus 1e-5)
    }

    @Test
    fun cie2000() = forAll(
        row(LAB(100.00000000, 426.67945353, 72.39590835), 94.0356490267),
        row(LAB(100.00000000, 74.05216981, 276.45318193), 14.8790641937),
        row(LAB(100.00000000, 8.32281957, -73.58297716), 68.2309487895),
    ) { c, ex ->
        c1.differenceCIE2000(c).toDouble() shouldBe (ex plusOrMinus 1e-4)
    }
}
