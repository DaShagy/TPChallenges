package com.dashagy.tpchallenges.presentation.viewmodel.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.utils.Result
import com.dashagy.tpchallenges.presentation.viewmodel.movies.mapper.toViewModelMovie
import com.dashagy.tpchallenges.presentation.viewmodel.movies.mapper.toViewModelMovieList
import com.dashagy.tpchallenges.presentation.viewmodel.movies.model.ViewModelMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMovieByIdUseCase: com.dashagy.domain.useCases.GetMovieByIdUseCase,
    private val searchMoviesUseCase: com.dashagy.domain.useCases.SearchMoviesUseCase,
    private val getPopularMoviesUseCase: com.dashagy.domain.useCases.GetPopularMoviesUseCase
): ViewModel() {

    private var _movieState: MutableLiveData<MovieState> = MutableLiveData()
    val movieState: LiveData<MovieState>
        get() = _movieState

    fun getMovieById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _movieState.postValue(MovieState.Loading)
        getMovieByIdUseCase(id).let { result ->
            when (result) {
                is Result.Success -> _movieState.postValue(
                    MovieState.Success(
                        listOf(result.data.toViewModelMovie())
                    )
                )
                is Result.Error -> _movieState.postValue(
                    MovieState.Error(
                        result.exception
                    )
                )
            }
        }
    }


    fun searchMovie(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _movieState.postValue(MovieState.Loading)
        searchMoviesUseCase(query).let { result ->
            when (result) {
                is Result.Success -> _movieState.postValue(
                    MovieState.Success(
                        result.data.toViewModelMovieList()
                    )
                )
                is Result.Error -> _movieState.postValue(
                    MovieState.Error(
                        result.exception
                    )
                )
            }
        }
    }

    fun getPopularMovies() = viewModelScope.launch(Dispatchers.IO) {
        _movieState.postValue(MovieState.Loading)
        getPopularMoviesUseCase().let { result ->
            when (result) {
                is Result.Success -> _movieState.postValue(
                    MovieState.Success(
                        result.data.toViewModelMovieList()
                    )
                )
                is Result.Error -> _movieState.postValue(
                    MovieState.Error(
                        result.exception
                    )
                )
            }
        }
    }


    sealed class MovieState {
        class Success(val movies: List<ViewModelMovie>): MovieState()
        class Error(val exception: Exception): MovieState()
        object Loading: MovieState()
    }
}