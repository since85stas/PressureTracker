package stas.batura.pressuretracker.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import stas.batura.pressuretracker.ChessClockRx.ChessClockRx
import stas.batura.pressuretracker.ChessClockRx.ChessStateChageListner
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Pressure
import javax.inject.Inject


@AndroidEntryPoint
class PressureService @Inject constructor(): Service (), SensorEventListener, ChessStateChageListner {

    private val TAG = PressureService::class.simpleName

    private val NOTIFICATION_ID = 21

    private val CHANNEL_ID = "PressCh"

    private val INTERVAL = 60L * 2

    @Inject lateinit var sensorManager: SensorManager

    @Inject lateinit var repository: IRep

    private var sensor: Sensor? = null

    private lateinit var chessClockRx: ChessClockRx

    override fun onCreate() {
        super.onCreate()
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "onCreate: " + deviceSensors.toString())

        initPressSensor()

//        registerListn()

        createClock()

        startForeground(NOTIFICATION_ID, getNotification())
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
        unregisterListn()
        stopClock()
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
            unregisterListn()
            savePressureValue(pressure[0])
        }
    }

    private fun registerListn() {
        sensor?.also { light ->
            sensorManager.registerListener(this, light, 100000)
        }
    }

    private fun unregisterListn() {
        sensorManager.unregisterListener(this)
    }

    private fun createClock() {
        chessClockRx = ChessClockRx(INTERVAL, this);
    }

    private fun stopClock() {
        chessClockRx.stopTimer()
    }

    override fun timeChange(time: Long) {
        Log.d(TAG, "timeChange: " + time)
        if (time == 0L) {
            registerListn()
        }
    }

    override fun timeFinish() {
        Log.d(TAG, "timeFinish: ")
        createClock()
    }

    private fun getNotification(): Notification {

        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_delete)
                .setStyle(NotificationCompat.BigTextStyle()

                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "name"
            val description = "descr"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class PressureServiceBinder : Binder() {

        var isBind: Boolean = false

    }


}