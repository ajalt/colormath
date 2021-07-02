package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorModel

fun <T : Color> T.interpolate(other: Color, amount: Float, premultiplyAlpha: Boolean = true): T =
    transform { model, components ->
        val lmult = mult(model, premultiplyAlpha, components)
        val rmult = mult(model, premultiplyAlpha,  model.convert(other).toArray())
        interpolateComponents(lmult, rmult, amount, premultiplyAlpha, model)
    }

fun <T : Color> ColorModel<T>.interpolator(builder: InterpolatorBuilder.() -> Unit): Interpolator<T> {
    return InterpolatorBuilderImpl(this).apply(builder).build()
}

fun <T : Color> ColorModel<T>.interpolator(vararg stops: Color, premultiplyAlpha: Boolean = true): Interpolator<T> {
    require(stops.size > 1) { "interpolators require at least two stops" }
    val positioned = stops.mapIndexed { i, it -> convert(it).toArray() to (i.toFloat() / stops.lastIndex) }
    return InterpolatorImpl(this, positioned, premultiplyAlpha)
}

interface Interpolator<T : Color> {
    fun interpolate(position: Float): T
    fun interpolate(position: Double): T = interpolate(position.toFloat())
}

interface InterpolatorBuilder {
    fun stop(color: Color)
    fun stop(color: Color, position: Float)
    fun stop(color: Color, position: Double) = stop(color, position.toFloat())
    fun stop(color: Color, position1: Float, position2: Float)
    fun stop(color: Color, position1: Double, position2: Double) = stop(color, position1.toFloat(), position2.toFloat())
    fun hint(position: Float)
    fun hint(position: Double) = hint(position.toFloat())
    var premultiplyAlpha: Boolean
}

/** Create a sequence of [length] colors evenly spaced along this interpolator's values */
fun <T : Color> Interpolator<T>.sequence(length: Int): Sequence<T> {
    require(length > 1) { "length must be 2 or greater, was $length" }
    return (0 until length).asSequence().map { interpolate(it / (length - 1).toFloat()) }
}


//region: implementations

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
        return interpolateComponents(lc, rc, (pos - lp) / (rp - lp), premultiplyAlpha, model)
    }
}

private class InterpolatorBuilderImpl<T : Color>(private val model: ColorModel<T>) : InterpolatorBuilder {
    override var premultiplyAlpha: Boolean = true

    // stops may omit position, never color
    // hints omit color, never position
    private data class Entry(val color: Color?, val pos: Float?) {
        val isHint get() = color == null
        val isStop get() = color != null
    }

    private val entries = mutableListOf<Entry>()


    override fun stop(color: Color) {
        entries += Entry(color, null)
    }

    override fun stop(color: Color, position: Float) {
        entries += Entry(color, position)
    }

    override fun stop(color: Color, position1: Float, position2: Float) {
        entries += Entry(color, position1)
        entries += Entry(color, position2)
    }

    override fun hint(position: Float) {
        check(entries.isNotEmpty() && entries.last().isStop) { "Hints must be placed between two color stops" }
        entries += Entry(null, position)
    }

    fun build(): Interpolator<T> {
        require(entries.count { it.isStop } >= 2) { "At least two color stops are required" }
        require(entries.last().isStop) { "Must have a color stop after the last hint" }
        // https://www.w3.org/TR/css-images-4/#color-stop-syntax
        fixupEndpoints()
        fixupDecreasingPos()
        fixupMissingPos()
        fixupHints()

        return InterpolatorImpl(model, bakeComponents(), premultiplyAlpha)
    }

    // step 1
    private fun fixupEndpoints() {
        entries[0] = entries[0].copy(pos = entries[0].pos ?: 0f)
        entries[entries.lastIndex] = entries.last().copy(pos = entries.last().pos ?: 1f)
    }

    // step 2
    private fun fixupDecreasingPos() {
        var pos = 0f
        for ((i, entry) in entries.withIndex()) {
            val p = entry.pos ?: continue
            if (p < pos) entries[i] = entry.copy(pos = pos)
            pos = p
        }
    }

    // step 3
    // precondition: endpoints have positions
    private fun fixupMissingPos() {
        var runStart = -1
        var runLen = 0
        for ((i, entry) in entries.withIndex()) {
            if (entry.pos == null) {
                if (runStart < 0) {
                    runStart = i
                }
                runLen += 1
            } else if (entry.isStop && runLen > 0) {
                val prevPos = entries[runStart - 1].pos!!
                val nextPos = entry.pos
                var fixed = 0
                for (j in runStart until i) {
                    if (entries[j].isHint) continue
                    entries[j] = entries[j].copy(pos = lerp(prevPos, nextPos, (1 + fixed).toFloat() / (1 + runLen)))
                    fixed += 1
                }

                runStart = -1
                runLen = 0
            }
        }
    }

    private fun fixupHints() {
        // precondition: there is always a regular stop before and after every hint
        // precondition: all stops have positions
        for ((i, stop) in entries.withIndex()) {
            if (stop.isStop) continue
            val prev = entries[i - 1].color!!
            val next = entries[i + 1].color!!
            entries[i] = stop.copy(color = prev.interpolate(next, 0.5f, premultiplyAlpha))
        }
    }

    // Convert all colors to this model, premultiply alphas if necessary, and convert to arrays
    private fun bakeComponents(): List<Pair<FloatArray, Float>> {
        // precondition: all entries have colors and positions
        return entries.map { (c, p) ->
            mult(model, premultiplyAlpha, model.convert(c!!).toArray()) to p!!
        }
    }
}

private fun interpolateComponents(
    l: FloatArray,
    r: FloatArray,
    amount: Float,
    divideAlpha: Boolean,
    model: ColorModel<*>,
): FloatArray {
    return div(model, divideAlpha, FloatArray(l.size) {
        lerp(l[it], r[it], amount)
    })
}

private fun mult(model: ColorModel<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    if (premultiplyAlpha) multiplyAlphaInPlace(model, components)
    return components
}

private fun div(model: ColorModel<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    if (premultiplyAlpha) divideAlphaInPlace(model, components)
    return components
}

private fun lerp(l: Float, r: Float, amount: Float): Float = l + amount * (r - l)
//endregion
