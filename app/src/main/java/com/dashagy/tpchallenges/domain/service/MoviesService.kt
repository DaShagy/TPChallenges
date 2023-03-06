package com.dashagy.tpchallenges.domain.service

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.Result

interface MoviesService {
    fun searchMovies(query: String): Result<List<Movie>>
    fun getMovieById(movieId: Int): Result<Movie>
}