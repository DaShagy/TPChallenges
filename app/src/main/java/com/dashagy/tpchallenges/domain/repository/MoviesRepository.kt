package com.dashagy.tpchallenges.domain.repository

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.Result

interface MoviesRepository {
    suspend fun getMovieById(id: Int, isOnline: Boolean): Result<List<Movie>>
    suspend fun searchMovies(query: String?, isOnline: Boolean): Result<List<Movie>>
}