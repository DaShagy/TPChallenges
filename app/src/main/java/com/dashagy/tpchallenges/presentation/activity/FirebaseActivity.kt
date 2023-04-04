package com.dashagy.tpchallenges.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dashagy.tpchallenges.databinding.ActivityPicturesBinding
import com.dashagy.tpchallenges.presentation.fragments.LocationFragment
import com.dashagy.tpchallenges.presentation.fragments.PicturesFragment
import com.tomtom.sdk.map.display.ui.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirebaseActivity : BaseActivity() {

    private var _binding: ActivityPicturesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPicturesBinding.inflate(layoutInflater)
        replaceFragment(PicturesFragment.newInstance())
        setContentView(binding.root)
    }

    override fun onMenuChangeActivityPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        with (supportFragmentManager) {
            when (fragments.last()) {
                is MapFragment,
                is LocationFragment -> popBackStack()
                else -> finish()
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().replace(binding.fragmentCvPictures.id, fragment).addToBackStack(null).commit()
        }
    }

    fun addFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().add(binding.fragmentCvPictures.id, fragment).addToBackStack(null).commit()
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