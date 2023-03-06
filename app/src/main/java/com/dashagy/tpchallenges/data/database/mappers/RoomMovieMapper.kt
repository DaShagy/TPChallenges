package com.dashagy.tpchallenges.data.database.mappers

import com.dashagy.tpchallenges.data.database.entities.RoomMovie
import com.dashagy.tpchallenges.domain.entities.Movie

fun RoomMovie.toMovie(): Movie = Movie(id, title.orEmpty(), overview.orEmpty(), poster)

fun Movie.toRoomMovie(): RoomMovie = RoomMovie(id, title, overview, poster)

fun List<RoomMovie>.toListOfMovies(): List<Movie> = this.map { it.toMovie() }