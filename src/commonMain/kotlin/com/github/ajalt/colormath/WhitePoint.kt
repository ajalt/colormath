package com.github.ajalt.colormath

/** A white point specified in XYZ tristimulus values */
data class WhitePoint(val name: String, val chromaticity: Chromaticity) {
    companion object {
        /** CIE 1931 2° Standard Illuminant A */
        val A = WhitePoint("A", Chromaticity(1.09850f, 0.35585f))

        /** CIE 1931 2° Standard Illuminant B */
        val B = WhitePoint("B", Chromaticity(0.99072f, 0.85223f))

        /** CIE 1931 2° Standard Illuminant C */
        val C = WhitePoint("C", Chromaticity(0.98074f, 1.18232f))

        /** CIE 1931 2° Standard Illuminant D50 */
        val D50 = WhitePoint("D50", Chromaticity(0.96422f, 0.82521f))

        /** CIE 1931 2° Standard Illuminant D55 */
        val D55 = WhitePoint("D55", Chromaticity(0.95682f, 0.92149f))

        /** CIE 1931 2° Standard Illuminant D65 */
        val D65 = WhitePoint("D65", Chromaticity(0.95047f, 1.08883f))

        /** CIE 1931 2° Standard Illuminant D75 */
        val D75 = WhitePoint("D75", Chromaticity(0.94972f, 1.22638f))

        /** CIE 1931 2° Standard Illuminant E */
        val E = WhitePoint("E", Chromaticity(1.00000f, 1.00000f))
    }

    override fun toString(): String = name
}

