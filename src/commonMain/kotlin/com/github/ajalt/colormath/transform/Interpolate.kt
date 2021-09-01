package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.transform.InterpolationMethod.Point

/**
 * Interpolate linearly between this color and [other].
 *
 * @param other the color to interpolate to. It will be converted to this color's space if it isn't already.
 * @param t The amount to interpolate, with 0 returning this color, and 1 returning [other]
 * @param premultiplyAlpha If true, multiply each color's components be the color's alpha value
 *   before interpolating, and divide the result by its alpha.
 * @param hueAdjustment How to interpolate the hue component, if there is one.
 */
fun <T : Color> T.interpolate(
    other: Color,
    t: Float,
    premultiplyAlpha: Boolean = true,
    hueAdjustment: ComponentAdjustment = HueAdjustments.shorter,
): T = map { space, components ->
    val l = mult(space, premultiplyAlpha, components)
    val r = mult(space, premultiplyAlpha, space.convert(other).toArray())
    fixupHues(space, hueAdjustment, listOf(Stop(l, 0f), Stop(r, 1f)))
    interpolateComponents(l, r, FloatArray(components.size), t, premultiplyAlpha, space)
}

/**
 * Build an interpolator that will produce colors in this color space.
 */
fun <T : Color> ColorSpace<T>.interpolator(builder: InterpolatorBuilder.() -> Unit): Interpolator<T> {
    return InterpolatorBuilderImpl(this).apply(builder).build()
}

/**
 * Build a linear interpolator with two or more evenly spaced [stops].
 *
 * @param premultiplyAlpha If true, multiply each color's components be the color's alpha value
 *   before interpolating, and divide the result by its alpha.
 */
fun <T : Color> ColorSpace<T>.interpolator(
    vararg stops: Color,
    premultiplyAlpha: Boolean = true,
): Interpolator<T> {
    require(stops.size > 1) { "interpolators require at least two stops" }
    return interpolator {
        this.premultiplyAlpha = premultiplyAlpha
        stops.forEach { stop(it) }
    }
}

/**
 * An interpolator between two or more colors.
 *
 * Build an instance of this class with [interpolator].
 */
interface Interpolator<T : Color> {
    fun interpolate(t: Float): T
    fun interpolate(t: Double): T = interpolate(t.toFloat())
}


/**
 * A function that takes a list of values for a single component and returns a new list with the
 * value to use for interpolation of that component.
 *
 * The returned list must be the same size as the input.
 */
typealias ComponentAdjustment = (hues: List<Float>) -> List<Float>


interface InterpolatorBuilder {
    /** Add a stop with a default position */
    fun stop(color: Color)

    /** Add a stop with a given [position] */
    fun stop(color: Color, position: Float)

    /** Add a stop with a given [position] */
    fun stop(color: Color, position: Double) = stop(color, position.toFloat())

    /**
     * Add stops at [position1] and [position2] with the same color.
     *
     * This can be useful for creating a solid color stripe.
     */
    fun stop(color: Color, position1: Float, position2: Float)

    /**
     * Add stops at [position1] and [position2] with the same color.
     *
     * This can be useful for creating a solid color stripe.
     */
    fun stop(color: Color, position1: Double, position2: Double) = stop(color, position1.toFloat(), position2.toFloat())

    /** Set the midpoint of the gradient between the previous and next stop */
    fun hint(position: Float)

    /** Set the midpoint of the gradient between the previous and next stop */
    fun hint(position: Double) = hint(position.toFloat())

    /**
     * If true, multiply each color's components be the color's alpha value before interpolating,
     * and divide the result by its alpha.
     */
    var premultiplyAlpha: Boolean

    /**
     * The interpolation method to use. Linear by default.
     */
    var method: InterpolationMethod

    /**
     * Add an [adjustment] to a [component] with a given name.
     *
     * By default, two adjustments are added:
     *
     * - alpha: if an alpha components are not `NaN`, all `NaN` values are replaced with 1
     * - hue: for [polar][ColorComponentInfo.isPolar] components, [HueAdjustments.shorter] is applied
     *
     * ## Example
     *
     * Overriding the default hue adjustment, and disabling the alpha adjustment:
     *
     * ```
     * HSV.interpolator {
     *     componentAdjustment("h", HueAdjustments.longer)
     *     componentAdjustment("alpha") { it }
     * }
     * ```
     */
    fun componentAdjustment(component: String, adjustment: ComponentAdjustment)
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
    private val lerps: List<InterpolationMethod.ComponentInterpolator>,
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

    private val adjustments = mutableMapOf("alpha" to alphaAdjustment)

    init {
        space.components.filter { it.isPolar }.forEach {
            adjustments[it.name.lowercase()] = HueAdjustments.shorter
        }
    }

    override var method: InterpolationMethod = InterpolationMethods.linear()

    override fun componentAdjustment(component: String, adjustment: ComponentAdjustment) {
        require(space.components.any { it.name.equals(component, ignoreCase = true) }) {
            "Unknown component name \"$component\" for color model ${space.name}. " +
                    "Valid names are ${space.components.map { it.name }}"
        }
        adjustments[component.lowercase()] = adjustment
    }

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
        applyAdjustments(out)
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
            entries[i] = stop.copy(color = prev.interpolate(next, 0.5f, premultiplyAlpha))
        }
    }

    // Convert all colors to this model, premultiply alphas if necessary, and convert to arrays
    private fun bakeComponents(): List<Stop> {
        // precondition: all entries have colors and positions
        return entries.map { (c, p) ->
            Stop(mult(space, premultiplyAlpha, space.convert(c!!).toArray()), p!!)
        }
    }

    private fun applyAdjustments(entries: List<Stop>) {
        for ((i, component) in space.components.withIndex()) {
            val adj = adjustments[component.name.lowercase()] ?: continue
            adj(entries.map { it.components[i] }).forEachIndexed { j, new ->
                entries[j].components[i] = new
            }
        }
    }
}

private fun fixupHues(space: ColorSpace<*>, hueAdjustment: ComponentAdjustment, entries: List<Stop>) {
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

private val alphaAdjustment: ComponentAdjustment = { l ->
    when {
        l.all { it.isNaN() } -> l
        else -> l.map { if (it.isNaN()) 1f else it }
    }
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
