package com.dashagy.tpchallenges.presentation.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dashagy.tpchallenges.databinding.ActivityMyPlacesBinding
import com.dashagy.tpchallenges.presentation.fragments.MyPlaceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPlacesActivity : BaseActivity() {

    private var _binding: ActivityMyPlacesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyPlacesBinding.inflate(layoutInflater)
        addFragment(MyPlaceFragment.newInstance())
        setContentView(binding.root)
    }

    override fun onMenuChangeActivityPressed() {
        onBackPressed()
    }

    fun replaceFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().replace(binding.fragmentCvMyPlaces.id, fragment).addToBackStack(null).commit()
        }
    }

    private fun addFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().add(binding.fragmentCvMyPlaces.id, fragment).commit()
        }
    }

    fun showProgressBar() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            fragmentCvMyPlaces.visibility = View.GONE
        }
    }

    fun hideProgressBar() {
        binding.apply {
            progressBar.visibility = View.GONE
            fragmentCvMyPlaces.visibility = View.VISIBLE
        }
    }

}