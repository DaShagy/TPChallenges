package com.dashagy.tpchallenges.data.service.responseSchemas

import com.google.gson.annotations.SerializedName

data class MovieListResponseSchema(
    @SerializedName("results") val movies: List<MovieResponseSchema>
)
