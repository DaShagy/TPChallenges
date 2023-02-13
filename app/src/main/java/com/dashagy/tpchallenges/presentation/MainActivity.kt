package com.dashagy.tpchallenges.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Constants
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var getMovieByIdUseCase: GetMovieByIdUseCase

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        searchMoviesUseCase = (application as TPChallengesApplication).searchMoviesUseCase
        getMovieByIdUseCase = (application as TPChallengesApplication).getMovieByIdUseCase

        CoroutineScope(Dispatchers.IO).launch {
            val movie = getMovieByIdUseCase(111, isOnline())
            withContext(Dispatchers.Main) {
                updateShownMovie(movie)
            }
        }

        binding.svMovie.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                @RequiresApi(Build.VERSION_CODES.M)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun searchMovie(query: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = searchMoviesUseCase(query, isOnline())
            withContext(Dispatchers.Main) {
                updateShownMovie(movies.firstOrNull())
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(): Boolean {
        val connectivityManager = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        with (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)){
            return if (this == null) false
            else { hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        }
    }
}