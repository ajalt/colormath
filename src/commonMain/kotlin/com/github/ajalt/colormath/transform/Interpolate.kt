package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.internal.nanToOne

fun <T : Color> T.interpolate(
    other: Color,
    t: Float,
    premultiplyAlpha: Boolean = true,
    hueAdjustment: HueAdjustment = HueAdjustments.shorter,
): T = map { space, components ->
    val l = mult(space, premultiplyAlpha, components)
    val r = mult(space, premultiplyAlpha, space.convert(other).toArray())
    fixupHues(space, hueAdjustment, listOf(Stop(l, 0f), Stop(r, 1f)))
    interpolateComponents(l, r, FloatArray(components.size), t, premultiplyAlpha, space)
}

fun <T : Color> ColorSpace<T>.interpolator(builder: InterpolatorBuilder.() -> Unit): Interpolator<T> {
    return InterpolatorBuilderImpl(this).apply(builder).build()
}

fun <T : Color> ColorSpace<T>.interpolator(vararg stops: Color, premultiplyAlpha: Boolean = true): Interpolator<T> {
    require(stops.size > 1) { "interpolators require at least two stops" }
    val positioned = stops.mapIndexed { i, it -> Stop(convert(it).toArray(), (i.toFloat() / stops.lastIndex)) }
    val lerps = components.mapIndexed { i, _ ->
        InterpolationMethods.linear().build(positioned.map { Point(it.pos, it.components[i]) })
    }
    return InterpolatorImpl(lerps, this, premultiplyAlpha)
}

interface Interpolator<T : Color> {
    fun interpolate(t: Float): T
    fun interpolate(t: Double): T = interpolate(t.toFloat())
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

    /**
     * An optional adjustment to the hue components of the colors, if there is one, or `null` to
     * leave hue components unchanged.
     *
     * Defaults to [HueAdjustments.shorter].
     */
    var hueAdjustment: HueAdjustment

    /**
     * A function too apply to all colors' alpha values prior to interpolation.
     *
     * By default, this will replace all [NaN][Float.NaN] values with `1`.
     */
    var alphaFixup: (Float) -> Float

    /**
     * The interpolation method to use. Linear by default.
     */
    var method: InterpolationMethod
}

/** Create a sequence of [length] colors evenly spaced along this interpolator's values */
fun <T : Color> Interpolator<T>.sequence(length: Int): Sequence<T> {
    require(length > 1) { "length must be 2 or greater, was $length" }
    return (0 until length).asSequence().map { interpolate(it / (length - 1).toFloat()) }
}


//region: implementations

@Suppress("ArrayInDataClass")
private data class Stop(val components: FloatArray, val pos: Float)

private class InterpolatorImpl<T : Color>(
    private val lerps: List<InterpolationMethod.ChannelInterpolator>,
    private val space: ColorSpace<T>,
    private val premultiplyAlpha: Boolean,
) : Interpolator<T> {
    init {
        require(lerps.size == space.components.size)
    }

    private val out = FloatArray(space.components.size)

    override fun interpolate(t: Float): T {
        for (i in out.indices) {
            out[i] = lerps[i].interpolate(t)
        }
        div(space, premultiplyAlpha, out)
        return space.create(out)
    }
}

private class InterpolatorBuilderImpl<T : Color>(private val space: ColorSpace<T>) : InterpolatorBuilder {
    override var premultiplyAlpha: Boolean = true
    override var hueAdjustment: HueAdjustment = HueAdjustments.shorter
    override var alphaFixup: (Float) -> Float = { it.nanToOne() }

    override var method: InterpolationMethod = InterpolationMethods.linear()

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

        val out = bakeComponents()
        fixupAlpha(out)
        fixupHues(space, hueAdjustment, out)
        val lerps = space.components.mapIndexed { i, _ ->
            method.build(out.map { Point(it.pos, it.components[i]) })
        }
        return InterpolatorImpl(lerps, space, premultiplyAlpha)
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
            entries[i] = stop.copy(color = prev.interpolate(next, 0.5f, premultiplyAlpha, hueAdjustment))
        }
    }

    // Convert all colors to this model, premultiply alphas if necessary, and convert to arrays
    private fun bakeComponents(): List<Stop> {
        // precondition: all entries have colors and positions
        return entries.map { (c, p) ->
            Stop(mult(space, premultiplyAlpha, space.convert(c!!).toArray()), p!!)
        }
    }

    private fun fixupAlpha(entries: List<Stop>) {
        for ((c, _) in entries) {
            c[c.lastIndex] = alphaFixup(c.last())
        }
    }
}

private fun fixupHues(space: ColorSpace<*>, hueAdjustment: HueAdjustment, entries: List<Stop>) {
    for ((i, c) in space.components.withIndex()) {
        if (!c.isPolar) continue
        hueAdjustment(entries.map { it.components[i] }).forEachIndexed { j, hue ->
            entries[j].components[i] = hue
        }
    }
}

private fun interpolateComponents(
    l: FloatArray,
    r: FloatArray,
    out: FloatArray,
    amount: Float,
    divideAlpha: Boolean,
    space: ColorSpace<*>,
): FloatArray {
    for (i in out.indices) {
        out[i] = lerp(l[i], r[i], amount)
    }
    return div(space, divideAlpha, out)
}

private fun mult(space: ColorSpace<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    if (premultiplyAlpha) multiplyAlphaInPlace(space, components)
    return components
}

private fun div(space: ColorSpace<*>, premultiplyAlpha: Boolean, components: FloatArray): FloatArray {
    if (premultiplyAlpha) divideAlphaInPlace(space, components)
    return components
}

private fun lerp(l: Float, r: Float, amount: Float): Float {
    return when {
        l.isNaN() -> r
        r.isNaN() -> l
        else -> l + amount * (r - l)
    }
}
//endregion
