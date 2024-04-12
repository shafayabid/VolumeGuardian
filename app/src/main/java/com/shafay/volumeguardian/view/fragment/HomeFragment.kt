package com.shafay.volumeguardian.view.fragment

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shafay.volumeguardian.databinding.DialogueNoAudioBinding
import com.shafay.volumeguardian.databinding.DialoguePermissionBinding
import com.shafay.volumeguardian.databinding.FragmentHomeBinding
import com.shafay.volumeguardian.service.DetectAudioService


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val sharedPref by lazy {
        activity?.getPreferences(Context.MODE_PRIVATE)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                context?.let { context ->
                    activity?.let { activity ->
                        audioManager?.let {
                            if (it.isMusicActive) {
                                startService(context, activity)
                            } else {
                                if (!isMyServiceRunning(context, DetectAudioService::class.java)) {
                                    showNoMusicDialogue(context, activity)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Volume Guardian is Already Running",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            } else {

            }
        }

    private val requestNotificationLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {

        }

    private var audioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkAndRequestNotificationPermission(it)
            }
            audioManager = it.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            checkAudio()
        }

        activity?.let {
            initAudioListener(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            activity?.let { activity ->
                binding.ivSpeaker.setOnClickListener {
                    checkAndRequestAudioPermission(context, activity)
                }
            }
            binding.ivSpeakerDisabled.setOnClickListener {
                Toast.makeText(context, "Please Increase Music Volume!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        activity?.stopService(Intent(activity, DetectAudioService::class.java))
    }

    private fun checkAndRequestAudioPermission(context: Context, activity: Activity) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                audioManager?.let {
                    if (it.isMusicActive) {
                        startService(context, activity)
                    } else {
                        if (!isMyServiceRunning(context, DetectAudioService::class.java)) {
                            showNoMusicDialogue(context, activity)
                        } else {
                            Toast.makeText(
                                context,
                                "Volume Guardian is Already Running",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            else -> {
                sharedPref?.let {
                    var count = it.getInt("micCount", 0)
                    if (count > 1) {
                        showPermissionDialogue(context)
                    } else {
                        requestPermissionLauncher.launch(
                            Manifest.permission.RECORD_AUDIO
                        )
                        count += 1
                        it.edit().putInt("micCount", count).apply()
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            else -> {
                requestNotificationLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun startService(context: Context, activity: Activity) {
        if (!isMyServiceRunning(context, DetectAudioService::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(activity, DetectAudioService::class.java))
            } else {
                activity.startService(Intent(activity, DetectAudioService::class.java))
            }
        } else {
            Toast.makeText(context, "Volume Guardian is Already Running", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoMusicDialogue(context: Context, activity: Activity) {
        val dialogueBinding = DialogueNoAudioBinding.inflate(layoutInflater)
        val dialogue = Dialog(context)
        dialogue.setContentView(dialogueBinding.root)
        dialogue.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialogue.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
        dialogue.setCancelable(false)

        dialogueBinding.btnContinue.setOnClickListener {
            startService(context, activity)
            dialogue.dismiss()
        }

        dialogueBinding.btnCancel.setOnClickListener {
            dialogue.dismiss()
        }

        dialogue.show()
    }

    private fun showPermissionDialogue(context: Context) {
        val dialogueBinding = DialoguePermissionBinding.inflate(layoutInflater)
        val dialogue = Dialog(context)
        dialogue.setContentView(dialogueBinding.root)
        dialogue.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogue.setCancelable(false)

        dialogueBinding.btnAllow.setOnClickListener {
            goToSettings(context)
            dialogue.dismiss()
        }

        dialogueBinding.btnCancel.setOnClickListener {
            dialogue.dismiss()
        }

        dialogue.show()
    }

    private fun initAudioListener(activity: Activity) {
        val mSettingsContentObserver = SettingsContentObserver(Handler())
        activity.applicationContext.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            mSettingsContentObserver
        );
    }

    private fun checkAudio() {
        audioManager?.let {
            if (it.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                showDisabledButton()
            } else {
                showEnabledButton()
            }
        }
    }

    private fun showEnabledButton() {
        binding.ivSpeaker.visibility = View.VISIBLE
        binding.ivSpeakerDisabled.visibility = View.GONE

        binding.tvActivateVolume.visibility = View.VISIBLE
        binding.tvDisabledVolume.visibility = View.GONE
        binding.tvRaiseVolume.visibility = View.GONE
    }

    private fun showDisabledButton() {
        binding.ivSpeaker.visibility = View.GONE
        binding.ivSpeakerDisabled.visibility = View.VISIBLE

        binding.tvActivateVolume.visibility = View.GONE
        binding.tvDisabledVolume.visibility = View.VISIBLE
        binding.tvRaiseVolume.visibility = View.VISIBLE
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        manager?.let {
            for (service in it.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        }
        return false
    }

    private fun goToSettings(context: Context){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.setData(uri)
        startActivity(intent)
    }

    inner class SettingsContentObserver(handler: Handler?) : ContentObserver(handler) {
        override fun deliverSelfNotifications(): Boolean {
            return super.deliverSelfNotifications()
        }

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            checkAudio()
        }
    }

}


