package com.dashagy.data.service.mappers

import com.dashagy.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.data.service.responseSchemas.MovieResponseSchema
import com.dashagy.domain.entities.Movie

fun MovieResponseSchema.toMovie(): Movie = Movie(id, title, overview, poster)

fun MovieListResponseSchema.toMovieList(): List<Movie> = this.movies.map { it.toMovie() }