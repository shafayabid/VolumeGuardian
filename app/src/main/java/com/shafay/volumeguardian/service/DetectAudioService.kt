package com.shafay.volumeguardian.service

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.mediapipe.tasks.audio.core.RunningMode
import com.shafay.volumeguardian.R
import com.shafay.volumeguardian.mediapipe.AudioClassifierHelper
import com.shafay.volumeguardian.view.activity.MainActivity

class DetectAudioService : Service(), AudioClassifierHelper.ClassifierListener {

    private val TAG = "DetectAudioService"
    private lateinit var audioClassifierHelper: AudioClassifierHelper
    private lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()

        audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        initNotification()

        audioClassifierHelper =
            AudioClassifierHelper(
                context = applicationContext,
                runningMode = RunningMode.AUDIO_STREAM,
                listener = this
            )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

        NotificationManagerCompat.from(applicationContext).cancel(1)
        audioClassifierHelper.stopAudioClassification()

    }

    private fun initNotification() {

        // Create an explicit intent for an Activity in your app.
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        var builder = NotificationCompat.Builder(applicationContext, NotificationConsts.NOTIFICATION_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(NotificationConsts.NOTIFICATION_CONTENT_TITLE)
            .setContentText(NotificationConsts.NOTIFICATION_CONTENT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(1, builder.build())
        }

    }

    override fun onError(error: String) {

    }

    override fun onResult(resultBundle: AudioClassifierHelper.ResultBundle) {
        resultBundle.results.forEach { 
            it.classificationResults().forEach { 
                it.classifications().forEach { 
                    it.categories().forEach {
                        Log.d(TAG, "onResult: ${it.categoryName()}, ${it.score()}")
                        if(it.categoryName() == "Speech"){
                            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)
                        }else{
                            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND)
                        }
                    }
                }
            }
        }
    }

}