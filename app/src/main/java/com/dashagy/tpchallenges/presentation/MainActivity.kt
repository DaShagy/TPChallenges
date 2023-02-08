package com.dashagy.tpchallenges.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.database.entities.RoomMovie
import com.dashagy.tpchallenges.utils.Constants
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import com.dashagy.tpchallenges.domain.entities.Movie
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    //Room
    private val db by lazy { TPChallengesDatabase.getInstance(this) }

    //Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Api service
    private val theMovieDatabaseAPI: TheMovieDatabaseAPI = retrofit.create(TheMovieDatabaseAPI::class.java)

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.IO).launch {
            val movieResponse = theMovieDatabaseAPI.getMovieById(111)
            withContext(Dispatchers.Main) { if (movieResponse.isSuccessful) updateShownMovie(movieResponse.body()?.toMovie()) }
        }

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

    private fun searchMovie(query: String?) {
        query?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val movieResponse = theMovieDatabaseAPI.searchMovieByName(query)
                for (movie in movieResponse.body()?.movies ?: listOf()) {
                    insertMovieInDatabase(movie.toDatabaseMovie())
                }
                withContext(Dispatchers.Main) { if (movieResponse.isSuccessful) updateShownMovie(movieResponse.body()?.movies?.first()?.toMovie()) }
            }
        }
    }

    private fun updateShownMovie(movie: Movie?) {
        movie?.let {
            binding.tvMovieTitle.text = "Title: ${it.title}, Id: ${it.id}"
            binding.tvMovieOverview.text = "Overview: ${it.overview}"
            it.poster?.let { imagePath ->
                Glide.with(this).load("${Constants.API_IMAGE_BASE_URL}${imagePath}").into(binding.ivMoviePoster)
            }
        }
    }

    private suspend fun insertMovieInDatabase(movie: RoomMovie) {
        db.movieDao().insertMovie(movie)
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }
}