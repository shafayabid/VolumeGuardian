package com.shafay.volumeguardian.view.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shafay.volumeguardian.databinding.DialogueNoAudioBinding
import com.shafay.volumeguardian.databinding.FragmentHomeBinding
import com.shafay.volumeguardian.service.DetectAudioService


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                if (audioManager.isMusicActive) {
                    activity?.startService(Intent(activity, DetectAudioService::class.java))
                } else {
                    context?.let { context ->
                        activity?.let { activity ->
                            showNoMusicDialogue(context, activity)
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
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
                Toast.makeText(context, "Please Raise Music Volume!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.stopService(Intent(activity, DetectAudioService::class.java))
    }

    private fun checkAndRequestAudioPermission(context: Context, activity: Activity) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (audioManager.isMusicActive) {
                    activity.startService(Intent(activity, DetectAudioService::class.java))
                } else {
                    showNoMusicDialogue(context, activity)
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO
                )
            }
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
            activity.startService(Intent(activity, DetectAudioService::class.java))
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
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            showDisabledButton()
        } else {
            showEnabledButton()
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


