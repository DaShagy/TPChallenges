package com.dashagy.data.database.mappers

import com.dashagy.data.database.entities.RoomMovie
import com.dashagy.domain.entities.Movie

fun RoomMovie.toMovie():Movie =
   Movie(id, title.orEmpty(), overview.orEmpty(), poster, createdAt)

fun Movie.toRoomMovie(): RoomMovie = RoomMovie(id, title, overview, poster, createdAt)

fun List<RoomMovie>.toMovieList(): List<Movie> = this.map { it.toMovie() }