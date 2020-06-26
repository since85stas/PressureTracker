package stas.batura.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class PressureService: Service() {

    private val TAG = PressureService::class.simpleName

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