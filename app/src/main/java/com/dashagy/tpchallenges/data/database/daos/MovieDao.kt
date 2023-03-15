package com.dashagy.tpchallenges.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dashagy.tpchallenges.data.database.entities.RoomMovie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: RoomMovie)

    @Query("SELECT * FROM Movies WHERE id = :id")
    fun getMovieById(id: Int): List<RoomMovie>

    @Query("SELECT * FROM Movies ORDER BY created_at DESC LIMIT 20")
    fun getLastUpdatedMovies(): List<RoomMovie>

    @Query("SELECT * FROM Movies WHERE title LIKE '%' || :query || '%' ORDER BY created_at DESC LIMIT 2")
    fun searchMovieByName(query: String): List<RoomMovie>
}