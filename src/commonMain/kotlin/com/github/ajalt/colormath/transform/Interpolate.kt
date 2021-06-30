package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

fun <T : Color> T.interpolate(other: Color, amount: Float, premultiplyAlpha: Boolean = true): T =
    transform { model, components ->
        interpolateComponents(components, model.convert(other).components(), amount, premultiplyAlpha, model)
    }

@Suppress("FunctionName")
fun <T : Color> Interpolator(model: ColorModel<T>, builder: InterpolatorBuilder.() -> Unit): Interpolator<T> {
    return InterpolatorBuilderImpl(model).apply(builder).build()
}

interface Interpolator<T : Color> {
    fun interpolate(position: Float): T
}

interface InterpolatorBuilder {
    fun stop(color: Color)
    fun stop(color: Color, position: Float)
    fun stop(color: Color, position1: Float, position2: Float)
    fun hint(position: Float)
    var premultiplyAlpha: Boolean
}

/** Create a sequence of [length] colors evenly spaced along this interpolator's values */
fun <T : Color> Interpolator<T>.sequence(length: Int): Sequence<T> {
    require(length > 1) { "length must be 2 or greater, was $length" }
    return (0 until length).asSequence().map { interpolate(it / (length - 1).toFloat()) }
}


private class InterpolatorImpl<T : Color>(
    private val model: ColorModel<T>,
    private val stops: List<Pair<FloatArray, Float>>,
    private val premultiplyAlpha: Boolean,
) : Interpolator<T> {
    override fun interpolate(position: Float): T {
        return model.create(lerpComponents(position))
    }

    private fun lerpComponents(pos: Float): FloatArray {
        if (pos <= 0f) return stops.first().first
        if (pos >= 1f) return stops.last().first

        val start = stops.indexOfLast { it.second <= pos }
        if (start < 0) return stops.first().first
        if (stops[start].second == pos || start == stops.lastIndex) return stops[start].first
        val end = start + 1

        val (lc, lp) = stops[start]
        val (rc, rp) = stops[end]
        return interpolateComponents(lc, rc, (rp - lp) / (pos - lp), premultiplyAlpha, model)
    }
}

private class InterpolatorBuilderImpl<T : Color>(private val model: ColorModel<T>) : InterpolatorBuilder {
    override var premultiplyAlpha: Boolean = true
    private val stops = mutableListOf<Pair<Color?, Float?>>()
    override fun stop(color: Color) {
        stops += color to null
    }

    override fun stop(color: Color, position: Float) {
        stops += color to position
    }

    override fun stop(color: Color, position1: Float, position2: Float) {
        stops += color to position1
        stops += color to position2
    }

    override fun hint(position: Float) {
        check(stops.isNotEmpty() && stops.last().first != null) { "Hints must be placed between two color stops" }
        stops += null to position
    }

    fun build(): Interpolator<T> {
        require(stops.count { it.first != null } >= 2) { "At least two color stops are required" }
        require(stops.last().first != null) { "Must have a color stop after the last hint" }
        // https://www.w3.org/TR/css-images-4/#color-stop-syntax
        fixupEndpoints()
        fixupDecreasingPos()
        fixupMissingPos()
        fixupHints()

        return InterpolatorImpl(model, stops.map { (c, p) -> model.convert(c!!).components() to p!! }, premultiplyAlpha)
    }

    // step 1
    private fun fixupEndpoints() {
        stops[0] = stops[0].copy(second = stops[0].second ?: 0f)
        stops[stops.lastIndex] = stops.last().copy(second = stops.last().second ?: 1f)
    }

    // step 2
    private fun fixupDecreasingPos() {
        var pos = 0f
        for ((i, stop) in stops.withIndex()) {
            val p = stop.second ?: continue
            if (p < pos) stops[i] = stop.copy(second = pos)
            pos = p
        }
    }

    // step 3
    // precondition: endpoints have positions
    private fun fixupMissingPos() {
        var runStart = -1
        for ((i, stop) in stops.withIndex()) {
            if (stop.second == null) {
                if (runStart < 0) {
                    runStart = i
                }
            } else if (runStart < 0) {
                val prevPos = stops[i - 1].second!!
                val nextPos = stop.second!!
                val len = i - runStart
                for (j in 0 until len) {
                    val k = runStart + j
                    stops[k] = stops[k].copy(second = lerp(prevPos, nextPos, (1 + j).toFloat() / (len + 1)))
                }
            }
        }
    }

    private fun fixupHints() {
        // precondition: there is always a regular stop before and after every hint
        // precondition: all stops have positions
        for ((i, stop) in stops.withIndex()) {
            if (stop.first != null) continue
            val prev = stops[i - 1].first!!
            val next = stops[i + 1].first!!
            stops[i] = stop.copy(first = prev.interpolate(next, 0.5f, premultiplyAlpha))
        }
    }
}

private fun interpolateComponents(
    l: FloatArray,
    r: FloatArray,
    amount: Float,
    premultiplyAlpha: Boolean,
    model: ColorModel<*>,
): FloatArray {
    val lmult = mult(model, premultiplyAlpha, l)
    val rmult = mult(model, premultiplyAlpha, r)
    return div(model, premultiplyAlpha, FloatArray(l.size) {
        lerp(lmult[it], rmult[it], amount)
    })
}

private fun mult(model: ColorModel<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) multiplyAlphaTransform(model, components) else components
}

private fun div(model: ColorModel<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    return if (premultiplyAlpha) divideAlphaTransform(model, components) else components
}

private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
