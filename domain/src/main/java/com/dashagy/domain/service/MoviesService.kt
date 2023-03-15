package com.dashagy.domain.service

import com.dashagy.domain.entities.Movie
import com.dashagy.domain.utils.Result

interface MoviesService {
    fun searchMovies(query: String): Result<List<Movie>>
    fun getMovieById(movieId: Int): Result<Movie>
    fun getPopularMovies(): Result<List<Movie>>
}