package com.dashagy.tpchallenges.presentation

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.presentation.model.MoviesModel
import com.dashagy.tpchallenges.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var getMovieByIdUseCase: GetMovieByIdUseCase

    private lateinit var moviesModel: MoviesModel

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        moviesModel = MoviesModel(applicationContext)

        searchMoviesUseCase = (application as TPChallengesApplication).searchMoviesUseCase
        getMovieByIdUseCase = (application as TPChallengesApplication).getMovieByIdUseCase

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
        updateShownMovie(moviesModel.searchMovie(query).firstOrNull())
    }

    private fun getMovieById(id: Int) = CoroutineScope(Dispatchers.Main).launch {
        updateShownMovie(moviesModel.getMovieById(id))
    }

    private fun updateShownMovie(movie: Movie?) {
        movie?.let {
            binding.tvMovieTitle.text = "Title: ${it.title}, Id: ${it.id}"
            binding.tvMovieOverview.text = "Overview: ${it.overview}"
            it.poster?.let { imagePath ->
                Glide.with(this).load("${Constants.API_IMAGE_BASE_URL}${imagePath}").error(R.drawable.ic_baseline_image_not_supported_24).into(binding.ivMoviePoster)
            }
        }
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }
}