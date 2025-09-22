package com.navher.myapplication.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

fun formatDateToSpanish(localDate: LocalDate): String {
    val day = localDate.day
    val monthInSpanish = when (localDate.month) {
        Month.JANUARY -> "enero"
        Month.FEBRUARY -> "febrero"
        Month.MARCH -> "marzo"
        Month.APRIL -> "abril"
        Month.MAY -> "mayo"
        Month.JUNE -> "junio"
        Month.JULY -> "julio"
        Month.AUGUST -> "agosto"
        Month.SEPTEMBER -> "septiembre"
        Month.OCTOBER -> "octubre"
        Month.NOVEMBER -> "noviembre"
        Month.DECEMBER -> "diciembre"
    }
    val year = localDate.year
    return "$day de $monthInSpanish del $year"
}
