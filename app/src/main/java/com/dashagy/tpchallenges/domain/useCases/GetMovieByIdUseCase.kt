package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(private val repository: MoviesRepository) {
    suspend operator fun invoke(id: Int, isOnline: Boolean): Movie? {
        return repository.getMovieById(id, isOnline).firstOrNull()
    }
}