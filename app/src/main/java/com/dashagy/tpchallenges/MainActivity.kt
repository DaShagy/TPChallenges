package com.dashagy.tpchallenges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dashagy.tpchallenges.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

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
            val movieResponse = theMovieDatabaseAPI.getMovieById(550)
            withContext(Dispatchers.Main) { if (movieResponse.isSuccessful) updateShownMovie(movieResponse.body()) }
        }

        setContentView(binding.root)
    }

    private fun updateShownMovie(movie: Movie?) {
        movie?.let {
            binding.tvMovieTitle.text = "Title: ${it.title}, Id: ${it.id}, poster path: ${it.posterPath}"
            binding.tvMovieOverview.text = "Overview: ${it.overview}"
        }
    }
}