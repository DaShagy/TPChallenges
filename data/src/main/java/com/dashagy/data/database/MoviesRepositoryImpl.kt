package com.dashagy.data.database

import com.dashagy.data.database.daos.MovieDao
import com.dashagy.data.database.mappers.toMovieList
import com.dashagy.data.database.mappers.toMovie
import com.dashagy.data.database.mappers.toRoomMovie
import com.dashagy.domain.entities.Movie
import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.utils.Result
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(private val movieDao: MovieDao) : MoviesRepository {

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
            movieDao.insertMovieWithTimestamp(it.toRoomMovie())
        }
    }

    override fun insertMovie(movie: Movie) {
        movieDao.insertMovieWithTimestamp(movie.toRoomMovie())
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
