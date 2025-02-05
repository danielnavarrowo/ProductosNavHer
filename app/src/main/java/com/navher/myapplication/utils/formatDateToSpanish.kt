package com.navher.myapplication.utils

import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun formatDateToSpanish(localDate: LocalDate): String {
    val day = localDate.dayOfMonth
    val month = localDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")) // Obtiene el mes en español
    val year = localDate.year
    return "$day de $month del $year"
}