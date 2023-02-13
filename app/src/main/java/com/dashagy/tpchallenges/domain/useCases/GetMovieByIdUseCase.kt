package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.entities.Movie

class GetMovieByIdUseCase(
    private val api: TheMovieDatabaseAPI,
    private val database: TPChallengesDatabase
) {
    suspend operator fun invoke(id: Int, isOnline: Boolean): Movie? {
        return if (isOnline) {
            api.getMovieById(id).body()?.toMovie()
        } else {
            try {
                database.movieDao().getMovieById(id).toMovie()
            } catch (e: Exception) {
                null
            }
        }
    }
}