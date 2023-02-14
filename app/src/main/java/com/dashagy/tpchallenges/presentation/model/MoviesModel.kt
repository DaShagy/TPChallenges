package com.dashagy.tpchallenges.presentation.model

import android.content.Context
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesModel(private val context: Context) {

    private val searchMoviesUseCase: SearchMoviesUseCase = (context as TPChallengesApplication).searchMoviesUseCase
    private val getMovieByIdUseCase: GetMovieByIdUseCase = (context as TPChallengesApplication).getMovieByIdUseCase

    suspend fun getMovieById(id: Int) =
        withContext(Dispatchers.IO) {
            return@withContext getMovieByIdUseCase(id, (context as TPChallengesApplication).isDeviceOnline)
        }

    suspend fun searchMovie(query: String?) =
        withContext(Dispatchers.IO) {
            return@withContext searchMoviesUseCase(query, (context as TPChallengesApplication).isDeviceOnline)
        }
}