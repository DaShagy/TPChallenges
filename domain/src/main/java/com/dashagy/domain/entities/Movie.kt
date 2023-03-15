package com.dashagy.domain.entities

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster: String?,
    val createdAt: Long = 0L
)