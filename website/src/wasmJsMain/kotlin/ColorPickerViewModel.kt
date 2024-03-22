import androidx.compose.runtime.*
import com.github.ajalt.colormath.*
import com.github.ajalt.colormath.model.*
import com.github.ajalt.colormath.model.RGBColorSpaces.ACES
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScc
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScct
import com.github.ajalt.colormath.model.RGBColorSpaces.ACEScg
import com.github.ajalt.colormath.model.RGBColorSpaces.AdobeRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.BT2020
import com.github.ajalt.colormath.model.RGBColorSpaces.BT709
import com.github.ajalt.colormath.model.RGBColorSpaces.DCI_P3
import com.github.ajalt.colormath.model.RGBColorSpaces.DisplayP3
import com.github.ajalt.colormath.model.RGBColorSpaces.LinearSRGB
import com.github.ajalt.colormath.model.RGBColorSpaces.ROMM_RGB
import com.github.ajalt.colormath.transform.*
import kotlin.math.roundToInt
import kotlin.random.Random

const val STEPS_DEFAULT = 8
const val STEPS_MIN = 2
const val STEPS_MAX = 10
const val GRADIENT_COUNT_MAX = 10

sealed class Stops {
    data class Endpoints(override val start: Color, override val end: Color) : Stops()
    data class Custom(val stops: List<Color>) : Stops() {
        init {
            require(stops.size >= 2) { "Gradients must have at least two stops" }
        }

        override val start: Color get() = stops.first()
        override val end: Color get() = stops.last()
    }

    abstract val start: Color
    abstract val end: Color
}

private fun Stops.copyStops(stepCount: Int, keepEndpoints: Boolean = true): MutableList<Color> {
    return when (this) {
        is Stops.Endpoints -> {
            if (stepCount == 2 || keepEndpoints) mutableListOf(start, end)
            else start.space.interpolator(start, end).sequence(stepCount).toMutableList()
        }

        is Stops.Custom -> {
            when {
                stepCount == stops.size -> stops.toMutableList()
                stepCount < stops.size -> (stops.take(stepCount - 1) + stops.last()).toMutableList()
                else -> {
                    val interpolator = stops.first().space.interpolator {
                        stops.forEach { stop(it) }
                    }
                    interpolator.sequence(stepCount).toMutableList()
                }
            }
        }
    }
}

private fun Stops(stops: List<Color>): Stops {
    return if (stops.size == 2) Stops.Endpoints(stops[0], stops[1])
    else Stops.Custom(stops)
}

data class Gradient(
    val stops: Stops,
    val easingFnName: String = "linear",
    val splineInterp: Boolean = false,
)

val Gradient.start: Color get() = stops.start
val Gradient.end: Color get() = stops.end
val Gradient.colorSpace: ColorSpace<*> get() = start.space
val Gradient.hasCustomStops: Boolean get() = stops is Stops.Custom

fun Gradient.interpolator(): Interpolator<out Color> {
    return colorSpace.interpolator {
        when (stops) {
            is Stops.Endpoints -> {
                stop(stops.start)
                stop(stops.end)
            }

            is Stops.Custom -> stops.stops.forEach { stop(it) }
        }
        method = when {
            splineInterp -> InterpolationMethods.monotoneSpline(true)
            else -> InterpolationMethods.linear()
        }
        easing = easingFunctions[easingFnName] ?: EasingFunctions.linear()
    }
}

@Stable
class ColorPickerViewModel {
    private val _gradients = mutableStateListOf<Gradient>()
    val gradients: List<Gradient> get() = _gradients

    var gradientIndex: Int by mutableStateOf(0)
        private set
    var stepIndex: Int? by mutableStateOf(null)
        private set
    var stepSliderValue: Float by mutableStateOf(STEPS_DEFAULT.toFloat())
        private set
    var overlayHexCode: Boolean by mutableStateOf(true)
    var overlayContrast: Boolean by mutableStateOf(false)
    var showContinuousGradients: Boolean by mutableStateOf(false)
    var menuExpanded: Boolean by mutableStateOf(false)
    private var menuIndex: Int by mutableStateOf(0)
    var menuText: String by mutableStateOf("")
        private set
    var menuOptions: List<String> by mutableStateOf(emptyList())
        private set
    var pickerHueOffsetFraction: Float by mutableStateOf(0f)
        private set
    var pickerSatOffsetFraction: Float by mutableStateOf(0f)
        private set
    var pickerLightOffsetFraction: Float by mutableStateOf(0f)
        private set

    var selectedGradient: Gradient
        get() = _gradients[gradientIndex]
        private set(value) {
            _gradients[gradientIndex] = value
        }
    var gradientSpace: ColorSpace<*>
        get() = selectedGradient.colorSpace
        set(value) {
            selectedGradient = selectedGradient.copy(
                stops = Stops(
                    selectedGradient.stops.copyStops(stepCount).map { it.convertTo(value) }
                ),
            )
        }
    val selectedColor: Color
        get() {
            val i = stepIndex ?: 0
            return when (val stops = selectedGradient.stops) {
                is Stops.Custom -> stops.stops[i]
                is Stops.Endpoints -> {
                    selectedGradient.interpolator().interpolate(i.toDouble() / (stepCount - 1))
                }
            }
        }
    val selectedHue: Float
        get() = pickerHueOffsetFraction * 360
    val stepCount: Int
        get() = stepSliderValue.roundToInt().coerceIn(STEPS_MIN, STEPS_MAX)

    val gradientSpaces = listOf(RGB, HSL, LAB, LCHab, LUV, LCHuv, Oklab, Oklch, JzAzBz)
    val easingFunctionNames = easingFunctions.keys.toList()

    init {
        showGeneratePalette()
        selectStep(0, null)
    }

    fun setColorMenuIndex(index: Int) {
        menuIndex = index.coerceIn(0, menuOptions.lastIndex)
        menuText = menuOptions[index]
    }

    fun colorTextIsError(): Boolean {
        return parseColorOrNull(menuText) == null
    }

    fun addGradient() {
        if (_gradients.size < GRADIENT_COUNT_MAX) _gradients.add(randGradient())
    }

    fun removeGradient(index: Int) {
        _gradients.removeAt(index)
        if (gradientIndex >= index) selectGradient((gradientIndex - 1).coerceAtLeast(0))
    }

    private fun selectGradient(index: Int) {
        require(index in _gradients.indices) { "Invalid gradient index" }
        gradientIndex = index
        refreshCurrentColor()
    }

    fun selectStep(gradientIndex: Int, stepIndex: Int?) {
        this.stepIndex = stepIndex
        selectGradient(gradientIndex)
    }

    fun setStepSliderValue(value: Float) {
        val old = stepCount
        stepSliderValue = value
        val c = stepCount
        if (c == old) return
        for ((i, g) in _gradients.withIndex()) {
            if (g.stops is Stops.Endpoints) continue
            _gradients[i] = g.copy(stops = Stops(g.stops.copyStops(c)))
        }
        refreshCurrentColor()
    }

    fun changeMenuText(colorString: String) {
        menuText = colorString
        menuExpanded = false
        val newColor = parseColorOrNull(colorString) ?: return
        changeColor(newColor, skipMenu = true)
    }

    private fun changeColor(
        color: Color,
        skipMenu: Boolean = false,
        skipPicker: Boolean = false,
    ) {
        val i = stepIndex ?: 0
        val newStops = selectedGradient.stops.copyStops(
            stepCount,
            keepEndpoints = i == 0 || i == stepCount - 1
        )
        newStops[i] = color
        selectedGradient = selectedGradient.copy(stops = Stops(newStops))
        refreshCurrentColor(skipMenu, skipPicker)
    }

    private fun refreshCurrentColor(
        skipMenu: Boolean = false,
        skipPicker: Boolean = false,
    ) {
        val color = selectedColor
        if (!skipMenu) {
            menuText = colorToString(color)
            menuExpanded = false
            menuOptions = menuSpacesToString.map { it(selectedColor) }
            setColorMenuIndex(menuIndex) // update menu text
        }
        if (!skipPicker) {
            val h = color.toHSL().hueOr(0f)
            pickerHueOffsetFraction = (h / 360f).coerceIn(0f, 1f)
            pickerSatOffsetFraction = color.toHSL().s.coerceIn(0f, 1f)
            pickerLightOffsetFraction = 1f - color.toHSL().l.coerceIn(0f, 1f)
        }
    }

    fun setSplineInterp(spline: Boolean) {
        selectedGradient = selectedGradient.copy(splineInterp = spline)
    }

    fun setEasingFunction(easing: String) {
        selectedGradient = selectedGradient.copy(easingFnName = easing)
    }

    fun showCompareColorSpaces() {
        val start = RGB("#eef")
        val end = RGB("#33d")
        val newGradients = listOf(RGB, LAB, LUV, Oklab, JzAzBz).map {
            Gradient(Stops.Endpoints(start.convertTo(it), end.convertTo(it)))
        }
        _gradients.clear()
        _gradients.addAll(newGradients)
        stepSliderValue = 2f
        overlayContrast = false
        overlayHexCode = false
        showContinuousGradients = true
        selectStep(0, null)
    }

    fun showGeneratePalette() {
        val startH = Random.nextInt(360)
        _gradients.clear()
        _gradients.addAll(
            listOf(
                randGradient(startH),
                randGradient(startH + 120),
                randGradient(startH + 240),
            )
        )
        stepSliderValue = STEPS_DEFAULT.toFloat()
        overlayContrast = false
        overlayHexCode = true
        showContinuousGradients = false
        selectStep(0, null)
    }

    fun colorToString(color: Color): String {
        return color.toSRGB().toHex().uppercase()
    }

    fun changePickerHueOffsetFraction(value: Float) {
        pickerHueOffsetFraction = value.coerceIn(0f, 1f)
        val hsl = selectedColor.toHSL()
        val color = HSL(selectedHue, hsl.s, hsl.l).convertTo(selectedColor.space)
        changeColor(color, skipPicker = true)
    }

    fun changePickerSatLightOffsetFractions(saturation: Float, lightness: Float) {
        pickerSatOffsetFraction = saturation.coerceIn(0f, 1f)
        pickerLightOffsetFraction = lightness.coerceIn(0f, 1f)
        val h = selectedHue
        val s = pickerSatOffsetFraction
        val l = 1f - pickerLightOffsetFraction
        val color = HSL(h, s, l).convertTo(selectedColor.space)
        changeColor(color, skipPicker = true)
    }

    private fun parseColorOrNull(colorString: String): Color? {
        var s = colorString.trim()
        if ('(' in s && !s.endsWith(')')) s = "$s)"
        return try {
            Color.parse(s)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

private val menuSpacesToString = listOf<(Color) -> String>(
    { it.toSRGB().toHex() },
    { c -> c.toSRGB().toRGBInt().let { "rgb(${it.r} ${it.g} ${it.b})" } },
    { it.toHSL().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toHWB().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toLAB().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toLCHab().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toOklab().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toOklch().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toHSV().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toLUV().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toLCHuv().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toJzAzBz().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toJzCzHz().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toXYZ().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toHSLuv().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toHPLuv().formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(LinearSRGB).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(ACES).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(ACEScc).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(ACEScct).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(ACEScg).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(AdobeRGB).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(BT2020).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(BT709).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(DCI_P3).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(DisplayP3).formatCssString(customColorSpaces = customColorSpaces) },
    { it.convertTo(ROMM_RGB).formatCssString(customColorSpaces = customColorSpaces) },
    { it.toCMYK().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toAnsi16().formatCssString(customColorSpaces = customColorSpaces) },
    { it.toAnsi256().formatCssString(customColorSpaces = customColorSpaces) },
)
private val customColorSpaces = listOf(
    "hsv" to HSV,
    "luv" to LUV,
    "lch-uv" to LCHuv,
    "jzazbz" to JzAzBz,
    "jzczhz" to JzCzHz,
    "hsluv" to HSLuv,
    "hpluv" to HPLuv,
    "aces" to ACES,
    "aces-cc" to ACEScc,
    "aces-cct" to ACEScct,
    "aces-cg" to ACEScg,
    "bt709" to BT709,
    "dci-p3" to DCI_P3,
    "cmyk" to CMYK,
    "ansi16" to Ansi16,
    "ansi256" to Ansi256,
)

private val easingFunctions = mapOf(
    "linear" to EasingFunctions.linear(),
    "ease" to EasingFunctions.ease(),
    "ease-in" to EasingFunctions.easeIn(),
    "ease-in-out" to EasingFunctions.easeInOut(),
    "ease-out" to EasingFunctions.easeOut(),
    "midpoint 25%" to EasingFunctions.midpoint(.25),
    "midpoint 75%" to EasingFunctions.midpoint(.75),
)

private fun randf(a: Int, b: Int): Double = Random.nextDouble(a.toDouble(), b.toDouble())
private fun randGradient(h: Int = Random.nextInt(360)): Gradient = Gradient(
    Stops.Endpoints(
        LCHab(95, randf(5, 10), h).toSRGB().clamp().toOklab(),
        LCHab(5, randf(70, 90), h).toSRGB().clamp().toOklab(),
    )
)

