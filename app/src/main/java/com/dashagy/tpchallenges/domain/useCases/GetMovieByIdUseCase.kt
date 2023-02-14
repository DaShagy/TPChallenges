package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.entities.Movie
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(
    private val api: TheMovieDatabaseAPI,
    private val movieDao: MovieDao
) {
    suspend operator fun invoke(id: Int, isOnline: Boolean): Movie? {
        return if (isOnline) {
            api.getMovieById(id).body()?.toMovie()
        } else {
            try {
                movieDao.getMovieById(id).toMovie()
            } catch (e: Exception) {
                null
            }
        }
    }
}