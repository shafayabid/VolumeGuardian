package com.shafay.volumeguardian.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.shafay.volumeguardian.R
import com.shafay.volumeguardian.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private val binding by lazy {
        FragmentStartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {
            findNavController().apply {
                if(currentDestination?.id == R.id.startFragment){
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }

        

    }
}