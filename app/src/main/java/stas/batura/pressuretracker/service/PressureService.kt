package stas.batura.pressuretracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.*
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.functions.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import rx.Observable
import stas.batura.pressuretracker.MainActivity
import stas.batura.pressuretracker.R
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.rx.chess.ChessClockRx
import stas.batura.pressuretracker.rx.chess.ChessStateChageListner
import stas.batura.pressuretracker.rx.rxZipper.Zipper
import stas.batura.pressuretracker.utils.getCurrentDayBegin
import stas.batura.pressuretracker.utils.getCurrentDayEnd
import stas.batura.pressuretracker.utils.getTimeInHours
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PressureService @Inject constructor(): LifecycleService(), SensorEventListener, LocationListener,
        ChessStateChageListner {

    /**
     * Job allows us to cancel all coroutines started by this ViewModel.
     */
    private var serviceJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this Service.
     */
    private val ioScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val TAG = PressureService::class.simpleName

    private val NOTIFICATION_ID = 21

    private val CHANNEL_ID = "PressCh"

    // interval between saves in milliseconds
    private val INTERVAL = 60L * 5

    @Inject lateinit var sensorManager: SensorManager

    @Inject lateinit var repository: IRep

    // pressure sensor installed
    private var sensor: Sensor? = null

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false

    // Declaring a Location Manager
    @Inject lateinit var locationManager: LocationManager

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient

    // chess instance to calculate distance before saves
    private lateinit var chessClockRx: ChessClockRx

    // last rain power
    private var lastPower: Int = 0

    // begin of last day date
    private lateinit var lastDayBegin: Calendar

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters


    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
            .toFloat()

    private var lastAlt: Float = 0.0f

    var stringObservable1 = Observable.just("String")
    var stringObservable2 = Observable.just("String")


    private val consumer = object : Consumer<String> {
        override fun accept(t: String?) {
            println(t)
        }
    }

    val zipper = Zipper(consumer)

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "onCreate: " + deviceSensors.toString())

        lastDayBegin = getCurrentDayBegin()

        initPressSensor()

        createClock()

        startForeground(NOTIFICATION_ID, getNotification())

        lastPower = repository.getRainPower().lastPowr

    }


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
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

            zipper.generatePress(pressure[0].toInt())
//            zipper.generObserv()

            savePressureValue(pressure[0])
        }
    }

    /**
     * registring from sensor
     */
    private fun registerListn() {

        if (sensor != null) {
            sensor?.also { light ->
                sensorManager.registerListener(this, light, 100000)
            }
        } else {
            savePressureValue(1000.1f)
        }
    }

    /**
     * unregistring from sensor
     */
    private fun unregisterListn() {
        sensorManager.unregisterListener(this)
    }

    override fun onLocationChanged(location: Location?) {
        Log.d(TAG, "onLocationChanged: ")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
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
//            registerListn()
            combineData()
            if (checkNextDay()) {
//                addObservers()
                getPressrsForLastday()
            }
        }
    }

    private fun combineData() {
        registerListn()
        getLocation()
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

    /**
     * get notif icon ID
     */
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

    /**
     * updating notific icons
     */
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
            notificationManager?.createNotificationChannel(channel)
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
            this@PressureService.registerListn()
            this@PressureService.getLocation()
            return
        }

        fun testWrite() {
            this@PressureService.testWriteFile()
        }

        fun testLocation() {
            this@PressureService.getLocation()
        }

        fun testRx() {
            this@PressureService.testRx()
        }
    }

    /**
     * stopping service
     */
    fun stopService() {
        stopSelf()
    }

    /**
     * adding observers for presures
     */
//    fun addObservers() {
//        repository.getPressuresInIntervalLive(lastDayBegin.timeInMillis,
//                getCurrentDayEnd(lastDayBegin).timeInMillis)
//                .observe( this, Observer {
//                    Log.d(TAG, "addObservers: getting day pressure")
//                    ioScope.launch {
//                        Log.d(TAG, "addObservers: size write =" + it.size)
//                        val formatter = SimpleDateFormat("dd.MM.YY ");
//                        val dateString = formatter.format( Date(lastDayBegin.timeInMillis)) + ".txt";
//                        writeDataToFile(createTxtFile(dateString), it)
//                        lastDayBegin = getCurrentDayBegin()
//                    }
//
//        })
//    }
    fun getPressrsForLastday() {
        ioScope.launch {
            val res =  repository.getPressuresInInterval(lastDayBegin.timeInMillis,
                getCurrentDayEnd(lastDayBegin).timeInMillis)
//            val res = repository.getPressures()
            Log.d(TAG, "getPressrsForLastday: result")
                        val formatter = SimpleDateFormat("dd.MM.YY ");
                        val dateString = formatter.format( Date(lastDayBegin.timeInMillis)) + ".txt";
                        writeDataToFile(createTxtFile(dateString), res)
                        lastDayBegin = getCurrentDayBegin()
        }
    }

    fun removeObservers() {
        repository.getPressuresInIntervalLive(lastDayBegin.timeInMillis,
        getCurrentDayEnd(lastDayBegin).timeInMillis).removeObservers(this)
    }

    fun testWriteFile() {
        getPressrsForLastday()
    }

    private fun checkNextDay(): Boolean {
        return Calendar.getInstance().after(getCurrentDayEnd())
    }

    private suspend fun writeDataToFile(fileWriter: FileWriter?, data: List<Pressure>) {
        Log.d(TAG, "writeDataToFile: begin size=" + data.size)
        if (data.size  > 0) {
            val initTime = data[0].time
            for (pressure in data) {
                fileWriter!!.append(getTimeInHours((pressure.time - initTime).toInt()).toString() +
                        " " + pressure.pressure + " " + pressure.rainPower + "\n")
            }
            fileWriter!!.close()
        }
        Log.d(TAG, "writeDataToFile: close file")
    }

    private fun createTxtFile(fileName: String): FileWriter? {
        return try {
            // create a File object for the parent directory

            // create a File object for the parent directory
            val wallpaperDirectory = File("/sdcard/Pressure/")
            // have the object build the directory structure, if needed.
            // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs()
            val file = File(wallpaperDirectory,fileName)
            FileWriter(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Log.d(TAG, "getLocation: " + location!!.altitude + " " + location.latitude + " "
                    + location.longitude)
                    ioScope.launch {
                        lastAlt = location!!.altitude.toFloat()
                        zipper.generateAltit(location!!.altitude.toString())
                        zipper.generObserv()
                    }
                }
    }

    private val mNmeaListener: GpsStatus.NmeaListener = object : GpsStatus.NmeaListener {
        override fun onNmeaReceived(timestamp: Long, nmea: String?) {
            parseNmeaString(nmea!!)
        }
    }

    private val nmaListn: OnNmeaMessageListener = object : OnNmeaMessageListener {
        override fun onNmeaMessage(message: String?, timestamp: Long) {
            parseNmeaString(message!!)
        }
    }

    private fun testRx() {
        zipper.generatePress(1)
        zipper.generateAltit("1")
        zipper.generObserv()
    }


    private fun parseNmeaString(line: String) {
        if (line.startsWith("$")) {
            val tokens = line.split(",".toRegex()).toTypedArray()
            val type = tokens[0]

            // Parse altitude above sea level, Detailed description of NMEA string here http://aprs.gids.nl/nmea/#gga
            if (type.startsWith("\$GPGGA")) {
                if (!tokens[9].isEmpty()) {
                    val alt = tokens[9].toDouble()
                }
            }
        }
    }

}