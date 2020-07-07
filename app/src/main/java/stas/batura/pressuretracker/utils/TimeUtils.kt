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

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 30)
    return calendar.timeInMillis
}

fun getCurrentDayEnd(calendar: Calendar): Calendar {
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 50)
    return calendar
}

fun getTimeInHours(time: Int): Float {
    val timeMin = time / (1000.0*60.0)
    val timeHours = time/(60.0*24.0)
    return timeHours.toFloat()
}