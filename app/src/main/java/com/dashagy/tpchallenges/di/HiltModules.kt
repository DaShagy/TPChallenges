package com.dashagy.tpchallenges.di

import android.app.Application
import android.content.Context
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.database.daos.MovieDao
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext app: Context) = TPChallengesDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideMovieDao(database: TPChallengesDatabase) = database.movieDao()

    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Api service
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit) = retrofit.create(TheMovieDatabaseAPI::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    @Provides
    @ViewModelScoped
    fun provideGetMovieByIdUseCase(api: TheMovieDatabaseAPI, movieDao: MovieDao) = GetMovieByIdUseCase(api, movieDao)

    @Provides
    @ViewModelScoped
    fun provideSearchMovieUseCase(api: TheMovieDatabaseAPI, movieDao: MovieDao) = SearchMoviesUseCase(api, movieDao)
}