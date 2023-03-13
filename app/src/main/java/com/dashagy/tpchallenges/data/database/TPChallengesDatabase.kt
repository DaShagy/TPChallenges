package com.dashagy.tpchallenges.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.database.entities.RoomMovie

@Database(entities = [RoomMovie::class], version = 2)
abstract class TPChallengesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    //Implementing Singleton design pattern

    companion object {
        private var instance: TPChallengesDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): TPChallengesDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, TPChallengesDatabase::class.java,
                    "movies-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!
        }
    }
}