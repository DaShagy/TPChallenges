package com.dashagy.tpchallenges.data.database

import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.database.mappers.toMovieList
import com.dashagy.tpchallenges.data.database.mappers.toMovie
import com.dashagy.tpchallenges.data.database.mappers.toRoomMovie
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.utils.Result
import com.dashagy.tpchallenges.utils.TimeUtil
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao
) : MoviesRepository {

    override fun getMovieById(id: Int): Result<Movie> =
        movieDao.getMovieById(id).let {
            if (it.isNotEmpty()) {
                Result.Success(it.first().toMovie())
            } else {
                Result.Error(Exception(DB_MOVIE_NOT_FOUND))
            }
        }

    override fun getLastUpdatedMovies(): Result<List<Movie>> =
        movieDao.getLastUpdatedMovies().let {
            if (it.isNotEmpty()) {
                Result.Success(it.toMovieList())
            } else {
                Result.Error(Exception(DB_MOVIE_NOT_FOUND))
            }
        }

    override fun insertMovies(movieList: List<Movie>) {
        movieList.forEach {
            movieDao.insertMovie(it.toRoomMovie())
        }
    }

    override fun insertMovie(movie: Movie) {
        movieDao.insertMovie(movie.toRoomMovie())
    }

    override fun searchMovies(query: String): Result<List<Movie>> =
        movieDao.searchMovieByName(query).let {
            if (it.isNotEmpty()) {
                Result.Success(it.toMovieList())
            } else {
                Result.Error(Exception(DB_MOVIE_NOT_FOUND))
            }
        }

    companion object {
        const val DB_MOVIE_NOT_FOUND = "Movie not found"
    }

}
