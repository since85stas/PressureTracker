package stas.batura.pressuretracker.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.data.room.Rain
import javax.inject.Inject

interface IRep: PressureDao {

}

class Repository @Inject constructor(): IRep {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var repositoryJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [repositoryJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val ioScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    @Inject lateinit var pressureData: PressureDao

    override fun insertPressure(pressure: Pressure) {
        ioScope.launch {
            pressureData.insertPressure(pressure)
        }
    }

    override fun getMessages(): LiveData<List<Pressure>> {
        return pressureData.getMessages()
    }

    override fun setLastRainPower(power: Int) {
        ioScope.launch {
            pressureData.setLastRainPower(power)
        }
    }

    override fun getRainPower(): LiveData<Rain> {
        return pressureData.getRainPower()
    }

    override fun insertRain(rain: Rain) {
        Log.d("ins","ins")
    }

    //    override fun insertRain(rain: Rain) {
//        ioScope.launch {
//            pressureData.insertRain(rain)
//        }
//    }
//
//    override fun getRainList(): LiveData<List<Rain>> {
//        return pressureData.getRainList()
//    }
//
//    override fun lastRain(): LiveData<Rain?> {
//        return pressureData.lastRain()
//    }
}