package com.dashagy.tpchallenges.di

import android.content.Context
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.database.MoviesRepositoryImpl
import com.dashagy.tpchallenges.data.service.MoviesServiceImpl
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.repository.MoviesRepository
import com.dashagy.tpchallenges.domain.service.MoviesService
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Constants
import dagger.Binds
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
object AppModule {

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext app: Context) =
        TPChallengesDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideMovieDao(database: TPChallengesDatabase) = database.movieDao()

    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Api service
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): TheMovieDatabaseAPI = retrofit.create(TheMovieDatabaseAPI::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMoviesRepository(moviesRepository: MoviesRepositoryImpl): MoviesRepository

    @Binds
    abstract fun bindMoviesService(movieService: MoviesServiceImpl): MoviesService
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    @Provides
    @ViewModelScoped
    fun provideGetMovieByIdUseCase(repository: MoviesRepository, moviesService: MoviesService) = GetMovieByIdUseCase(repository, moviesService)

    @Provides
    @ViewModelScoped
    fun provideSearchMovieUseCase(repository: MoviesRepository, moviesService: MoviesService) = SearchMoviesUseCase(repository, moviesService)
}