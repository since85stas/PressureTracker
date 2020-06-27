package stas.batura.pressuretracker

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.ui.data.Repository
import stas.batura.service.PressureService

class MainViewModel @ViewModelInject constructor(val repository: PressureDao) : ViewModel() {

    private val TAG = MainViewModel::class.simpleName

    var serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData()

    private var playerServiceBinder: PressureService.PressureServiceBinder? = null

    init {
        createService()
    }

    /**
     * creating our service and getting connection
     */
    fun createService() {

        if (serviceConnection.value == null) {

            // соединение с сервисом
            serviceConnection.value = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    playerServiceBinder = service as PressureService.PressureServiceBinder
                    try {

                    } catch (e: RemoteException) {
                        Log.d(TAG, "onServiceConnected: " + e);
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    playerServiceBinder = null
                }
            }

        }

    }

}