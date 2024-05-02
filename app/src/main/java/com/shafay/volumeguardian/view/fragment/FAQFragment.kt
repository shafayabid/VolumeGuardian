package com.shafay.volumeguardian.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.shafay.volumeguardian.R
import com.shafay.volumeguardian.databinding.FragmentFAQBinding


class FAQFragment : Fragment() {

    private val binding by lazy {
        FragmentFAQBinding.inflate(layoutInflater)
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

        binding.btnContinue.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.FAQFragment){
                findNavController().navigate(R.id.homeFragment)
            }
        }

    }
}