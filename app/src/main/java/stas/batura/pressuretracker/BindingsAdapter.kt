package stas.batura.pressuretracker

import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.pressuretracker.data.room.Pressure

@BindingAdapter("pressureTextBind")
fun TextView.pressureTextBind(pressure: Pressure) {
    text = pressure.pressure.toString()
}

@BindingAdapter("timeBind")
fun TextView.timeBind(pressure: Pressure) {
    text = pressure.time.toString()
}