package com.shafay.volumeguardian.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shafay.volumeguardian.databinding.FragmentHomeBinding
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
                activity?.startService(Intent(activity, DetectAudioService::class.java))
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                binding.tv.setOnClickListener{
                    checkAndRequestAudioPermission(context, activity)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.stopService(Intent(activity, DetectAudioService::class.java))
    }

    private fun checkAndRequestAudioPermission(context: Context, activity: Activity){
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                activity.startService(Intent(activity, DetectAudioService::class.java))
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO)
            }
        }
    }

}