package com.dashagy.tpchallenges.domain.repository

import com.dashagy.tpchallenges.domain.entities.Movie

interface MoviesRepository {
    suspend fun getMovieById(id: Int, isOnline: Boolean): List<Movie>
    suspend fun searchMovies(query: String?, isOnline: Boolean): List<Movie>
}