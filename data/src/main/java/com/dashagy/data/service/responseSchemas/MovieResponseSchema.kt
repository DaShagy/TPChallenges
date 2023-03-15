package com.dashagy.data.service.responseSchemas

import com.google.gson.annotations.SerializedName

data class MovieResponseSchema(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val poster: String?
)