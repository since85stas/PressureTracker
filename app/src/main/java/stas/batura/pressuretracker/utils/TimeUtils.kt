package stas.batura.pressuretracker.utils

import java.util.*

fun getCurrentDayBegin(): Long {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 10)
    return calendar.timeInMillis
}

fun getCurrentDayEnd(): Long {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 11)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 30)
    return calendar.timeInMillis
}