package com.dashagy.tpchallenges.domain.useCases

import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.domain.service.MoviesService
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchMoviesUseCaseTest {

    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

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

        searchMoviesUseCase = SearchMoviesUseCase(moviesRepository, moviesService)
    }

    @Test
    fun `search movies use case from service returns success`() {
        every { moviesRepository.searchMovies(QUERY) } returns Result.Success(movieList)
        every { moviesService.searchMovies(QUERY) } returns Result.Success(movieList)

        val result = searchMoviesUseCase(QUERY)

        verify { moviesRepository.insertMovies(movieList) }
        verify { moviesRepository.searchMovies(QUERY) }

        Assert.assertEquals(movieList, (result as Result.Success).data)
    }

    @Test
    fun `search movies use case from database returns success`() {
        every { moviesRepository.searchMovies(QUERY) } returns Result.Success(movieList)
        every { moviesService.searchMovies(QUERY) } returns Result.Error(exception)

        val result = searchMoviesUseCase(QUERY)

        verify { moviesRepository.searchMovies(QUERY) }

        Assert.assertEquals(movieList, (result as Result.Success).data)
    }

    @Test
    fun `search movies use case from database returns error`() {
        every { moviesRepository.searchMovies(QUERY) } returns Result.Error(exception)
        every { moviesService.searchMovies(QUERY) } returns Result.Error(exception)

        val result = searchMoviesUseCase(QUERY)

        verify { moviesRepository.searchMovies(QUERY) }

        Assert.assertEquals(MSG, (result as Result.Error).exception.message)
    }

    @Test
    fun `search movies use case should get movie by id from database if database search returns error - success`(){
        every { moviesRepository.searchMovies(QUERY) } returns Result.Error(exception)
        every { moviesService.searchMovies(QUERY) } returns Result.Success(movieList)

        every { movieList.first() } returns movie
        every { movieList.isEmpty() } returns false
        every { moviesRepository.getMovieById(movie.id) } returns Result.Success(movie)

        val result = searchMoviesUseCase(QUERY)

        verify { moviesRepository.insertMovies(movieList) }
        verify { moviesRepository.searchMovies(QUERY) }
        verify { moviesRepository.getMovieById(movie.id) }

        Assert.assertEquals(movie, (result as Result.Success).data.first())
    }

    @Test
    fun `search movies use case should get movie by id from database if database search returns error - error`(){
        every { moviesRepository.searchMovies(QUERY) } returns Result.Error(exception)
        every { moviesService.searchMovies(QUERY) } returns Result.Success(movieList)

        every { movieList.first() } returns movie
        every { movieList.isEmpty() } returns false
        every { moviesRepository.getMovieById(movie.id) } returns Result.Error(exception)

        val result = searchMoviesUseCase(QUERY)

        verify { moviesRepository.insertMovies(movieList) }
        verify { moviesRepository.searchMovies(QUERY) }
        verify { moviesRepository.getMovieById(movie.id) }

        Assert.assertEquals(MSG, (result as Result.Error).exception.message)
    }

    companion object {
        private const val QUERY = "Scarface"
        private const val ID = 101
        private const val TITLE = "Scarface"
        private const val OVERVIEW = "Overview of scarface"
        private const val POSTER = "Some URL"
        private const val MSG = "ERROR"
    }
}