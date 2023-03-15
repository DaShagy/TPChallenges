package com.dashagy.tpchallenges.presentation.viewmodel.movies.mapper

import com.dashagy.domain.entities.Movie
import com.dashagy.tpchallenges.presentation.viewmodel.movies.model.ViewModelMovie

fun Movie.toViewModelMovie(): ViewModelMovie = ViewModelMovie(id, title, overview, poster, createdAt)

fun ViewModelMovie.toMovie(): Movie = Movie(id, title, overview, poster, createdAt)

fun List<Movie>.toViewModelMovieList(): List<ViewModelMovie> = this.map { it.toViewModelMovie() }