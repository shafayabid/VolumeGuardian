package com.shafay.volumeguardian.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DetectAudioService: Service() {

    private val TAG = "DetectAudioService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}