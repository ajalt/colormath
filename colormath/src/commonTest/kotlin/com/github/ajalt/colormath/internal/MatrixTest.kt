package com.github.ajalt.colormath.internal

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixTest {
    @Test
    @JsName("matrix_times_matrix")
    fun `matrix times matrix`() {
        val l = Matrix(
            -2f, 5f, 6f,
            -1f, -3f, 2f,
            0f, 7f, 4f,
        )
        val r = Matrix(
            8f, 1f, -2f,
            3f, 5f, 6f,
            -4f, -3f, -1f,
        )
        (l.dot(r)).rowMajor shouldBe Matrix(
            -25f, 5f, 28f,
            -25f, -22f, -18f,
            5f, 23f, 38f,
        ).rowMajor
    }

    @Test
    fun inverse() {
        val orig = Matrix(
            9f, 13f, 14f,
            12f, 11f, 6f,
            3f, 5f, 15f,
        )
        val copy = orig.copy()
        val ex = Matrix(
            -0.2631579f, +0.24366471f, +0.14814815f,
            +0.31578946f, -0.18128654f, -0.22222222f,
            -0.05263158f, +0.0116959065f, +0.11111111f,
        )
        orig.inverse().rowMajor shouldBe ex.rowMajor
        orig.rowMajor shouldBe copy.rowMajor

        copy.inverse(inPlace = true).rowMajor shouldBe ex.rowMajor
        copy.rowMajor shouldBe ex.rowMajor
    }

    @Test
    @JsName("matrix_times_vec")
    fun `matrix times vec`() {
        val l = Matrix(
            1f, 2f, 3f,
            4f, 5f, 6f,
            7f, 8f, 9f,
        )
        l.dot(10f, 20f, 30f).values shouldBe floatArrayOf(140f, 320f, 500f)
    }
}

// TODO(kotest): go back to kotest once is supports wasm
private infix fun FloatArray.shouldBe(other: FloatArray) {
    for (i in indices) {
        assertEquals(this[i], other[i], 0.00000001f, "index $i")
    }
}
