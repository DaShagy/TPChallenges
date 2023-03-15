package com.dashagy.data.service

import com.dashagy.data.service.api.TheMovieDatabaseAPI
import com.dashagy.data.service.mappers.toMovie
import com.dashagy.data.service.mappers.toMovieList
import com.dashagy.domain.entities.Movie
import com.dashagy.domain.utils.Result
import com.dashagy.domain.service.MoviesService
import javax.inject.Inject

class MoviesServiceImpl @Inject constructor(private val api: TheMovieDatabaseAPI): MoviesService {

    override fun searchMovies(query: String): Result<List<Movie>> {
        try {
            val callResponse = api.searchMovieByName(query)
            val response = callResponse.execute()
            if (response.isSuccessful) response.body()?.let { movieSchemaList ->
                return if (movieSchemaList.movies.isNotEmpty()) Result.Success(movieSchemaList.toMovieList())
                else Result.Error(Exception(SERVICE_MOVIE_NOT_FOUND))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Error(Exception(SERVICE_MOVIE_NOT_FOUND))
    }

    override fun getMovieById(movieId: Int): Result<Movie> {
        try {
            val callResponse = api.getMovieById(movieId)
            val response = callResponse.execute()
            if (response.isSuccessful) response.body()?.let { movieSchema ->
                return Result.Success(movieSchema.toMovie())
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Error(Exception(SERVICE_MOVIE_NOT_FOUND))
    }

    override fun getPopularMovies(): Result<List<Movie>> {
        try {
            val callResponse = api.getPopularMovies()
            val response = callResponse.execute()
            if (response.isSuccessful) response.body()?.let { movieSchemaList ->
                return if (movieSchemaList.movies.isNotEmpty()) Result.Success(movieSchemaList.toMovieList())
                else Result.Error(Exception(SERVICE_MOVIE_NOT_FOUND))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Error(Exception(SERVICE_MOVIE_NOT_FOUND))
    }

    companion object {
        const val SERVICE_MOVIE_NOT_FOUND = "Movie not found"
    }
}