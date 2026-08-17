package com.github.ajalt.colormath.model

import com.github.ajalt.colormath.model.RGBColorSpaces.SRGB
import com.github.ajalt.colormath.shouldEqualColor
import com.github.ajalt.colormath.testColorConversions
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test

class HCTTest {
    @Test
    fun roundtrip() {
        val hct = HCT(200.0, 30.0, 50.0, 0.5)
        HCT.convert(hct).shouldEqualColor(hct)
        HCT.create(hct.toArray()).shouldEqualColor(hct)
        HCT.convert(SRGB.convert(hct)).shouldEqualColor(hct, 0.05)
    }

    // Expected values generated with material-color-utilities' Hct.java
    @[Test JsName("RGB_to_HCT")]
    fun `RGB to HCT`() = testColorConversions(
        RGB("#ff0000") to HCT(27.408225, 113.357887, 53.232882),
        RGB("#00ff00") to HCT(142.139894, 108.410061, 87.737033),
        RGB("#0000ff") to HCT(282.788180, 87.230694, 32.302587),
        RGB("#ffff00") to HCT(111.051140, 75.509488, 97.138247),
        RGB("#00ffff") to HCT(196.544900, 58.954409, 91.116521),
        RGB("#ff00ff") to HCT(334.635185, 107.400226, 60.319934),
        RGB("#000000") to HCT(0.000000, 0.000000, 0.000000),
        RGB("#123456") to HCT(255.347283, 29.908670, 21.043062),
        RGB("#fa8072") to HCT(25.589078, 53.759847, 67.259953),
        RGB("#6750a4") to HCT(298.980997, 47.856526, 40.083244),
        RGB("#1b6ef3") to HCT(268.986801, 69.625010, 49.320907),
        RGB("#008772") to HCT(178.866549, 42.233502, 50.148016),
        RGB("#9a4058") to HCT(5.702380, 47.952230, 39.984106),
        RGB("#f0e68c") to HCT(105.605745, 36.008669, 90.327296),
        tolerance = 0.05,
    )

    // The hue of these near-achromatic colors is extremely sensitive to the small differences
    // between colormath's sRGB matrices and the ones material-color-utilities derives its expected
    // values with, so it's excluded from the comparison.
    @[Test JsName("RGB_to_HCT_achromatic")]
    fun `RGB to HCT achromatic`() = testColorConversions(
        RGB("#ffffff") to HCT(209.491959, 2.869035, 100.000000),
        RGB("#808080") to HCT(209.493826, 1.896022, 53.585013),
        tolerance = 0.05,
        ignorePolar = true,
    )

    /**
     * Check that conversion to sRGB matches material-color-utilities' HctSolver within one 8-bit
     * step per channel. Chromas larger than the sRGB gamut exercise the gamut mapping, which
     * reduces the chroma while keeping the hue and tone.
     */
    @[Test JsName("HCT_to_RGB")]
    fun `HCT to RGB`() {
        for (line in SOLVER_VECTORS.trim().lines()) {
            val (h, c, t, hex) = line.trim().split(" ")
            val actual = HCT(h.toFloat(), c.toFloat(), t.toFloat()).toSRGB()
            withClue("HCT($h, $c, $t) -> #$hex, was ${actual.toHex()}") {
                for (i in 0..2 step 1) {
                    val expectedInt = hex.substring(i * 2, i * 2 + 2).toInt(16)
                    val actualInt = (actual.toArray()[i] * 255).roundToInt().coerceIn(0, 255)
                    abs(actualInt - expectedInt) shouldBeLessThanOrEqual 1
                }
            }
        }
    }

    @[Test JsName("HCT_srgb_roundtrip")]
    fun `HCT sRGB roundtrip`() {
        for (r in 0..255 step 51) {
            for (g in 0..255 step 51) {
                for (b in 0..255 step 51) {
                    val rgb = SRGB.from255(r, g, b)
                    rgb.toHCT().toSRGB().shouldEqualColor(rgb, 0.005)
                }
            }
        }
    }

    @[Test JsName("HCT_extreme_tones")]
    fun `HCT extreme tones`() = testColorConversions(
        HCT(100.0, 50.0, 0.0) to RGB("#000000"),
        HCT(100.0, 50.0, 100.0) to RGB("#ffffff"),
        tolerance = 1e-4,
        testInverse = false,
    )
}

// hue chroma tone -> srgb hex, generated with material-color-utilities' HctSolver.java
private const val SOLVER_VECTORS = """
0.0 4.0 10.0 201a1b
0.0 4.0 35.0 585052
0.0 4.0 50.0 7e7576
0.0 4.0 75.0 c1b6b8
0.0 4.0 90.0 ebe0e1
0.0 24.0 10.0 31101c
0.0 24.0 35.0 704653
0.0 24.0 50.0 996a78
0.0 24.0 75.0 e1aab9
0.0 24.0 90.0 ffd9e2
0.0 48.0 10.0 3e001d
0.0 48.0 35.0 893455
0.0 48.0 50.0 b6587a
0.0 48.0 75.0 ff9bbb
0.0 48.0 90.0 ffd9e2
0.0 90.0 10.0 3e001d
0.0 90.0 35.0 a30056
0.0 90.0 50.0 e11e7c
0.0 90.0 75.0 ff9bbb
0.0 90.0 90.0 ffd9e2
0.0 150.0 10.0 3e001d
0.0 150.0 35.0 a30056
0.0 150.0 50.0 e7007d
0.0 150.0 75.0 ff9bbb
0.0 150.0 90.0 ffd9e2
45.0 4.0 10.0 201a18
45.0 4.0 35.0 59514d
45.0 4.0 50.0 7f7571
45.0 4.0 75.0 c2b6b2
45.0 4.0 90.0 ede0db
45.0 24.0 10.0 311303
45.0 24.0 35.0 734833
45.0 24.0 50.0 9c6c54
45.0 24.0 75.0 e5ac91
45.0 24.0 90.0 ffdbcb
45.0 48.0 10.0 341100
45.0 48.0 35.0 893b0b
45.0 48.0 50.0 b75f2d
45.0 48.0 75.0 ffa273
45.0 48.0 90.0 ffdbcb
45.0 90.0 10.0 341100
45.0 90.0 35.0 8c3900
45.0 90.0 50.0 c75400
45.0 90.0 75.0 ffa273
45.0 90.0 90.0 ffdbcb
45.0 150.0 10.0 341100
45.0 150.0 35.0 8c3900
45.0 150.0 50.0 c75400
45.0 150.0 75.0 ffa273
45.0 150.0 90.0 ffdbcb
90.0 4.0 10.0 1e1b16
90.0 4.0 35.0 56524b
90.0 4.0 50.0 7b766f
90.0 4.0 75.0 beb8b0
90.0 4.0 90.0 e8e1d9
90.0 24.0 10.0 241a00
90.0 24.0 35.0 615123
90.0 24.0 50.0 887544
90.0 24.0 75.0 cdb780
90.0 24.0 90.0 f9e0a6
90.0 48.0 10.0 241a00
90.0 48.0 35.0 674f00
90.0 48.0 50.0 937300
90.0 48.0 75.0 deb43b
90.0 48.0 90.0 ffdf92
90.0 90.0 10.0 241a00
90.0 90.0 35.0 674f00
90.0 90.0 50.0 937300
90.0 90.0 75.0 e3b200
90.0 90.0 90.0 ffdf92
90.0 150.0 10.0 241a00
90.0 150.0 35.0 674f00
90.0 150.0 50.0 937300
90.0 150.0 75.0 e3b200
90.0 150.0 90.0 ffdf92
135.0 4.0 10.0 1a1c18
135.0 4.0 35.0 52534e
135.0 4.0 50.0 777872
135.0 4.0 75.0 b9b9b2
135.0 4.0 90.0 e3e3dc
135.0 24.0 10.0 102004
135.0 24.0 35.0 455835
135.0 24.0 50.0 697d57
135.0 24.0 75.0 aac094
135.0 24.0 90.0 d3eabc
135.0 48.0 10.0 0c2000
135.0 48.0 35.0 335d0d
135.0 48.0 50.0 568331
135.0 48.0 75.0 95c76c
135.0 48.0 90.0 bef291
135.0 90.0 10.0 0c2000
135.0 90.0 35.0 2e5d00
135.0 90.0 50.0 458600
135.0 90.0 75.0 6ed000
135.0 90.0 90.0 90fd2e
135.0 150.0 10.0 0c2000
135.0 150.0 35.0 2e5d00
135.0 150.0 50.0 458600
135.0 150.0 75.0 6ed000
135.0 150.0 90.0 88fe00
180.0 4.0 10.0 191c1b
180.0 4.0 35.0 505351
180.0 4.0 50.0 747876
180.0 4.0 75.0 b6b9b7
180.0 4.0 90.0 e0e3e1
180.0 24.0 10.0 00201a
180.0 24.0 35.0 2e5a50
180.0 24.0 50.0 537f74
180.0 24.0 75.0 94c2b6
180.0 24.0 90.0 bdecdf
180.0 48.0 10.0 00201a
180.0 48.0 35.0 005e4f
180.0 48.0 50.0 008673
180.0 48.0 75.0 48cdb3
180.0 48.0 90.0 78f8dd
180.0 90.0 10.0 00201a
180.0 90.0 35.0 005e4f
180.0 90.0 50.0 008673
180.0 90.0 75.0 00d0b3
180.0 90.0 90.0 00fedb
180.0 150.0 10.0 00201a
180.0 150.0 35.0 005e4f
180.0 150.0 50.0 008673
180.0 150.0 75.0 00d0b3
180.0 150.0 90.0 00fedb
225.0 4.0 10.0 191c1d
225.0 4.0 35.0 505354
225.0 4.0 50.0 757779
225.0 4.0 75.0 b7b9bb
225.0 4.0 90.0 e1e3e4
225.0 24.0 10.0 001f29
225.0 24.0 35.0 305766
225.0 24.0 50.0 567d8c
225.0 24.0 75.0 97bfd0
225.0 24.0 90.0 c0e9fb
225.0 48.0 10.0 001f29
225.0 48.0 35.0 005a71
225.0 48.0 50.0 0082a1
225.0 48.0 75.0 4dc6ee
225.0 48.0 90.0 b9eaff
225.0 90.0 10.0 001f29
225.0 90.0 35.0 005a71
225.0 90.0 50.0 0082a1
225.0 90.0 75.0 00c8f8
225.0 90.0 90.0 b9eaff
225.0 150.0 10.0 001f29
225.0 150.0 35.0 005a71
225.0 150.0 50.0 0082a1
225.0 150.0 75.0 00c8f8
225.0 150.0 90.0 b9eaff
270.0 4.0 10.0 1b1b1f
270.0 4.0 35.0 525256
270.0 4.0 50.0 77777a
270.0 4.0 75.0 b9b8bc
270.0 4.0 90.0 e4e2e6
270.0 24.0 10.0 0e1a37
270.0 24.0 35.0 475271
270.0 24.0 50.0 6b7697
270.0 24.0 75.0 adb8dc
270.0 24.0 90.0 dae2ff
270.0 48.0 10.0 001847
270.0 48.0 35.0 2c4f9c
270.0 48.0 50.0 5474c4
270.0 48.0 75.0 9db7ff
270.0 48.0 90.0 dae2ff
270.0 90.0 10.0 001847
270.0 90.0 35.0 004bb8
270.0 90.0 50.0 126eff
270.0 90.0 75.0 9db7ff
270.0 90.0 90.0 dae2ff
270.0 150.0 10.0 001847
270.0 150.0 35.0 004bb8
270.0 150.0 50.0 126eff
270.0 150.0 75.0 9db7ff
270.0 150.0 90.0 dae2ff
315.0 4.0 10.0 1d1b1e
315.0 4.0 35.0 555155
315.0 4.0 50.0 7a767a
315.0 4.0 75.0 bdb7bb
315.0 4.0 90.0 e7e0e5
315.0 24.0 10.0 251431
315.0 24.0 35.0 5f4b6b
315.0 24.0 50.0 856f92
315.0 24.0 75.0 c9b0d6
315.0 24.0 90.0 f3daff
315.0 48.0 10.0 2f004c
315.0 48.0 35.0 6c3e8c
315.0 48.0 50.0 9363b4
315.0 48.0 75.0 d9a4fb
315.0 48.0 90.0 f3daff
315.0 90.0 10.0 2f004c
315.0 90.0 35.0 7f00c3
315.0 90.0 50.0 ae31ff
315.0 90.0 75.0 dba3ff
315.0 90.0 90.0 f3daff
315.0 150.0 10.0 2f004c
315.0 150.0 35.0 7f00c3
315.0 150.0 50.0 ae31ff
315.0 150.0 75.0 dba3ff
315.0 150.0 90.0 f3daff
"""
