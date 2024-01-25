package com.github.ajalt.colormath

import com.github.ajalt.colormath.model.HSL
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.test.Test

class HueColorTest {
    @Test
    fun hueAsRad(): Unit = assertSoftly {
        HSL(0, 0, 0).hueAsRad() shouldBe 0f
        HSL(180, 0, 0).hueAsRad() shouldBe PI.toFloat()
        HSL(360, 0, 0).hueAsRad() shouldBe (2 * PI).toFloat()
    }

    @Test
    fun hueAsGrad(): Unit = assertSoftly {
        HSL(0, 0, 0).hueAsGrad() shouldBe 0f
        HSL(180, 0, 0).hueAsGrad() shouldBe 200f
        HSL(360, 0, 0).hueAsGrad() shouldBe 400f
    }

    @Test
    fun hueAsTurns(): Unit = assertSoftly {
        HSL(0, 0, 0).hueAsTurns() shouldBe 0f
        HSL(180, 0, 0).hueAsTurns() shouldBe .5f
        HSL(360, 0, 0).hueAsTurns() shouldBe 1f
    }
}
