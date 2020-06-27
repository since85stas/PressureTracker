package stas.batura.pressuretracker.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.batura.stat.batchat.repository.room.PressureDao

class MainFragmentViewModel @ViewModelInject constructor(val repository: PressureDao) : ViewModel() {

    val pressureLive = repository.getMessages()


}