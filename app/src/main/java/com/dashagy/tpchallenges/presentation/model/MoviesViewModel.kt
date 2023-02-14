package com.dashagy.tpchallenges.presentation.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dashagy.tpchallenges.TPChallengesApplication
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


//TODO Take out app as constructor parameter
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val app: Application,
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
): ViewModel() {

    private var _movieState: MutableLiveData<MovieState> = MutableLiveData()
    val movieState: LiveData<MovieState>
        get() = _movieState

    suspend fun getMovieById(id: Int) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext getMovieByIdUseCase(id, (app as TPChallengesApplication).isDeviceOnline)
        }

        _movieState.value = MovieState.Success(listOf(result))
    }

    suspend fun searchMovie(query: String?) {
        _movieState.value = MovieState.Loading

        val result = withContext(Dispatchers.IO) {
            return@withContext searchMoviesUseCase(query, (app as TPChallengesApplication).isDeviceOnline)
        }

        _movieState.value = MovieState.Success(result)
    }

    sealed class MovieState {
        class Success(val movies: List<Movie?>): MovieState()
        object Error: MovieState()
        object Loading: MovieState()
    }
}