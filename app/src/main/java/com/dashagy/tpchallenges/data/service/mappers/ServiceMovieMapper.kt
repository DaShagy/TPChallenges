package com.dashagy.tpchallenges.data.service.mappers

import com.dashagy.tpchallenges.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.tpchallenges.data.service.responseSchemas.MovieResponseSchema
import com.dashagy.tpchallenges.domain.entities.Movie

fun MovieResponseSchema.toMovie(): Movie = Movie(id, title, overview, poster)

fun MovieListResponseSchema.toMovieList(): List<Movie> = this.movies.map { it.toMovie() }