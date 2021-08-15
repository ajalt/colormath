package com.github.ajalt.colormath.internal

import kotlin.jvm.JvmInline

@JvmInline
internal value class Matrix(val rowMajor: FloatArray) {
    constructor(
        v00: Float, v10: Float, v20: Float,
        v01: Float, v11: Float, v21: Float,
        v02: Float, v12: Float, v22: Float,
    ) : this(floatArrayOf(
        v00, v10, v20,
        v01, v11, v21,
        v02, v12, v22,
    ))

    fun copy() = Matrix(rowMajor.copyOf())

    operator fun get(x: Int, y: Int): Float = rowMajor[y * 3 + x]

    operator fun set(x: Int, y: Int, value: Float) {
        rowMajor[y * 3 + x] = value
    }

    operator fun set(x: Int, y: Int, value: Double) = set(x, y, value.toFloat())

    override fun toString(): String {
        return """Mat3(
        |   ${get(0, 0)}, ${get(1, 0)}, ${get(2, 0)}, 
        |   ${get(0, 1)}, ${get(1, 1)}, ${get(2, 1)}, 
        |   ${get(0, 2)}, ${get(1, 2)}, ${get(2, 2)}, 
        |)
        """.trimMargin()
    }
}

internal fun Matrix.inverse(inPlace: Boolean = false): Matrix {
    val a = get(0, 0).toDouble()
    val b = get(1, 0).toDouble()
    val c = get(2, 0).toDouble()
    val d = get(0, 1).toDouble()
    val e = get(1, 1).toDouble()
    val f = get(2, 1).toDouble()
    val g = get(0, 2).toDouble()
    val h = get(1, 2).toDouble()
    val i = get(2, 2).toDouble()

    val A = e * i - h * f
    val B = h * c - b * i
    val C = b * f - e * c

    val det = a * A + d * B + g * C

    val out = if (inPlace) this else copy()
    out[0, 0] = A / det
    out[0, 1] = (g * f - d * i) / det
    out[0, 2] = (d * h - g * e) / det
    out[1, 0] = B / det
    out[1, 1] = (a * i - g * c) / det
    out[1, 2] = (g * b - a * h) / det
    out[2, 0] = C / det
    out[2, 1] = (d * c - a * f) / det
    out[2, 2] = (a * e - d * b) / det
    return out
}

internal inline fun <T> Matrix.dot(v0: Float, v1: Float, v2: Float, block: (Float, Float, Float) -> T): T {
    return block(
        get(0, 0) * v0 + get(1, 0) * v1 + get(2, 0) * v2,
        get(0, 1) * v0 + get(1, 1) * v1 + get(2, 1) * v2,
        get(0, 2) * v0 + get(1, 2) * v1 + get(2, 2) * v2,
    )
}

internal fun Matrix.dot(v0: Float, v1: Float, v2: Float): Vector = dot(v0, v1, v2, ::Vector)

internal fun Matrix.dot(other: Matrix): Matrix {
    fun f(x: Int, y: Int): Float {
        return this[0, y] * other[x, 0] + this[1, y] * other[x, 1] + this[2, y] * other[x, 2]
    }

    return Matrix(
        f(0, 0), f(1, 0), f(2, 0),
        f(0, 1), f(1, 1), f(2, 1),
        f(0, 2), f(1, 2), f(2, 2),
    )
}

/** Return the dot product of this matrix with a diagonal matrix, with the three arguments as the diagonal */
internal fun Matrix.dotDiagonal(v0: Float, v1: Float, v2: Float): Matrix {
    return Matrix(
        get(0, 0) * v0, get(1, 0) * v1, get(2, 0) * v2,
        get(0, 1) * v0, get(1, 1) * v1, get(2, 1) * v2,
        get(0, 2) * v0, get(1, 2) * v1, get(2, 2) * v2,
    )
}

@JvmInline
internal value class Vector(val values: FloatArray) {
    constructor(v0: Float, v1: Float, v2: Float) : this(floatArrayOf(v0, v1, v2))

    operator fun get(i: Int): Float = values[i]

    operator fun set(i: Int, value: Float) {
        values[i] = value
    }

    val r get() = values[0]
    val g get() = values[1]
    val b get() = values[2]

    val x get() = values[0]
    val y get() = values[1]
    val z get() = values[2]

    val l get() = values[0]
    val m get() = values[1]
    val s get() = values[2]

    operator fun component1() = values[0]
    operator fun component2() = values[1]
    operator fun component3() = values[2]
}

internal fun Matrix.scalarDiv(x: Float, inPlace: Boolean = false): Matrix {
    val out = (if (inPlace) this else copy()).rowMajor
    for (i in out.indices) {
        out[i] /= x
    }
    return Matrix(out)
}
