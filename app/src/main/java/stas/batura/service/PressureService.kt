package stas.batura.service

import android.app.Service
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import stas.batura.pressuretracker.ui.data.IRep
import stas.batura.pressuretracker.ui.data.Repository
import javax.inject.Inject

@AndroidEntryPoint
class PressureService @Inject constructor(var repository: IRep): Service () {

//    @Inject lateinit

    private val TAG = PressureService::class.simpleName

    @Inject lateinit var sensorManager: SensorManager


    override fun onCreate() {
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "onCreate: " + deviceSensors.toString())
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "servise is bind " + intent.toString())
        this.PressureServiceBinder().isBind = true
        return PressureServiceBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "servise is unbind " + intent.toString())
        this.PressureServiceBinder().isBind = false
        return super.onUnbind(intent)
    }

    inner class PressureServiceBinder : Binder() {

        var isBind: Boolean = false

    }
}