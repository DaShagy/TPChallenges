package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Movie
import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.service.MoviesService
import com.dashagy.domain.utils.Result
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository,
    private val service: MoviesService
){
    operator fun invoke(): Result<List<Movie>> {
        return when (val serviceResult = service.getPopularMovies()) {
            is Result.Success -> {
                repository.insertMovies(serviceResult.data)
                repository.getLastUpdatedMovies()
            }
            is Result.Error -> repository.getLastUpdatedMovies()
        }
    }
}
