package stas.batura.pressuretracker

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.service.PressureService

class MainViewModel @ViewModelInject constructor(val repository: IRep) : ViewModel() {

    private val TAG = MainViewModel::class.simpleName

    var serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData()

    private var playerServiceBinder: PressureService.PressureServiceBinder? = null

    private var _stopServiceLive: MutableLiveData<Boolean> = MutableLiveData(false)
    val stopServiceLive: LiveData<Boolean>
        get() = _stopServiceLive

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
                    try {
                        playerServiceBinder = service as PressureService.PressureServiceBinder
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

    /**
     * closing service through a Binder, after we unbinding it
     */
    fun closeService() {
        if (playerServiceBinder != null) {
            playerServiceBinder!!.closeService()
        }
        _stopServiceLive.value = false
    }

    /**
     * sending command to unbind and close service
     */
    fun stopService() {
        _stopServiceLive.value = true
    }

    fun setServiceRain(rainp: Int) {
        if (playerServiceBinder != null) {
            playerServiceBinder!!.setRainPower(rainp)
        }
    }

    /**
     * saving value in DB
     */
    fun savePressureValue() {
        if (playerServiceBinder != null) {
            playerServiceBinder!!.savePressure()
        }
    }

    fun testSave() {
        if (playerServiceBinder != null) {
            playerServiceBinder!!.testWrite()
        }
    }

}