package com.dashagy.tpchallenges.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dashagy.tpchallenges.domain.entities.Movie

@Entity(tableName = "Movies")
data class RoomMovie(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "poster_path") val poster: String?
)