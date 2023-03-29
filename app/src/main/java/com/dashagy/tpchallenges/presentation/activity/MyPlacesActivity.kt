package com.dashagy.tpchallenges.presentation.activity

import android.os.Bundle
import com.dashagy.tpchallenges.databinding.ActivityMyPlacesBinding

class MyPlacesActivity : BaseActivity() {

    private var _binding: ActivityMyPlacesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onMenuChangeActivityPressed() {
        onBackPressed()
    }

}