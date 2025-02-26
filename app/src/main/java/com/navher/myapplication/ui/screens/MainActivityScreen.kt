package com.navher.myapplication.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navher.myapplication.R
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.Products
import com.navher.myapplication.utils.formatDateToSpanish
import kotlin.math.roundToInt

object MainActivityScreen {

    @Composable
    fun RowScope.ScannerButton(onQueryChange: (String) -> Unit) {
        IconButton(
            modifier = Modifier.weight(.16f)
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
            Icon(modifier = Modifier.padding(12.dp), painter = painterResource(id = R.drawable.barcode), contentDescription = "Barcode Scanner")
        }
    }

    @Composable
    fun ColumnScope.LastUpdate (dataService: DataService) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp, // Set the thickness of the border
                    color = MaterialTheme.colorScheme.secondary, // Set the color of the border
                    shape = RoundedCornerShape(6.dp) // Match the same shape as the Box
                )
                .padding(horizontal = 6.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Última actualización: " + formatDateToSpanish(dataService.serverUpdate),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }

    @Composable
    fun StepsSlider(initialValue: Int, onValueChange: (Int) -> Unit) {
        var sliderPosition by remember { mutableIntStateOf(initialValue) }
        var textValue by remember { mutableStateOf(sliderPosition.toString()) }
        val haptic = LocalHapticFeedback.current // Get haptic feedback provider
        var previousStep by remember { mutableIntStateOf(sliderPosition) } // Store the previous step

        Column(modifier = Modifier.background(
            color = MaterialTheme.colorScheme.inversePrimary,
            shape = RoundedCornerShape(10.dp),
        )) {
            BasicTextField(
                value = textValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        textValue = ""
                        sliderPosition = 1
                        onValueChange(1)
                    } else {
                        newValue.toIntOrNull()?.let { intValue ->
                            if (intValue in 1..500) {
                                textValue = intValue.toString()
                                sliderPosition = intValue
                                onValueChange(intValue)
                            }
                        }
                    }
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            )
            Slider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).semantics { contentDescription = "Localized Description" },
                value = sliderPosition.toFloat(),
                onValueChange = {
                    sliderPosition = it.roundToInt()
                    textValue = it.roundToInt().toString()
                    onValueChange(it.roundToInt())

                    val currentStep = sliderPosition // Get the current step
                    if (currentStep != previousStep) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Trigger vibration
                        previousStep = currentStep // Update the previous step
                    }
                },
                valueRange = 1f..25f,
                steps = 24
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
                    "Buscar productos",
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            )
        )
    }

    @Composable
    fun ProductCard(product: Products) {
        var isExpanded by remember { mutableStateOf(false) }
        var multiplier by remember { mutableIntStateOf(1) }
        val focusManager = LocalFocusManager.current

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .animateContentSize() // This will animate the size changes smoothly
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(

                        onPress = {
                            focusManager.clearFocus()
                        },
                        onTap = {
                            isExpanded = !isExpanded
                        }
                    )
                }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.descripcion,
                        Modifier.weight(3.2f),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "$${product.pventa}0",
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.End
                    )
                }

                if (isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().wrapContentSize()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(10.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Costo:\n$${String.format("%.2f", product.pcosto * multiplier)}",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(120.dp)

                                )
                                Text(
                                    text = "Venta:\n$${String.format("%.2f", product.pventa * multiplier)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(120.dp)

                                )
                                Text(
                                    text = "Mayoreo:\n$${String.format("%.2f", product.mayoreo * multiplier)}",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(120.dp)

                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            StepsSlider(
                                initialValue = multiplier,
                                onValueChange = { multiplier = it }
                            )
                        }
                    }
                }
            }
        }
    }

}