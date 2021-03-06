package stas.batura.pressuretracker.ui.graph

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import stas.batura.pressuretracker.data.IRep

class GraphViewModel @ViewModelInject constructor(private var repository: IRep) : ViewModel() {

//    val pressList = repository.getPressures()
    val pressList = repository.getPressuresLiveDay()

    var lastPress = repository.getRainPower()

    fun saveRainPower(power: Int) {
        repository.setLastRainPower(power)
    }

    fun updateRainpower() {
        lastPress = repository.getRainPower()
    }
}