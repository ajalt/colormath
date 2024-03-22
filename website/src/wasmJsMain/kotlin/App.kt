@file:Suppress("FunctionName")

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import colormath_root.website.generated.resources.Res
import colormath_root.website.generated.resources.colormath_wordmark
import com.github.ajalt.colormath.calculate.firstWithContrast
import com.github.ajalt.colormath.calculate.wcagContrastRatio
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.HSL
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.transform.sequence
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import kotlin.math.roundToInt
import com.github.ajalt.colormath.Color as ColormathColor
import org.jetbrains.skia.ColorSpace.Companion as SkiaColorSpace
import org.jetbrains.skia.Image.Companion as SkiaImage

@Composable
fun App() {
    val viewModel = remember { ColorPickerViewModel() }
    val scrollState = rememberScrollState()

    MaterialTheme(colorScheme = darkColorScheme()) {
        PermanentNavigationDrawer(
            drawerContent = {
                Box {
                    PermanentDrawerSheet(
                        Modifier.width(300.dp).verticalScroll(scrollState)
                    ) {
                        ControlPanel(viewModel)
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState),
                        style = defaultScrollbarStyle().copy(
                            hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unhoverColor = Color.Transparent,
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            content = { GradientPanel(Modifier.fillMaxSize(), viewModel) }
        )
    }
}

@Composable
private fun ColumnScope.ControlPanel(vm: ColorPickerViewModel) {
    Image(painterResource(Res.drawable.colormath_wordmark), null)
    Spacer(Modifier.height(24.dp))
    NavigationDrawerItem(
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add Gradient") },
        label = { Text("Add Gradient") },
        selected = false,
        modifier = Modifier.padding(horizontal = 12.dp),
        onClick = vm::addGradient,
    )
    NavigationDrawerItem(
        label = { Text("Generate color palettes") },
        selected = false,
        modifier = Modifier.padding(horizontal = 12.dp),
        onClick = vm::showGeneratePalette,
    )
    NavigationDrawerItem(
        label = { Text("Compare color spaces") },
        selected = false,
        modifier = Modifier.padding(horizontal = 12.dp),
        onClick = vm::showCompareColorSpaces,
    )
    HorizontalDivider(Modifier.padding(horizontal = 12.dp))
    Text(
        "Steps",
        Modifier.padding(12.dp),
        style = MaterialTheme.typography.titleMedium
    )
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Slider(
            value = vm.stepSliderValue,
            onValueChange = vm::setStepSliderValue,
            modifier = Modifier.weight(0.9f, fill = true),
            steps = STEPS_MAX - STEPS_MIN - 1,
            valueRange = STEPS_MIN.toFloat()..STEPS_MAX.toFloat(),
        )
        Text(
            vm.stepCount.toString(),
            Modifier.weight(0.1f, fill = true).align(Alignment.CenterVertically)
                .padding(start = 4.dp),
        )
    }
    CheckboxWithText("Continuous gradients", vm.showContinuousGradients) {
        vm.showContinuousGradients = it
    }
    HorizontalDivider(Modifier.padding(horizontal = 12.dp))
    FlowRow(Modifier.padding(12.dp)) {
        for (i in vm.gradients.indices) {
            GradientChip(vm, i)
        }
    }
    SatLightnessPicker(
        Modifier.padding(12.dp).align(Alignment.CenterHorizontally),
        vm,
    )
    HuePicker(
        Modifier.padding(horizontal = 12.dp).fillMaxWidth()
            .height(16.dp).align(Alignment.CenterHorizontally),
        vm,
    )
    Spacer(Modifier.height(12.dp))
    ColorMenuDropdown(Modifier.fillMaxWidth(), vm)
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(Modifier.padding(horizontal = 12.dp))
    Text(
        "Interpolation color space",
        Modifier.padding(start = 8.dp, top = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
    ControlDropdown(
        vm.gradientSpace.name,
        vm.gradientSpaces.map { it.name to { vm.gradientSpace = it } }
    )
    Text(
        "Easing function",
        Modifier.padding(start = 8.dp, top = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
    ControlDropdown(
        vm.selectedGradient.easingFnName,
        vm.easingFunctionNames.map { it to { vm.setEasingFunction(it) } }
    )
    CheckboxWithText("Spline Interpolation", vm.selectedGradient.splineInterp) {
        vm.setSplineInterp(it)
    }
    HorizontalDivider(Modifier.padding(horizontal = 12.dp))
    Text(
        "Overlay",
        Modifier.padding(start = 8.dp, top = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
    CheckboxWithText("Hex code", vm.overlayHexCode) { vm.overlayHexCode = it }
    CheckboxWithText("Contrast", vm.overlayContrast) { vm.overlayContrast = it }
}

@Composable
private fun HuePicker(
    modifier: Modifier = Modifier,
    vm: ColorPickerViewModel,
) {
    val halfCursorDp = 8.dp
    val halfCursorPx = with(LocalDensity.current) { halfCursorDp.toPx() }
    var maxPx by remember { mutableStateOf(0f) }

    Box(modifier.padding(horizontal = halfCursorDp)) {
        Box(Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .onGloballyPositioned {
                maxPx = it.size.width.toFloat()
            }
            .drawBehind {
                for (x in 0 until maxPx.toInt()) {
                    val h = x / maxPx * 360f
                    drawRect(
                        color = HSL(h, 0.8f, 0.5f).toSRGB().clamp().toComposeColor(),
                        topLeft = Offset(x.toFloat(), 0f),
                        size = Size(1f, size.height)
                    )
                }
            })
        Box(
            Modifier
                .offset {
                    IntOffset(
                        (vm.pickerHueOffsetFraction * maxPx - halfCursorPx).roundToInt(),
                        0
                    )
                }
                .size(16.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState {
                        vm.changePickerHueOffsetFraction(vm.pickerHueOffsetFraction + it / maxPx)
                    }
                )
                .background(Color.Black, CircleShape)
                .padding(1.dp)
                .background(Color.White, CircleShape)
                .padding(3.dp)
                .background(
                    HSL(vm.pickerHueOffsetFraction * 360, 0.8, 0.5).toSRGB().clamp()
                        .toComposeColor(),
                    CircleShape
                )
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SatLightnessPicker(
    modifier: Modifier = Modifier,
    vm: ColorPickerViewModel,
) {
    var maxPx by remember { mutableStateOf(0f) }
    val cursorSizePx = with(LocalDensity.current) { 16.dp.toPx() }

    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small)
                .onGloballyPositioned {
                    maxPx = it.size.width.toFloat()
                }
                .drawWithCache {
                    val imageBitmap = makeHslGradientBitmap(
                        vm.selectedHue, size.width.toInt(), size.height.toInt()
                    )
                    onDrawBehind { drawImage(imageBitmap) }
                }
        ) {
            Spacer(
                Modifier
                    .offset {
                        IntOffset(
                            (vm.pickerSatOffsetFraction * maxPx - cursorSizePx / 2).roundToInt(),
                            (vm.pickerLightOffsetFraction * maxPx - cursorSizePx / 2).roundToInt()
                        )
                    }
                    .draggable2D(
                        state = rememberDraggable2DState { delta ->
                            vm.changePickerSatLightOffsetFractions(
                                vm.pickerSatOffsetFraction + delta.x / maxPx,
                                vm.pickerLightOffsetFraction + delta.y / maxPx
                            )
                        }
                    )
                    .size(16.dp)
                    .background(Color.Black, CircleShape)
                    .padding(1.dp)
                    .background(Color.White, CircleShape)
                    .padding(3.dp)
                    .background(vm.selectedColor.toSRGB().clamp().toComposeColor(), CircleShape)
            )
        }
    }
}

private fun makeHslGradientBitmap(hue: Float, w: Int, h: Int): ImageBitmap {
    val imageInfo = ImageInfo(
        w, h, ColorType.RGBA_8888,
        ColorAlphaType.PREMUL, SkiaColorSpace.sRGB
    )
    val bytesPerPixel = imageInfo.bytesPerPixel
    val size = imageInfo.computeMinByteSize()
    val bytes = ByteArray(size)

    for (x in 0..<w) {
        for (y in 0..<h) {
            val s = x / w.toFloat()
            val l = 1f - y / h.toFloat()
            val c = HSL(hue, s, l).toSRGB().clamp().toRGBInt()
            val pixelIndex = (y * w + x) * bytesPerPixel
            bytes[pixelIndex + 0] = c.r.toByte()
            bytes[pixelIndex + 1] = c.g.toByte()
            bytes[pixelIndex + 2] = c.b.toByte()
            bytes[pixelIndex + 3] = 0xff.toByte()
        }
    }

    val image = SkiaImage.makeRaster(imageInfo, bytes, imageInfo.minRowBytes)
    val imageBitmap = image.toComposeImageBitmap()
    return imageBitmap
}

@Composable
private fun ColorMenuDropdown(modifier: Modifier, vm: ColorPickerViewModel) {
    ControlExposedDropdown(
        modifier = modifier,
        onExpandedChange = { vm.menuExpanded = it },
        expanded = vm.menuExpanded,
        textValue = vm.menuText,
        onTextValueChange = vm::changeMenuText,
        isError = vm.colorTextIsError(),
        onDismissMenu = { vm.menuExpanded = false },
        menuItems = {
            vm.menuOptions.forEachIndexed { i, selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = { vm.setColorMenuIndex(i) }
                )
            }
        }
    )
}


@Composable
private fun ControlExposedDropdown(
    modifier: Modifier,
    onExpandedChange: (Boolean) -> Unit,
    expanded: Boolean,
    textValue: String,
    onTextValueChange: (String) -> Unit,
    isError: Boolean,
    onDismissMenu: () -> Unit,
    menuItems: @Composable ColumnScope.() -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onTextValueChange,
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            isError = isError,
        )
        // filter options based on text field value
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissMenu,
            modifier = modifier.exposedDropdownSize(matchTextFieldWidth = true),
            properties = PopupProperties(focusable = false),
            content = menuItems
        )
    }
}

@Composable
private fun ControlDropdown(
    selected: String,
    entries: List<Pair<String, () -> Unit>>,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = OutlinedTextFieldDefaults.shape,
            content = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selected)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = {
                for ((text, action) in entries) {
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            action()
                            expanded = false
                        })
                }
            }
        )
    }
}

@Composable
private fun GradientPanel(
    modifier: Modifier = Modifier,
    vm: ColorPickerViewModel,
) {
    Row(modifier.background(MaterialTheme.colorScheme.surface)) {
        for ((i, gradient) in vm.gradients.withIndex()) {
            Column(Modifier.fillMaxSize().weight(1f)) {
                Row(
                    Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    GradientChip(vm, i)
                    Text(
                        gradient.colorSpace.name,
                        Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (vm.gradients.size > 1) {
                        TextButton(onClick = { vm.removeGradient(i) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Selected",
                            )
                        }
                    }
                }
                if (vm.showContinuousGradients) {
                    GradientEntry(Modifier.fillMaxSize(), vm, i, gradient)
                } else {
                    GradientStepsEntry(Modifier.fillMaxSize(), vm, i, gradient)
                }
            }
        }
    }
}

@Composable
private fun GradientChip(vm: ColorPickerViewModel, i: Int) {
    FilterChip(
        onClick = { vm.selectStep(i, null) },
        label = { Text("${i + 1}") },
        selected = vm.gradientIndex == i,
    )
}

@Composable
fun GradientStepsEntry(
    modifier: Modifier = Modifier,
    vm: ColorPickerViewModel,
    index: Int,
    gradient: Gradient,
) {
    Column(modifier) {
        val colors = gradient.interpolator().sequence(vm.stepCount)
        for ((stepIndex, color) in colors.withIndex()) {
            var boxModifier =
                Modifier.fillMaxWidth().weight(1f, fill = true)
            if (vm.gradientIndex == index && vm.stepIndex == stepIndex) {
                boxModifier = boxModifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
            }
            boxModifier = boxModifier
                .background(color.toSRGB().clamp().toComposeColor())
                .clickable(onClick = {
                    vm.selectStep(index, stepIndex)
                })
                .padding(8.dp)
            Box(boxModifier) {
                if (vm.overlayHexCode) {
                    Text(
                        vm.colorToString(color),
                        Modifier.align(Alignment.TopStart),
                        gradTextColor(color)
                    )
                }
                if (vm.overlayContrast) {
                    Text(
                        "${contrastString(color, RGB("#000"))}b",
                        Modifier.align(Alignment.TopEnd),
                        Color.Black
                    )
                    Text(
                        "${contrastString(color, RGB("#fff"))}w",
                        Modifier.align(Alignment.BottomEnd),
                        Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun GradientEntry(
    modifier: Modifier = Modifier,
    vm: ColorPickerViewModel,
    index: Int,
    gradient: Gradient,
) {
    Box(modifier.drawWithCache {
        val interpolator = gradient.interpolator()
        onDrawBehind {
            interpolator.sequence(size.height.toInt()).forEachIndexed { y, color ->
                // Offset by 0.5 to draw on pixel centers so that AA doesn't blur the line
                drawLine(
                    start = Offset(0f, y.toFloat() + 0.5f),
                    end = Offset(size.width, y.toFloat() + 0.5f),
                    color = color.toSRGB().clamp().toComposeColor(),
                )
            }
        }
    }) {
        if (vm.overlayHexCode) {
            TextButton(
                { vm.selectStep(index, 0) },
                Modifier.align(Alignment.TopStart),
            ) {
                Text(
                    vm.colorToString(gradient.start),
                    style = TextStyle(color = gradTextColor(gradient.start), fontSize = 18.sp),
                )
            }
            TextButton(
                { vm.selectStep(index, vm.stepCount - 1) },
                Modifier.align(Alignment.BottomStart),
            ) {
                Text(
                    vm.colorToString(gradient.end),
                    style = TextStyle(color = gradTextColor(gradient.end), fontSize = 18.sp),
                )
            }
        }
    }
}

@Composable
fun CheckboxWithText(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange(!checked) },
                role = Role.Checkbox
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun gradTextColor(color: ColormathColor): Color {
    return color.firstWithContrast(RGB("#000"), RGB("#fff"), targetContrast = 3f)
        .toComposeColor()
}

private fun contrastString(c1: ColormathColor, c2: ColormathColor): String {
    val s = c1.wcagContrastRatio(c2).toString()
    return s.take(s.indexOf('.') + 3)
}
