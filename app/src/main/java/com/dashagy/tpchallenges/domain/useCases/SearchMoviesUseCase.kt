package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.entities.Movie

class SearchMoviesUseCase(
    private val api: TheMovieDatabaseAPI,
    private val database: TPChallengesDatabase
) {
    suspend operator fun invoke(query: String?, isOnline: Boolean): List<Movie> {
        var result = listOf<Movie>()
        query?.let {
            if (isOnline) {
                val movieResponse = api.searchMovieByName(query)
                for (movie in movieResponse.body()?.movies ?: listOf()) {
                    database.movieDao().insertMovie(movie.toDatabaseMovie())
                }
                if (movieResponse.isSuccessful)
                    result = movieResponse.body()?.movies?.map { it.toMovie() }
                        ?: listOf()
            } else {
                result = database.movieDao().searchMovieByName(query).map { it.toMovie() }
            }
        }
        return result
    }
}