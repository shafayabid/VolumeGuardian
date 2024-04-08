package com.shafay.volumeguardian.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shafay.volumeguardian.databinding.FragmentHomeBinding
import com.shafay.volumeguardian.service.DetectAudioService


class HomeFragment : Fragment() {

    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
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

        activity?.let { activity ->
            binding.tv.setOnClickListener{
                activity.startService(Intent(activity, DetectAudioService::class.java))
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.stopService(Intent(activity, DetectAudioService::class.java))
    }
}