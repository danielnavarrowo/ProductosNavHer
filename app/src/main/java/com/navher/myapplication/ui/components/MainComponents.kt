package com.navher.myapplication.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.iconButtonVibrantColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.navher.myapplication.R
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.Products
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt


private const val SLIDER_MIN_VALUE = 1
private const val SLIDER_MAX_VALUE = 200
private const val SLIDER_VISIBLE_RANGE_MAX = 20f // Rango visible del slider (puede ser diferente al min/max real)
private const val SLIDER_VISIBLE_STEPS = 19 // Pasos para el rango visible del slider

@Composable
fun RowScope.ScannerButton(navController: NavController, onQueryChange: (String) -> Unit) {
    IconButton(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .size(56.dp),

        onClick = {
            startScan(navController, onQueryChange)
        },
        colors = iconButtonVibrantColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            modifier = Modifier.padding(16.dp).size(56.dp),
            painter = painterResource(id = R.drawable.scan_barcode),
            contentDescription = stringResource(R.string.barcode_scanner_cd)
        )
    }
}

@Composable
fun FAB () {
    MediumFloatingActionButton(
        onClick = { productsViewModel.loadProducts() },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) { }
}


@Composable
fun ColumnScope.LastUpdate(updateDate: String, navController: NavController) {
    Box(
        modifier = Modifier

            .background(
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { navController.navigate("settings") }
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .align(Alignment.CenterHorizontally)

    ) {
        Text(
            text = stringResource(R.string.last_update_prefix, updateDate ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StepsSlider(initialValue: Int, onValueChange: (Int) -> Unit) {
    var sliderPosition by remember {
        mutableIntStateOf(
            initialValue.coerceIn(
                SLIDER_MIN_VALUE,
                SLIDER_MAX_VALUE
            )
        )
    }
    var textValue by remember { mutableStateOf(sliderPosition.toString()) }
    val haptic = LocalHapticFeedback.current
    var previousStep by remember { mutableIntStateOf(sliderPosition) }

    fun updateValue(newValue: Int) {
        sliderPosition = newValue
        textValue = newValue.toString()
        onValueChange(newValue)
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        //horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val interactionSourceMinus = remember { MutableInteractionSource() }
        val interactionSourcePlus = remember { MutableInteractionSource() }
        val viewConfiguration = LocalViewConfiguration.current

        LaunchedEffect(interactionSourceMinus) {
            var isLongClick = false

            interactionSourceMinus.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isLongClick = false
                        delay(viewConfiguration.longPressTimeoutMillis)
                        isLongClick = true
                        while (sliderPosition > 1) {
                            updateValue(sliderPosition - 1)
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            delay(80)
                        }
                    }

                    is PressInteraction.Release -> {
                        if (!isLongClick) {
                            if (sliderPosition > 1) {
                                updateValue(sliderPosition - 1)
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            }
                        }
                    }
                }
            }
        }

        LaunchedEffect(interactionSourcePlus) {
            var isLongClick = false

            interactionSourcePlus.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isLongClick = false
                        delay(viewConfiguration.longPressTimeoutMillis)
                        isLongClick = true
                        while (sliderPosition < 500) {
                            updateValue(sliderPosition + 1)
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            delay(80)
                        }
                    }

                    is PressInteraction.Release -> {
                        if (!isLongClick) {
                            if (sliderPosition < 500) {
                                updateValue(sliderPosition + 1)
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            }
                        }
                    }
                }
            }
        }

        ButtonGroup(
            overflowIndicator = { state ->
                ButtonGroupDefaults.OverflowIndicator(
                    state,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                    modifier = Modifier.size(32.dp, 48.dp)
                )
            },
            //modifier = Modifier.fillMaxWidth()
        ) {
            customItem(
                {
                    FilledTonalIconButton(
                        onClick = {},
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shapes = IconButtonDefaults.shapes(),
                        interactionSource = interactionSourceMinus,
                        modifier = Modifier
                            .size(96.dp, 48.dp)
                            .animateWidth(interactionSourceMinus)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.remove),
                            contentDescription = "Menos",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                },
                { _ ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.remove),
                                null
                            )
                        },
                        text = { Text("Hola") },
                        onClick = {
                            if (sliderPosition > 1) {
                                updateValue(sliderPosition - 1)
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            }
                        }
                    )
                },
            )
            customItem(
                {
                    BasicTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty()) {
                                sliderPosition = 1
                                textValue = ""
                                onValueChange(1) // Notificar al exterior
                            }

                            else {
                                newValue.toIntOrNull()?.let { intValue ->
                                    if (intValue in 1..500) updateValue(intValue)
                                }
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                },
                {}
            )


            customItem(
                {
                    FilledTonalIconButton(
                        onClick = {},
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shapes = IconButtonDefaults.shapes(),
                        interactionSource = interactionSourcePlus,
                        modifier = Modifier
                            .size(96.dp, 48.dp)

                            .animateWidth(interactionSourcePlus)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Más",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                },
                { _ ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.add),
                                null
                            )
                        },
                        text = { Text("Hola") },
                        onClick = {
                            if (sliderPosition < 500) {
                                updateValue(sliderPosition + 1)
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            }
                        }
                    )
                },
            )
        }
    }

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    Slider(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .semantics { contentDescription = "" },
        value = sliderPosition.toFloat(),
        onValueChange = { newValueFromSlider ->
            val roundedValue = newValueFromSlider.roundToInt()
            updateValue(roundedValue) // Actualiza estado interno y notifica

            val currentStep = roundedValue
            if (currentStep != previousStep) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                previousStep = currentStep
            }
        },
        valueRange = SLIDER_MIN_VALUE.toFloat()..SLIDER_VISIBLE_RANGE_MAX, // Rango visible
        steps = SLIDER_VISIBLE_STEPS
    )
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (query.isNotEmpty())
                    MaterialTheme.colorScheme.surfaceContainerLowest
                else
                    MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.Center

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (query.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.search_products_placeholder),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = stringResource(R.string.clear_search_cd),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(45f)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("DefaultLocale") // Mantenido por String.format
@Composable
fun ProductCard(
    product: Products,
    forceExpanded: Boolean = false,
    isFirstItem: Boolean = false,
    isLastItem: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(forceExpanded) } // Inicializar con forceExpanded
    var multiplier by remember { mutableIntStateOf(1) }
    val focusManager = LocalFocusManager.current
    val isHighPriority = product.iprioridad == 1

    // Ajusta la expansión si forceExpanded cambia externamente
    LaunchedEffect(forceExpanded) {
        if (isExpanded != forceExpanded) {
            isExpanded = forceExpanded
        }
    }

    Box(
        modifier = Modifier
            .clip(
                when {
                    isFirstItem -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    isLastItem -> RoundedCornerShape( bottomStart = 16.dp, bottomEnd = 16.dp)
                    else -> RoundedCornerShape(0.dp)
                }
            )
            .background( color= MaterialTheme.colorScheme.surface
            )
            .wrapContentSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        focusManager.clearFocus() // Quita el foco de cualquier campo (como el de StepsSlider)
                    },
                    onTap = {
                        isExpanded = !isExpanded // Cambia el estado de expansión al tocar
                    }
                )
            }
    ) {
        Column {
            // Fila superior siempre visible
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Usa fillMaxWidth para consistencia
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Alinear verticalmente
            ) {

                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isHighPriority) {
                        Icon(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Estrella",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 1.dp)
                                .size(12.dp)
                        )
                    }
                    Text(
                        text = product.descripcion,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp
                        ),
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                    )
                }

                Text(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ).padding(horizontal = 6.dp, vertical = 3.dp),
                    text = "$${String.format("%.2f", product.pventa)}", // Mantiene formato dos decimales
                    // Mantenido el peso
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                )
            }

            AnimatedVisibility(isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.cost_label), // SUGGESTION: stringResource
                            value = product.pcosto * multiplier,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.sale_label), // SUGGESTION: stringResource
                            value = product.pventa * multiplier,
                            color = MaterialTheme.colorScheme.onSurface, // Color específico para Venta
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 22.sp, // Tamaño de fuente más grande
                            ), // Estilo específico para Venta
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f),
                        )
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.wholesale_label), // SUGGESTION: stringResource
                            value = product.mayoreo * multiplier,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    StepsSlider(
                        initialValue = 1,
                        onValueChange = { multiplier = it } // Actualiza el multiplicador del ProductCard
                    )
                }
            }
        }
    }
}

/**
 * SUGGESTION: Composable interno para mostrar etiqueta y precio formateado.
 */
@SuppressLint("DefaultLocale") // Mantenido por String.format
@Composable
private fun PriceText(
    label: String,
    value: Double,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified, // Usa el color del contexto por defecto
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color.copy(alpha = .6f),
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,

            )
        Spacer(modifier = Modifier.height(3.dp))
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }, label = "price-transition"
        ) { targetValue ->
            Text(
                text = "$${String.format("%.2f", targetValue)}",
                style = style,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
            )
        }
    }
}
