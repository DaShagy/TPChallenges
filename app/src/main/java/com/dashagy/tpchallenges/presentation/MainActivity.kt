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
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.database.entities.RoomMovie
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.Constants
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var database: TPChallengesDatabase
    private lateinit var api: TheMovieDatabaseAPI

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        database = (application as TPChallengesApplication).database
        api = (application as TPChallengesApplication).theMovieDatabaseAPI

        CoroutineScope(Dispatchers.IO).launch {
            if (isOnline()) {
                val movieResponse = api.getMovieById(111)
                withContext(Dispatchers.Main) {
                    if (movieResponse.isSuccessful) updateShownMovie(
                        movieResponse.body()?.toMovie()
                    )
                }
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
        query?.let {
            CoroutineScope(Dispatchers.IO).launch {
                if (isOnline()) {
                    val movieResponse = api.searchMovieByName(query)
                    for (movie in movieResponse.body()?.movies ?: listOf()) {
                        insertMovieInDatabase(movie.toDatabaseMovie())
                    }
                    withContext(Dispatchers.Main) {
                        if (movieResponse.isSuccessful) updateShownMovie(
                            movieResponse.body()?.movies?.first()?.toMovie()
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val movieList = database.movieDao().searchMovieByName(query)
                        if (movieList.isNotEmpty()) {
                            updateShownMovie(movieList.first().toMovie())
                        }
                    }
                }
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

    private suspend fun insertMovieInDatabase(movie: RoomMovie) {
        database.movieDao().insertMovie(movie)
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