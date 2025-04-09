package com.navher.myapplication.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.navher.myapplication.R
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.Products
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private const val SLIDER_MIN_VALUE = 1
private const val SLIDER_MAX_VALUE = 200
private const val SLIDER_VISIBLE_RANGE_MAX = 20f // Rango visible del slider (puede ser diferente al min/max real)
private const val SLIDER_VISIBLE_STEPS = 19 // Pasos para el rango visible del slider

@Composable
fun RowScope.ScannerButton(onQueryChange: (String) -> Unit) {
    IconButton(
        modifier = Modifier
            .weight(.16f)
            .align(Alignment.CenterVertically)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(16.dp)),
        onClick = {
            startScan(onQueryChange)
        },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Icon(
            modifier = Modifier.padding(12.dp),
            painter = painterResource(id = R.drawable.barcode),
            contentDescription = stringResource(R.string.barcode_scanner_cd)
        )
    }
}


@Composable
fun ColumnScope.LastUpdate(updateDate: String, navController: NavController) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .align(Alignment.CenterHorizontally)
            .clickable { navController.navigate("settings") }
    ) {
        Text(
            text = stringResource(R.string.last_update_prefix, updateDate ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun StepsSlider(initialValue: Int, onValueChange: (Int) -> Unit) {
    var sliderPosition by remember { mutableIntStateOf(initialValue.coerceIn(SLIDER_MIN_VALUE, SLIDER_MAX_VALUE)) }
    var textValue by remember { mutableStateOf(sliderPosition.toString()) }
    val haptic = LocalHapticFeedback.current
    var previousStep by remember { mutableIntStateOf(sliderPosition) }

    fun updateValue(newValue: Int) {
        sliderPosition = newValue
        textValue = newValue.toString() // Sincronizar texto
        onValueChange(newValue) // Notificar al exterior
    }



    Column(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.inversePrimary,
            shape = RoundedCornerShape(10.dp),
        ).padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(.7f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(.2f)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                // Decrementar inmediatamente una vez
                                if (sliderPosition > 1) {
                                    updateValue(sliderPosition - 1)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                // Detectar presión continua
                                val job = CoroutineScope(Dispatchers.Main).launch {
                                    // Esperar un poco antes de iniciar el decremento rápido
                                    delay(500)
                                    while (isActive && sliderPosition > 1) {
                                        updateValue(sliderPosition - 1)
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        delay(80) // Controla la velocidad de decremento
                                    }
                                }
                                // Esperar hasta que se levante el dedo o se cancele
                                tryAwaitRelease()
                                job.cancel()
                            }
                        )
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.minus),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Menos",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

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
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(.2f)
                    .padding(vertical = 6.dp)
            )

            // Botón de incremento con soporte para presión continua
            Box(
                modifier = Modifier
                    .weight(.2f)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                // Incrementar inmediatamente una vez
                                if (sliderPosition < 500) {
                                    updateValue(sliderPosition + 1)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }

                                // Detectar presión continua
                                val job = CoroutineScope(Dispatchers.Main).launch {
                                    // Esperar un poco antes de iniciar el incremento rápido
                                    delay(500)
                                    while (isActive && sliderPosition < 500) {
                                       updateValue(sliderPosition + 1)
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        delay(80) // Controla la velocidad de incremento
                                    }
                                }
                                // Esperar hasta que se levante el dedo o se cancele
                                tryAwaitRelease()
                                job.cancel()
                            }
                        )
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    contentDescription = "Más",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Slider(
            modifier = Modifier
                .semantics { contentDescription = "" },
            value = sliderPosition.toFloat(),
            onValueChange = { newValueFromSlider ->
                val roundedValue = newValueFromSlider.roundToInt()
                updateValue(roundedValue) // Actualiza estado interno y notifica

                // Haptic feedback específico del slider (al cambiar de paso)
                val currentStep = roundedValue
                if (currentStep != previousStep) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    previousStep = currentStep
                }
            },
            valueRange = SLIDER_MIN_VALUE.toFloat()..SLIDER_VISIBLE_RANGE_MAX, // Rango visible
            steps = SLIDER_VISIBLE_STEPS
        )
    }
}


@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.search_products_placeholder),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, // Descripción podría ser stringResource(R.string.search_icon_cd)
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                    }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search_cd),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
        ),
        textStyle = MaterialTheme.typography.bodySmall,
    )
}


@SuppressLint("DefaultLocale") // Mantenido por String.format
@Composable
fun ProductCard(product: Products, forceExpanded: Boolean = false) {
    var isExpanded by remember { mutableStateOf(forceExpanded) } // Inicializar con forceExpanded
    var multiplier by remember { mutableIntStateOf(1) }
    val focusManager = LocalFocusManager.current

    // Ajusta la expansión si forceExpanded cambia externamente
    LaunchedEffect(forceExpanded) {
        if (isExpanded != forceExpanded) {
            isExpanded = forceExpanded
        }
    }

    // Resetea el multiplicador si la tarjeta se colapsa (funcionalidad original mantenida)
    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            multiplier = 1
        }
    }


    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
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
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Alinear verticalmente
            ) {
                Text(
                    text = product.descripcion,
                    modifier = Modifier.weight(3.2f), // Mantenido el peso
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 2, // Evita que textos muy largos descuadren mucho (opcional)
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Opcional
                )
                Text(
                    text = "$${String.format("%.2f", product.pventa)}", // Mantiene formato dos decimales
                    modifier = Modifier.weight(1f), // Mantenido el peso
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.End
                )
            }

            // Contenido expandible
            // SUGGESTION: Usar AnimatedVisibility para una animación de entrada/salida más suave
            AnimatedVisibility(isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.cost_label), // SUGGESTION: stringResource
                            value = product.pcosto * multiplier,
                            modifier = Modifier.weight(.8f),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.sale_label), // SUGGESTION: stringResource
                            value = product.pventa * multiplier,
                            modifier = Modifier
                                .weight(1f),
                            color = MaterialTheme.colorScheme.onPrimaryContainer, // Color específico para Venta
                            style = MaterialTheme.typography.titleMedium, // Estilo específico para Venta
                            fontWeight = FontWeight.Black // Negrita para Venta
                        )
                        PriceText( // SUGGESTION: Composable interno para los textos de precio
                            label = stringResource(R.string.wholesale_label), // SUGGESTION: stringResource
                            value = product.mayoreo * multiplier,
                            modifier = Modifier.weight(.8f),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium
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
    val formattedValue = "$${String.format("%.2f", value)}"
    Text(
        text = "$label\n$formattedValue",
        style = style,
        color = color,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}