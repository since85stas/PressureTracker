package stas.batura.pressuretracker.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import stas.batura.pressuretracker.ChessClockRx.ChessClockRx
import stas.batura.pressuretracker.ChessClockRx.ChessStateChageListner
import stas.batura.pressuretracker.MainActivity
import stas.batura.pressuretracker.R
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Pressure
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class PressureService @Inject constructor(): LifecycleService(), SensorEventListener, ChessStateChageListner {

    private val TAG = PressureService::class.simpleName

    private val NOTIFICATION_ID = 21

    private val CHANNEL_ID = "PressCh"

    private val INTERVAL = 60L * 5

    @Inject lateinit var sensorManager: SensorManager

    @Inject lateinit var repository: IRep

    private var sensor: Sensor? = null

    private lateinit var chessClockRx: ChessClockRx

    private var lastPower: Int = 0

//    var lastRainPow: Int = 0

    override fun onCreate() {
        super.onCreate()
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "onCreate: " + deviceSensors.toString())

        initPressSensor()

//        registerListn()

        createClock()

        startForeground(NOTIFICATION_ID, getNotification())

        lastPower = repository.getRainPower().lastPowr

        repository.getPressures().observe( this, Observer {

        })
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
                Log.d(TAG, "servise is bind " + intent.toString())
        this.PressureServiceBinder().isBind = true
        return PressureServiceBinder()
    }


    //    override fun onBind(intent: Intent?): IBinder? {
//        Log.d(TAG, "servise is bind " + intent.toString())
//        this.PressureServiceBinder().isBind = true
//        return PressureServiceBinder()
//    }

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

    /**
     * initianing a sensor manager
     */
    private fun initPressSensor() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_PRESSURE)
            // Use the version 3 gravity sensor.
            sensor = gravSensors.firstOrNull()
        } else {
            Toast.makeText(applicationContext, "Sensor not detected", Toast.LENGTH_LONG).show()
        }

    }

    /**
     * saving value in DB
     */
    private fun savePressureValue(pressure: Float) {
        val roomPre = Pressure(pressure, System.currentTimeMillis(), lastPower)
        Log.d(TAG, "savePressureValue: " + pressure)
        repository.insertPressure(roomPre)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /**
     * getting a value from sensor
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val pressure = event.values
            unregisterListn()
            savePressureValue(pressure[0])
        }
    }

    /**
     * registring from sensor
     */
    private fun registerListn() {
        sensor?.also { light ->
            sensorManager.registerListener(this, light, 100000)
        }
    }

    /**
     * unregistring from sensor
     */
    private fun unregisterListn() {
        sensorManager.unregisterListener(this)
    }

    /**
     * creates a new clock object
     */
    private fun createClock() {
        chessClockRx = ChessClockRx(INTERVAL, this);
    }

    /**
     * stoping clock fun
     */
    private fun stopClock() {
        chessClockRx.stopTimer()
    }

    /**
     * recieving a time from clock
     */
    override fun timeChange(time: Long) {
        Log.d(TAG, "timeChange: " + time)
        if (time == 0L) {
            registerListn()
        }
    }

    /**
     * finshing a time period
     */
    override fun timeFinish() {
        Log.d(TAG, "timeFinish: ")
        createClock()
    }

    /**
     * create a Notification object
     */
    private fun getNotification(): Notification {

        createNotificationChannel()

        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(getIconId())
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("Collecting pressure..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notifyPendingIntent)

        return builder.build()
    }

    private fun getIconId(): Int {
        when (lastPower) {
            0 -> return R.drawable.icon_0
            1 -> return R.drawable.icon_1
            2 -> return R.drawable.icon_2
            3 -> return R.drawable.icon_3
            4 -> return R.drawable.icon_4
            5 -> return R.drawable.icon_5
        }
        return R.drawable.icon_0
    }


    private fun updateNotification() {

        val notification = getNotification()
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * create a notification chanel
     */
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

    /**
     * create a service binder
     */
    inner class PressureServiceBinder : Binder() {

        var isBind: Boolean = false

        fun setRainPower(rainpower: Int) {
            this@PressureService.lastPower = rainpower
            updateNotification()
        }

        fun closeService() {
            this@PressureService.stopService()
        }

        fun getRainPower() : Int {
            return this@PressureService.lastPower
        }

        fun savePressure() {
            return this@PressureService.registerListn()
        }

    }

    fun stopService() {
        stopSelf()
    }

    private fun createTxtFile(): FileWriter? {
        val fileName = "GoodNotes.txt"
        return try {
            FileWriter(File("sdcard/$fileName"))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}