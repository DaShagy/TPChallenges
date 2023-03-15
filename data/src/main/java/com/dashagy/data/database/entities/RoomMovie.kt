package com.dashagy.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dashagy.domain.entities.Movie

@Entity(tableName = "Movies")
data class RoomMovie(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "poster_path") val poster: String?,
    @ColumnInfo(name = "created_at") var createdAt: Long
)