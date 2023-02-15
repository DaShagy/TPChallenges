package com.dashagy.tpchallenges.data.repository

import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.utils.Result
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: TheMovieDatabaseAPI,
    private val movieDao: MovieDao
) : MoviesRepository {

    override suspend fun getMovieById(id: Int, isOnline: Boolean): Result<List<Movie>> {
        if (isOnline) {
            val response = api.getMovieById(id)
            if (!response.isSuccessful)
                return Result.Error(Exception(response.message()))

            val movie = response.body()?.toMovie()

            return if (movie == null) Result.Error(Exception("There was a problem"))
            else Result.Success(listOf(movie))

        } else {
            return try {
                Result.Success(listOf(movieDao.getMovieById(id).toMovie()))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun searchMovies(query: String?, isOnline: Boolean): Result<List<Movie>> {
        query?.let {
            if (isOnline) {
                val response = api.searchMovieByName(query)
                if (!response.isSuccessful) return Result.Error(Exception(response.message()))

                val movies = response.body()?.movies
                if (!movies.isNullOrEmpty()) {
                    for (movie in movies) {
                        movieDao.insertMovie(movie.toDatabaseMovie())
                    }
                    return Result.Success(movies.map{ movie -> movie.toMovie() })
                }

            } else {
                return Result.Success(movieDao.searchMovieByName(query).map { movie -> movie.toMovie() })
            }
        }
        return Result.Error(Exception("Query movies must not be null"))
    }

}
