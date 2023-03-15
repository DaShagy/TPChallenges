package com.dashagy.di

import android.content.Context
import com.dashagy.data.database.TPChallengesDatabase
import com.dashagy.data.database.MoviesRepositoryImpl
import com.dashagy.data.database.daos.MovieDao
import com.dashagy.domain.repository.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext app: Context) =
        TPChallengesDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideMovieDao(database: TPChallengesDatabase) = database.movieDao()

    @Provides
    @Singleton
    fun provideMoviesRepository(movieDao: MovieDao): MoviesRepository = MoviesRepositoryImpl(movieDao)
}
