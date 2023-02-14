package com.dashagy.tpchallenges.data.repository

import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: TheMovieDatabaseAPI,
    private val movieDao: MovieDao
) : MoviesRepository {

    override suspend fun getMovieById(id: Int, isOnline: Boolean): List<Movie> {
        return if (isOnline) {
            api.getMovieById(id).body()?.toMovie()?.let { listOf(it) } ?: listOf()
        } else {
            try {
                listOf(movieDao.getMovieById(id).toMovie())
            } catch (e: Exception) {
                listOf()
            }
        }
    }

    override suspend fun searchMovies(query: String?, isOnline: Boolean): List<Movie> {
        var result = listOf<Movie>()
        query?.let {
            if (isOnline) {
                val movieResponse = api.searchMovieByName(query)
                for (movie in movieResponse.body()?.movies ?: listOf()) {
                    movieDao.insertMovie(movie.toDatabaseMovie())
                }
                if (movieResponse.isSuccessful)
                    result = movieResponse.body()?.movies?.map { it.toMovie() }
                        ?: listOf()
            } else {
                result = movieDao.searchMovieByName(query).map { it.toMovie() }
            }
        }
        return result
    }

}
