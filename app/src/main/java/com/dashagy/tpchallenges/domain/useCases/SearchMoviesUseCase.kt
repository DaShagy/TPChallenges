package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val repository: MoviesRepository) {
    suspend operator fun invoke(query: String?, isOnline: Boolean): List<Movie> {
        return repository.searchMovies(query, isOnline)
    }
}