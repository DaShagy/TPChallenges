package com.dashagy.tpchallenges.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.domain.service.MoviesService
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMovieByIdUseCaseTest {

    private lateinit var getMovieByIdUseCase: GetMovieByIdUseCase

    @MockK
    private lateinit var moviesRepository: MoviesRepository

    @MockK
    private lateinit var moviesService: MoviesService

    private var movie: Movie = Movie(ID, TITLE, OVERVIEW, POSTER)

    @Before
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        getMovieByIdUseCase = GetMovieByIdUseCase(moviesRepository, moviesService)
    }

    @Test
    fun `get movie by id use case from service returns success`() {
        every { moviesRepository.getMovieById(ID) } returns Result.Success(movie)
        every { moviesService.getMovieById(ID) } returns Result.Success(movie)

        val result = getMovieByIdUseCase(movie.id)

        verify { moviesRepository.insertMovie(movie) }
        verify { moviesRepository.getMovieById(movie.id) }

        assertEquals(movie, (result as Result.Success).data)
    }

    @Test
    fun `get movie by id use case from database returns success`() {
        every { moviesRepository.getMovieById(ID) } returns Result.Success(movie)
        every { moviesService.getMovieById(ID) } returns Result.Error(Exception(MSG))

        val result = getMovieByIdUseCase(movie.id)

        verify { moviesRepository.getMovieById(movie.id) }

        assertEquals(movie, (result as Result.Success).data)
    }

    @Test
    fun `get movie by id use case from database returns error`() {
        every { moviesRepository.getMovieById(ID) } returns Result.Error(Exception(MSG))
        every { moviesService.getMovieById(ID) } returns Result.Error(Exception(MSG))

        val result = getMovieByIdUseCase(movie.id)

        verify { moviesRepository.getMovieById(movie.id) }

        assertEquals(MSG, (result as Result.Error).exception.message)
    }

    companion object {
        private const val ID = 101
        private const val TITLE = "Scarface"
        private const val OVERVIEW = "Overview of scarface"
        private const val POSTER = "Some URL"
        private const val MSG = "ERROR"
    }
}