package com.dashagy.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dashagy.data.database.entities.RoomMovie

@Dao
abstract class MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovie(movie: RoomMovie)

    fun insertMovieWithTimestamp(movie: RoomMovie) {
        insertMovie(movie.apply { if (createdAt == 0L)
            createdAt = System.currentTimeMillis()
        })
    }

    @Query("SELECT * FROM Movies WHERE id = :id")
    abstract fun getMovieById(id: Int): List<RoomMovie>

    @Query("SELECT * FROM Movies ORDER BY created_at DESC LIMIT 20")
    abstract fun getLastUpdatedMovies(): List<RoomMovie>

    @Query("SELECT * FROM Movies WHERE title LIKE '%' || :query || '%' ORDER BY created_at DESC LIMIT 20")
    abstract fun searchMovieByName(query: String): List<RoomMovie>
}