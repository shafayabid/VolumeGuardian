package com.shafay.volumeguardian.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shafay.volumeguardian.R
import com.shafay.volumeguardian.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}