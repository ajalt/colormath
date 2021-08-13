package com.github.ajalt.colormath.benchmark

import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.transform.createChromaticAdapter
import com.github.ajalt.colormath.transform.interpolator
import com.github.ajalt.colormath.transform.sequence
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


private val interpolator = RGB.interpolator(RGB("#000a"), RGB("#fffa"), premultiplyAlpha = false)
private val interpolatorPrumult = RGB.interpolator(RGB("#000a"), RGB("#fffa"))
private val adapter = RGBInt.createChromaticAdapter(RGBInt(200, 210, 220))
private val rgbInt = RGBInt(11, 222, 33)

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
open class ColorBenchmarks {
    @Benchmark
    open fun interpolate(): RGB {
        return interpolator.interpolate(0.5f)
    }

    @Benchmark
    open fun interpolatePremultiply(): RGB {
        return interpolatorPrumult.interpolate(0.5f)
    }

    @Benchmark
    @OperationsPerInvocation(10)
    open fun sequence(): List<RGB> {
        return interpolator.sequence(10).toList()
    }

    @Benchmark
    @OperationsPerInvocation(10)
    open fun sequencePremultiply(): List<RGB> {
        return interpolatorPrumult.sequence(10).toList()
    }

    @Benchmark
    open fun rgbToLch(): LCHab {
        return RGB(0.3f, 0.4f, 0.6f).toLCHab()
    }

    @Benchmark
    open fun rgbToLinear(): RGB {
        return RGB(0.3f, 0.4f, 0.6f).convertTo(RGBColorSpaces.LINEAR_SRGB)
    }

    @Benchmark
    open fun chromaticAdapter(): Int {
        return adapter.adapt(rgbInt).argb.toInt()
    }
}
