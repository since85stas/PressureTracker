package stas.batura.pressuretracker.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Rain

class MainFragmentViewModel @ViewModelInject constructor(val repository: IRep) : ViewModel() {

    val pressureLive = repository.getMessages()

    val rainLive = repository.getRainList()

    /**
     * writing srtart rain info
     */
    fun rainStart() {
        val rain = Rain(isStarted = true, isEnded = false, time = System.currentTimeMillis())
        repository.insertRain(rain)
    }

    /**
     * writing stop rain info
     */
    fun rainStop() {
        val rain = Rain(false, isEnded = true, time = System.currentTimeMillis())
        repository.insertRain(rain)
    }


}