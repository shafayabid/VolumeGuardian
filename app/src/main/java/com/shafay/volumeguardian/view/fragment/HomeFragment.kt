package com.shafay.volumeguardian.view.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shafay.volumeguardian.databinding.DialogueNoAudioBinding
import com.shafay.volumeguardian.databinding.FragmentHomeBinding
import com.shafay.volumeguardian.mediapipe.AudioClassifierHelper
import com.shafay.volumeguardian.service.DetectAudioService


class HomeFragment : Fragment() {

    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                if(audioManager.isMusicActive){
                    activity?.startService(Intent(activity, DetectAudioService::class.java))
                }else{
                    context?.let{ context ->
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

}