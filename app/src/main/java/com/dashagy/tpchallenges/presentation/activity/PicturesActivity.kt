package com.dashagy.tpchallenges.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dashagy.tpchallenges.databinding.ActivityPicturesBinding
import com.dashagy.tpchallenges.presentation.fragments.PicturesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PicturesActivity : BaseActivity() {

    private var _binding: ActivityPicturesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPicturesBinding.inflate(layoutInflater)
        addFragment(PicturesFragment.newInstance())
        setContentView(binding.root)
    }

    override fun onMenuChangeActivityPressed() {
        onBackPressed()
    }

    fun replaceFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().replace(binding.fragmentCvPictures.id, fragment).addToBackStack(null).commit()
        }
    }

    private fun addFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().add(binding.fragmentCvPictures.id, fragment).commit()
        }
    }

    fun showProgressBar() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            fragmentCvPictures.visibility = View.GONE
        }
    }

    fun hideProgressBar() {
        binding.apply {
            progressBar.visibility = View.GONE
            fragmentCvPictures.visibility = View.VISIBLE
        }
    }

}