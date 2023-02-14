package com.dashagy.tpchallenges.presentation.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.domain.entities.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//TODO make viewmodel application agnostic when implementing repository pattern in data layer
class MoviesViewModel(application: Application): AndroidViewModel(application) {

    private val getMovieByIdUseCase = getApplication<TPChallengesApplication>().getMovieByIdUseCase
    private val searchMoviesUseCase = getApplication<TPChallengesApplication>().searchMoviesUseCase

    private var _movieState: MutableLiveData<MovieState> = MutableLiveData()
    val movieState: LiveData<MovieState>
        get() = _movieState

    suspend fun getMovieById(id: Int) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext getMovieByIdUseCase(id, getApplication<TPChallengesApplication>().isDeviceOnline)
        }

        _movieState.value = MovieState.Success(listOf(result))
    }

    suspend fun searchMovie(query: String?) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext searchMoviesUseCase(query, getApplication<TPChallengesApplication>().isDeviceOnline)
        }

        _movieState.value = MovieState.Success(result)
    }

    sealed class MovieState {
        class Success(val movies: List<Movie?>): MovieState()
        object Error: MovieState()
        object Loading: MovieState()
    }
}