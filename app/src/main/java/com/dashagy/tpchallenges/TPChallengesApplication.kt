package com.dashagy.tpchallenges

import android.app.Application
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TPChallengesApplication: Application() {

    //Room
    private val database get() =  TPChallengesDatabase.getInstance(this)

    //Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Api service
    private val theMovieDatabaseAPI: TheMovieDatabaseAPI = retrofit.create(TheMovieDatabaseAPI::class.java)

    val searchMoviesUseCase get() = SearchMoviesUseCase(theMovieDatabaseAPI, database)
    val getMovieByIdUseCase get() = GetMovieByIdUseCase(theMovieDatabaseAPI, database)

    override fun onCreate() {
        super.onCreate()
    }
}