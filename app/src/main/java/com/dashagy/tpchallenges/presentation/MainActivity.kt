package com.dashagy.tpchallenges.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.presentation.viewmodel.MoviesViewModel
import com.dashagy.tpchallenges.utils.Constants
import com.dashagy.tpchallenges.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val moviesViewModel: MoviesViewModel by viewModels()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        moviesViewModel.movieState.observe(this, ::updateShownMovie)

        getMovieById(111)

        binding.svMovie.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchMovie(query)
                    hideKeyboard()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

        setContentView(binding.root)
    }

    private fun searchMovie(query: String?) = CoroutineScope(Dispatchers.Main).launch {
        moviesViewModel.searchMovie(query, (application as TPChallengesApplication).isDeviceOnline)
    }

    private fun getMovieById(id: Int) = CoroutineScope(Dispatchers.Main).launch {
        moviesViewModel.getMovieById(id, (application as TPChallengesApplication).isDeviceOnline)
    }

    private fun updateShownMovie(state: MoviesViewModel.MovieState) {
        when (state) {
            MoviesViewModel.MovieState.Error -> showProgressBar()
            MoviesViewModel.MovieState.Loading -> showProgressBar()
            is MoviesViewModel.MovieState.Success -> {
                hideProgressBar()
                state.movies.firstOrNull()?.let {
                    binding.tvMovieTitle.text = "Title: ${it.title}, Id: ${it.id}"
                    binding.tvMovieOverview.text = "Overview: ${it.overview}"
                    binding.ivMoviePoster.loadImage(this, it.poster)
                }
            }
        }
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    private fun showProgressBar() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvMovieTitle.visibility = View.GONE
            tvMovieOverview.visibility = View.GONE
            ivMoviePoster.visibility = View.GONE
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBar.visibility = View.GONE
            tvMovieTitle.visibility = View.VISIBLE
            tvMovieOverview.visibility = View.VISIBLE
            ivMoviePoster.visibility = View.VISIBLE
        }
    }
}