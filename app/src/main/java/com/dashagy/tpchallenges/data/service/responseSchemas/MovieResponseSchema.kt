package com.dashagy.tpchallenges.data.service.responseSchemas

import com.dashagy.tpchallenges.domain.entities.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponseSchema(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val poster: String?
) {
    fun toMovie(): Movie = Movie(id, title, overview, poster)
}