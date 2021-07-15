package com.github.ajalt.colormath

/** A white point specified in XYZ tristimulus values */
data class Illuminant(val x: Float, val y: Float, val z: Float) {
    companion object {
        /** CIE 1931 2° Standard Illuminant A */
        val A = Illuminant(1.09850f, 1.00000f, 0.35585f)

        /** CIE 1931 2° Standard Illuminant B */
        val B = Illuminant(0.99072f, 1.00000f, 0.85223f)

        /** CIE 1931 2° Standard Illuminant C */
        val C = Illuminant(0.98074f, 1.00000f, 1.18232f)

        /** CIE 1931 2° Standard Illuminant D50 */
        val D50 = Illuminant(0.96422f, 1.00000f, 0.82521f)

        /** CIE 1931 2° Standard Illuminant D55 */
        val D55 = Illuminant(0.95682f, 1.00000f, 0.92149f)

        /** CIE 1931 2° Standard Illuminant D65 */
        val D65 = Illuminant(0.95047f, 1.00000f, 1.08883f)

        /** CIE 1931 2° Standard Illuminant D75 */
        val D75 = Illuminant(0.94972f, 1.00000f, 1.22638f)

        /** CIE 1931 2° Standard Illuminant E */
        val E = Illuminant(1.00000f, 1.00000f, 1.00000f)
    }
}

