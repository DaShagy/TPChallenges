package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.domain.service.MoviesService
import com.dashagy.tpchallenges.utils.Result
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository,
    private val service: MoviesService
) {
    operator fun invoke(query: String): Result<List<Movie>> {
        return when (val serviceResponse = service.searchMovies(query)) {
            is Result.Success -> {
                repository.insertMovies(serviceResponse.data)
                when (val movieListFromDatabase = repository.searchMovies(query)) {
                    is Result.Success -> movieListFromDatabase
                    is Result.Error -> when (val movieFromDatabase = repository.getMovieById(serviceResponse.data.first().id)){
                        is Result.Success -> Result.Success(listOf(movieFromDatabase.data))
                        is Result.Error -> movieFromDatabase
                    }
                }
            }
            is Result.Error -> repository.searchMovies(query)
        }
    }
}