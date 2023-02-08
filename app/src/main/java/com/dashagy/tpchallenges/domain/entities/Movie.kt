package com.dashagy.tpchallenges.domain.entities

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster: String?
)