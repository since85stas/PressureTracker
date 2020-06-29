package stas.batura.pressuretracker

import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.data.room.Rain
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("pressureTextBind")
fun TextView.pressureTextBind(pressure: Pressure) {
    text = pressure.pressure.toString()
}

@BindingAdapter("timeBind")
fun TextView.timeBind(pressure: Pressure) {
    val formatter = SimpleDateFormat("dd/MM hh:mm");
    val dateString = formatter.format( Date(pressure.time));
    text = dateString
}

@BindingAdapter("timeRainBind")
fun TextView.timeRainBind(rain: Rain) {
    val formatter = SimpleDateFormat("dd/MM hh:mm");
    val dateString = formatter.format( Date(rain.time));
    text = dateString
}

@BindingAdapter("rainStartTextBind")
fun TextView.rainStartTextBind(rain: Rain) {
    if (rain.isStarted
    ) {
        text = "Start"
    } else if (rain.isEnded) {
        text = "Stop"
    }
}

