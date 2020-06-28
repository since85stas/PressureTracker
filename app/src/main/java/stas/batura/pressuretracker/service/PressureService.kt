package stas.batura.pressuretracker.service

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Pressure
import javax.inject.Inject

@AndroidEntryPoint
class PressureService @Inject constructor(): Service (), SensorEventListener {

    private val TAG = PressureService::class.simpleName

    @Inject lateinit var sensorManager: SensorManager

    @Inject lateinit var repository: IRep

    private var sensor: Sensor? = null


    override fun onCreate() {
        super.onCreate()
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "onCreate: " + deviceSensors.toString())

        initPressSensor()

        sensor?.also { light ->
            sensorManager.registerListener(this, light, 1000000)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun initPressSensor() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_PRESSURE)
            // Use the version 3 gravity sensor.
            sensor = gravSensors.firstOrNull()
        }

    }

    private fun savePressureValue(pressure: Float) {
        val roomPre = Pressure(pressure, System.currentTimeMillis())
        Log.d(TAG, "savePressureValue: " + pressure)
        repository.insertPressure(roomPre)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val pressure = event.values
//            savePressureValue(pressure[0])
        }
    }

    inner class PressureServiceBinder : Binder() {

        var isBind: Boolean = false

    }
}