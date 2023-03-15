package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Movie
import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.service.MoviesService
import com.dashagy.domain.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetPopularMoviesUseCaseTest {

    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    @MockK
    private lateinit var moviesRepository: MoviesRepository

    @MockK
    private lateinit var moviesService: MoviesService

    private var movie: Movie = Movie(ID, TITLE, OVERVIEW, POSTER)
    private var exception: Exception = Exception(MSG)

    @MockK
    private lateinit var movieList: List<Movie>

    @Before
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        getPopularMoviesUseCase = GetPopularMoviesUseCase(moviesRepository, moviesService)
    }

    @Test
    fun `get popular movies use case from service returns success`() {
        every { moviesRepository.getLastUpdatedMovies() } returns Result.Success(movieList)
        every { moviesService.getPopularMovies() } returns Result.Success(movieList)

        every { movieList.first() } returns movie
        every { movieList.isEmpty() } returns false

        val result = getPopularMoviesUseCase()

        verify { moviesRepository.insertMovies(movieList) }
        verify { moviesRepository.getLastUpdatedMovies() }

        Assert.assertEquals(movieList, (result as Result.Success).data)
        Assert.assertEquals(movie, result.data.first())
    }

    @Test
    fun `get popular movies use case from database returns success`() {
        every { moviesRepository.getLastUpdatedMovies() } returns Result.Success(movieList)
        every { moviesService.getPopularMovies() } returns Result.Error(exception)

        every { movieList.first() } returns movie
        every { movieList.isEmpty() } returns false

        val result = getPopularMoviesUseCase()

        verify { moviesRepository.getLastUpdatedMovies() }

        Assert.assertEquals(movieList, (result as Result.Success).data)
        Assert.assertEquals(movie, result.data.first())
    }

    @Test
    fun `get popular movies use case from database returns error`() {
        every { moviesRepository.getLastUpdatedMovies() } returns Result.Error(exception)
        every { moviesService.getPopularMovies() } returns Result.Error(exception)

        val result = getPopularMoviesUseCase()

        verify { moviesRepository.getLastUpdatedMovies() }

        Assert.assertEquals(MSG, (result as Result.Error).exception.message)
    }

    companion object {
        private const val ID = 101
        private const val TITLE = "Scarface"
        private const val OVERVIEW = "Overview of scarface"
        private const val POSTER = "Some URL"
        private const val MSG = "ERROR"
    }
}