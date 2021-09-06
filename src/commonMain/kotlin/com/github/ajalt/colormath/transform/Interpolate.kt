package com.github.ajalt.colormath.transform

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.ColorComponentInfo
import com.github.ajalt.colormath.ColorSpace
import com.github.ajalt.colormath.internal.scaleRange
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


/**
 * A builder for configuring an [InterpolatorBuilder] stop.
 */
interface InterpolatorStopBuilder {
    /**
     * The easing function to use for components in this stop. Overrides any easing functions
     * configured on the [InterpolatorBuilder].
     */
    var easing: EasingFunction?

    /**
     * The easing function to use for a single [component].
     *
     * Overrides easing functions configured elsewhere.
     */
    fun componentEasing(component: String, easingFn: EasingFunction)
}

typealias InterpolatorStopBuilderContext = InterpolatorStopBuilder.() -> Unit

interface InterpolatorBuilder {
    /** Add a stop with a default position */
    fun stop(color: Color, builder: InterpolatorStopBuilderContext = {})

    /** Add a stop with a given [position] */
    fun stop(color: Color, position: Float, builder: InterpolatorStopBuilderContext = {})

    /** Add a stop with a given [position] */
    fun stop(color: Color, position: Double, builder: InterpolatorStopBuilderContext = {}) =
        stop(color, position.toFloat(), builder)

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
     * The [EasingFunction] to use for all components. Defaults to [linear][EasingFunctions.linear].
     *
     * @see EasingFunctions
     */
    var easing: EasingFunction

    /**
     * Set the easing function for a particular [component] by name. This overrides the default
     * [easing][InterpolatorBuilder.easing] for this component.
     */
    fun componentEasing(component: String, easing: EasingFunction)

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

private class InterpolatorStopBuilderImpl(private val space: ColorSpace<*>) : InterpolatorStopBuilder {
    private val fns = mutableMapOf<String, EasingFunction>()
    override var easing: EasingFunction? = null

    override fun componentEasing(component: String, easingFn: EasingFunction) {
        fns[requireComponentName(space, component)] = easingFn
    }

    fun build(easingFns: Map<String, EasingFunction>, defEasingFn: EasingFunction): List<EasingFunction> {
        return space.components.map {
            val n = it.name.lowercase()
            fns.getOrElse(n) { easingFns.getOrElse(n) { easing ?: defEasingFn } }
        }
    }
}

@Suppress("ArrayInDataClass")
private data class Stop(val components: FloatArray, val pos: Float)

private class InterpolatorImpl<T : Color>(
    private val lerps: List<InterpolationMethod.ComponentInterpolator>,
    private val easing: List<Easing>,
    private val space: ColorSpace<T>,
    private val premultiplyAlpha: Boolean,
) : Interpolator<T> {
    data class Easing(val position: Float, val fns: List<EasingFunction>)

    init {
        require(lerps.size == space.components.size)
        require(easing.all { it.fns.size == space.components.size })
    }

    private val out = FloatArray(space.components.size)

    override fun interpolate(t: Float): T {
        val li = easing.indexOfLast { it.position <= t }
        if (li < 0 || li == easing.lastIndex || easing[li].position == t) {
            lerpEdgecase(t)
        } else {
            lerpEased(li, t)
        }

        div(space, premultiplyAlpha, out)
        return space.create(out)
    }

    private fun lerpEdgecase(t: Float) {
        for (i in out.indices) {
            out[i] = lerps[i].interpolate(t)
        }
    }

    private fun lerpEased(li: Int, t: Float) {
        val lx = easing[li].position
        val rx = easing[li + 1].position

        //scale t to 0-1 for easing
        val t1 = scaleRange(lx.toDouble(), rx.toDouble(), 0.0, 1.0, t.toDouble())

        for (i in out.indices) {
            val te = easing[li].fns[i].ease(t1)

            // scale eased t back to full range for interpolator
            val tf = scaleRange(0.0, 1.0, lx.toDouble(), rx.toDouble(), te)

            out[i] = lerps[i].interpolate(tf.toFloat())
        }
    }
}

private class InterpolatorBuilderImpl<T : Color>(private val space: ColorSpace<T>) : InterpolatorBuilder {
    private data class Entry(val color: Color, val pos: Float?, val builder: InterpolatorStopBuilderImpl)

    override var premultiplyAlpha: Boolean = true
    override var method: InterpolationMethod = InterpolationMethods.linear()
    override var easing: EasingFunction = EasingFunctions.linear()

    private val entries = mutableListOf<Entry>()
    private val easingFns = mutableMapOf<String, EasingFunction>()
    private val adjustments = mutableMapOf("alpha" to alphaAdjustment).apply {
        space.components.filter { it.isPolar }.forEach {
            set(it.name.lowercase(), HueAdjustments.shorter)
        }
    }

    override fun componentAdjustment(component: String, adjustment: ComponentAdjustment) {
        adjustments[requireComponentName(space, component)] = adjustment
    }

    override fun componentEasing(component: String, easing: EasingFunction) {
        easingFns[requireComponentName(space, component)] = easing
    }

    override fun stop(color: Color, builder: InterpolatorStopBuilderContext) {
        entries += Entry(color, null, InterpolatorStopBuilderImpl(space).also(builder))
    }

    override fun stop(color: Color, position: Float, builder: InterpolatorStopBuilderContext) {
        entries += Entry(color, position, InterpolatorStopBuilderImpl(space).also(builder))
    }

    fun build(): Interpolator<T> {
        // https://www.w3.org/TR/css-images-4/#color-stop-syntax
        fixupEndpoints()
        fixupDecreasingPos()
        fixupMissingPos()

        val out = bakeComponents()
        applyAdjustments(out)
        val lerps = space.components.mapIndexed { i, _ ->
            method.build(out.map { Point(it.pos, it.components[i]) })
        }

        val fns = entries.map {
            InterpolatorImpl.Easing(it.pos!!, it.builder.build(easingFns, easing))
        }
        return InterpolatorImpl(lerps, fns, space, premultiplyAlpha)
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
            } else if (runLen > 0) {
                val prevPos = entries[runStart - 1].pos!!
                val nextPos = entry.pos
                var fixed = 0
                for (j in runStart until i) {
                    entries[j] = entries[j].copy(pos = lerp(prevPos, nextPos, (1 + fixed).toFloat() / (1 + runLen)))
                    fixed += 1
                }

                runStart = -1
                runLen = 0
            }
        }
    }

    // Convert all colors to this model, premultiply alphas if necessary, and convert to arrays
    private fun bakeComponents(): List<Stop> {
        // precondition: all entries have colors and positions
        return entries.map { (c, p) ->
            Stop(mult(space, premultiplyAlpha, space.convert(c).toArray()), p!!)
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

private fun requireComponentName(space: ColorSpace<*>, name: String): String {
    require(space.components.any { it.name.equals(name, ignoreCase = true) }) {
        "Unknown component name \"$name\" for color model ${space.name}. " +
                "Valid names are ${space.components.map { it.name }}"
    }
    return name.lowercase()
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
