package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.utils.Result
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(private val repository: MoviesRepository) {
    suspend operator fun invoke(id: Int, isOnline: Boolean): Result<List<Movie>> {
        return repository.getMovieById(id, isOnline)
    }
}