package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Movie
import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.service.MoviesService
import com.dashagy.domain.utils.Result
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(
    private val repository: MoviesRepository,
    private val service: MoviesService
) {
    operator fun invoke(id: Int): Result<Movie> {
        return when (val serviceResult = service.getMovieById(id)) {
            is Result.Success -> {
                repository.insertMovie(serviceResult.data)
                repository.getMovieById(serviceResult.data.id)
            }
            is Result.Error -> repository.getMovieById(id)
        }
    }
}