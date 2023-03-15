package com.dashagy.domain.repository

import com.dashagy.domain.entities.Movie
import com.dashagy.domain.utils.Result

interface MoviesRepository {
    fun getMovieById(id: Int): Result<Movie>
    fun getLastUpdatedMovies(): Result<List<Movie>>
    fun insertMovies(movieList: List<Movie>)
    fun insertMovie(movie: Movie)
    fun searchMovies(query: String): Result<List<Movie>>
}