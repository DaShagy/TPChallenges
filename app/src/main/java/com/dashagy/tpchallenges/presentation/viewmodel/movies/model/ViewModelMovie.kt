package com.dashagy.tpchallenges.presentation.viewmodel.movies.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ViewModelMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster: String?,
    val createdAt: Long = 0L
): Parcelable