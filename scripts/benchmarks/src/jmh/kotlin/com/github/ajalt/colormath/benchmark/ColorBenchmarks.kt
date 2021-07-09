package com.github.ajalt.colormath.benchmark

import com.github.ajalt.colormath.LCH
import com.github.ajalt.colormath.LinearRGB
import com.github.ajalt.colormath.RGB
import com.github.ajalt.colormath.transform.interpolator
import com.github.ajalt.colormath.transform.sequence
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

private val interpolator = RGB.interpolator(RGB("#000a"), RGB("#fffa"), premultiplyAlpha = false)
private val interpolatorPrumult = RGB.interpolator(RGB("#000a"), RGB("#fffa"))


@Warmup(iterations = 2, time = 1)
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
    @OperationsPerInvocation(10)
    open fun rgbToLch(): LCH {
        return RGB(0.3f, 0.4f, 0.6f).toLCH()
    }

    @Benchmark
    @OperationsPerInvocation(10)
    open fun rgbToLinear(): LinearRGB {
        return RGB(0.3f, 0.4f, 0.6f).toLinearRGB()
    }
}