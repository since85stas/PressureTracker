package stas.batura.pressuretracker.utils

import java.util.*

fun getCurrentDayBegin(): Calendar {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 10)
    return calendar
}

fun getCurrentDayEnd(): Long {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 11)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 30)
    return calendar.timeInMillis
}

fun getCurrentDayEnd(calendar: Calendar): Calendar {
    calendar.set(Calendar.HOUR_OF_DAY, 11)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 50)
    return calendar
}