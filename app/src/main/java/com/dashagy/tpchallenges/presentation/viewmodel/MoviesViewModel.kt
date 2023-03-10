package com.dashagy.tpchallenges.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
): ViewModel() {

    private var _movieState: MutableLiveData<MovieState> = MutableLiveData()
    val movieState: LiveData<MovieState>
        get() = _movieState

    fun getMovieById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _movieState.postValue(MovieState.Loading)
        getMovieByIdUseCase(id).let { result ->
            when (result) {
                is Result.Success -> _movieState.postValue(MovieState.Success(listOf(result.data)))
                is Result.Error -> _movieState.postValue(MovieState.Error(result.exception))
            }
        }
    }


    fun searchMovie(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _movieState.postValue(MovieState.Loading)
        searchMoviesUseCase(query).let { result ->
            when (result) {
                is Result.Success -> _movieState.postValue(MovieState.Success(result.data))
                is Result.Error -> _movieState.postValue(MovieState.Error(result.exception))
            }
        }
    }


    sealed class MovieState {
        class Success(val movies: List<Movie>): MovieState()
        class Error(val exception: Exception): MovieState()
        object Loading: MovieState()
    }
}