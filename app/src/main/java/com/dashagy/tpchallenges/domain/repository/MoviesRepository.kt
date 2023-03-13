package com.dashagy.tpchallenges.domain.repository

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.Result

interface MoviesRepository {
    fun getMovieById(id: Int): Result<Movie>
    fun getLastUpdatedMovies(): Result<List<Movie>>
    fun insertMovies(movieList: List<Movie>)
    fun insertMovie(movie: Movie)
}