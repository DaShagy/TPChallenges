package com.dashagy.tpchallenges.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    @ApplicationContext app: Context,
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
): ViewModel() {

    private var _movieState: MutableLiveData<MovieState> = MutableLiveData()
    val movieState: LiveData<MovieState>
        get() = _movieState

    suspend fun getMovieById(id: Int, isDeviceOnline: Boolean) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext getMovieByIdUseCase(id, isDeviceOnline)
        }

        _movieState.value = MovieState.Success(listOf(result))
    }

    suspend fun searchMovie(query: String?, isDeviceOnline: Boolean) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext searchMoviesUseCase(query, isDeviceOnline)
        }

        _movieState.value = MovieState.Success(result)
    }

    sealed class MovieState {
        class Success(val movies: List<Movie?>): MovieState()
        object Error: MovieState()
        object Loading: MovieState()
    }
}