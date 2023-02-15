package com.dashagy.tpchallenges.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
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

        when (result){
            is Result.Error -> _movieState.value = MovieState.Error(result.exception)
            is Result.Success -> _movieState.value = MovieState.Success(result.data)
        }
    }

    suspend fun searchMovie(query: String?, isDeviceOnline: Boolean) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext searchMoviesUseCase(query, isDeviceOnline)
        }

        when (result){
            is Result.Error -> _movieState.value = MovieState.Error(result.exception)
            is Result.Success -> _movieState.value = MovieState.Success(result.data)
        }
    }

    sealed class MovieState {
        class Success(val movies: List<Movie>): MovieState()
        class Error(val exception: Exception): MovieState()
        object Loading: MovieState()
    }
}