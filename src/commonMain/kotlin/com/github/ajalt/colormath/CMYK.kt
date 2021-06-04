package com.github.ajalt.colormath

/**
 * A color in the CMYK (cyan, magenta, yellow, and key) color model.
 *
 * All color channels are integers in the range `[0, 100]`.
 */
data class CMYK(val c: Int, val m: Int, val y: Int, val k: Int, val a: Float = 1f) : Color {
    init {
        require(c in 0..100) { "c must be in range [0, 100] in $this" }
        require(m in 0..100) { "m must be in range [0, 100] in $this" }
        require(y in 0..100) { "y must be in range [0, 100] in $this" }
        require(k in 0..100) { "k must be in range [0, 100] in $this" }
        require(a in 0f..1f) { "a must be in range [0, 1] in $this" }
    }

    override val alpha: Float get() = a

    override fun toRGB(): RGB {
        val c = this.c / 100f
        val m = this.m / 100f
        val y = this.y / 100f
        val k = this.k / 100f
        val r = (1 - c) * (1 - k)
        val g = (1 - m) * (1 - k)
        val b = (1 - y) * (1 - k)
        return RGB(r, g, b, alpha)
    }

    override fun toCMYK() = this
}
