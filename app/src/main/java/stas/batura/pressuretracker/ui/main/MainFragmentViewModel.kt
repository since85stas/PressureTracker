package stas.batura.pressuretracker.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Rain

class MainFragmentViewModel @ViewModelInject constructor(val repository: IRep) : ViewModel() {

    val pressureLive = repository.getMessages()

    val lastPress = repository.getRainPower()

//    val lastPower = repository.getRainPower()

    fun saveRainPower(power: Int) {
        repository.setLastRainPower(power)
    }


}