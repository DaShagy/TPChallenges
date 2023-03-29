package com.dashagy.tpchallenges.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.presentation.fragments.MovieDetailsFragment
import com.dashagy.tpchallenges.presentation.fragments.MoviesListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        addFragment(MoviesListFragment.newInstance())
        setContentView(binding.root)
    }

    override fun onMenuChangeActivityPressed() {
        startActivity(Intent(this, MyPlacesActivity::class.java))
    }

    fun replaceFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().replace(binding.fragmentCvMain.id, fragment).addToBackStack(null).commit()
        }
    }

    private fun addFragment(fragment: Fragment) {
        with (supportFragmentManager) {
            beginTransaction().add(binding.fragmentCvMain.id, fragment).commit()
        }
    }


    fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        with (supportFragmentManager) {
            when (fragments.last()) {
                is MovieDetailsFragment -> popBackStack()
                else -> super.onBackPressed()
            }
        }
    }

    fun showProgressBar() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            fragmentCvMain.visibility = View.GONE
        }
    }

    fun hideProgressBar() {
        binding.apply {
            progressBar.visibility = View.GONE
            fragmentCvMain.visibility = View.VISIBLE
        }
    }
}