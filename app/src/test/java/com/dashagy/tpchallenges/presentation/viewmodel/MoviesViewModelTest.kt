package com.dashagy.tpchallenges.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Result
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MoviesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getMovieByIdUseCase: GetMovieByIdUseCase

    @MockK
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

    @MockK
    private lateinit var dataObserver: Observer<MoviesViewModel.MovieState>

    private var movie: Movie = Movie(ID, TITLE, OVERVIEW, POSTER)
    private var exception: Exception = Exception(MSG)

    private lateinit var viewModel: MoviesViewModel

    @Before
    fun init(){
        Dispatchers.setMain(UnconfinedTestDispatcher())
        MockKAnnotations.init(this, relaxUnitFun = true)

        viewModel = MoviesViewModel(getMovieByIdUseCase, searchMoviesUseCase)

        viewModel.movieState.observeForever(dataObserver)
    }

    @After
    fun teardown() {
        viewModel.movieState.removeObserver(dataObserver)

        Dispatchers.resetMain()
    }

    @Test
    fun `if get movie by id use case returns success`() {
        runTest(UnconfinedTestDispatcher()){
            coEvery { getMovieByIdUseCase(ID) } returns Result.Success(movie)

            viewModel.getMovieById(movie.id)

            val argumentCaptor: MutableList<MoviesViewModel.MovieState> = mutableListOf()

            delay(5000)
            verify { dataObserver.onChanged(capture(argumentCaptor)) }

            with(argumentCaptor){
                assert(this[0] is MoviesViewModel.MovieState.Loading)
                assert(this[1] is MoviesViewModel.MovieState.Success)

                assertEquals(movie, (this[1] as MoviesViewModel.MovieState.Success).movies[0])
            }
        }
    }

    @Test
    fun `if get movie by id use case returns error`() {
        runTest(UnconfinedTestDispatcher()){
            coEvery { getMovieByIdUseCase(ID) } returns Result.Error(exception)

            viewModel.getMovieById(movie.id)

            val argumentCaptor: MutableList<MoviesViewModel.MovieState> = mutableListOf()

            delay(5000)
            verify { dataObserver.onChanged(capture(argumentCaptor)) }

            with(argumentCaptor){
                assert(this[0] is MoviesViewModel.MovieState.Loading)
                assert(this[1] is MoviesViewModel.MovieState.Error)

                assertEquals(MSG, (this[1] as MoviesViewModel.MovieState.Error).exception.message)
            }
        }
    }

    @Test
    fun `if search movies use case returns success`() {
        runTest(UnconfinedTestDispatcher()){
            coEvery { searchMoviesUseCase(QUERY) } returns Result.Success(listOf(movie))

            viewModel.searchMovie(QUERY)

            val argumentCaptor: MutableList<MoviesViewModel.MovieState> = mutableListOf()

            delay(5000)
            verify { dataObserver.onChanged(capture(argumentCaptor)) }

            with(argumentCaptor){
                assert(this[0] is MoviesViewModel.MovieState.Loading)
                assert(this[1] is MoviesViewModel.MovieState.Success)

                assertEquals(movie, (this[1] as MoviesViewModel.MovieState.Success).movies[0])
            }
        }
    }

    @Test
    fun `if search movies use case returns error`() {
        runTest(UnconfinedTestDispatcher()){
            coEvery { searchMoviesUseCase(QUERY) } returns Result.Error(exception)

            viewModel.searchMovie(QUERY)

            val argumentCaptor: MutableList<MoviesViewModel.MovieState> = mutableListOf()

            delay(5000)
            verify { dataObserver.onChanged(capture(argumentCaptor)) }

            with(argumentCaptor){
                assert(this[0] is MoviesViewModel.MovieState.Loading)
                assert(this[1] is MoviesViewModel.MovieState.Error)

                assertEquals(MSG, (this[1] as MoviesViewModel.MovieState.Error).exception.message)
            }
        }
    }


    companion object{
        private const val QUERY = "Scarface"
        private const val ID = 101
        private const val TITLE = "Scarface"
        private const val OVERVIEW = "Overview of scarface"
        private const val POSTER = "Some URL"
        private const val MSG = "ERROR"
    }
}