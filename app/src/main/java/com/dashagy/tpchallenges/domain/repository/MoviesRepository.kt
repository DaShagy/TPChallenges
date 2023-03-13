package com.dashagy.tpchallenges.domain.repository

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.Result

interface MoviesRepository {
    fun getMovieById(id: Int): Result<Movie>
    fun searchMovies(query: String): Result<List<Movie>>
    fun insertMovies(movieList: List<Movie>)
    fun insertMovie(movie: Movie)
    fun getPopularMovies(): Result<List<Movie>>
}