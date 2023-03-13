package com.dashagy.tpchallenges.data.database.mappers

import com.dashagy.tpchallenges.data.database.entities.RoomMovie
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.utils.TimeUtil

fun RoomMovie.toMovie(): Movie = Movie(id, title.orEmpty(), overview.orEmpty(), poster)

fun Movie.toRoomMovie(): RoomMovie = RoomMovie(id, title, overview, poster, TimeUtil.getTimestamp())

fun List<RoomMovie>.toMovieList(): List<Movie> = this.map { it.toMovie() }