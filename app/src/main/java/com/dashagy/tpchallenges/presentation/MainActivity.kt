package com.dashagy.tpchallenges.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.presentation.viewmodel.MoviesViewModel
import com.dashagy.tpchallenges.presentation.utils.clean
import com.dashagy.tpchallenges.presentation.utils.loadImage
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

        binding.svMovie.apply {
            setOnQueryTextListener(
                object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchMovie(query.orEmpty())
                        cleanSearchView(this@apply)
                        hideKeyboard()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
        }

        setContentView(binding.root)
    }

    private fun searchMovie(query: String) = CoroutineScope(Dispatchers.Main).launch {
        moviesViewModel.searchMovie(query)
    }

    private fun getMovieById(id: Int) = CoroutineScope(Dispatchers.Main).launch {
        moviesViewModel.getMovieById(id)
    }

    private fun updateShownMovie(state: MoviesViewModel.MovieState) {
        when (state) {
            is MoviesViewModel.MovieState.Error -> {
                hideProgressBar()
                Toast.makeText(this, state.exception.message, Toast.LENGTH_SHORT).show()
            }
            MoviesViewModel.MovieState.Loading -> showProgressBar()
            is MoviesViewModel.MovieState.Success -> {
                hideProgressBar()
                state.movies.firstOrNull()?.let { movie ->
                    binding.tvMovieTitle.text = movie.title
                    binding.tvMovieOverview.text = movie.overview
                    binding.ivMoviePoster.loadImage(this, movie.poster)
                }
            }
        }
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    private fun cleanSearchView(sv: androidx.appcompat.widget.SearchView) {
        sv.clean()
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