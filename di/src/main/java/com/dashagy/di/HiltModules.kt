package com.dashagy.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.dashagy.data.database.TPChallengesDatabase
import com.dashagy.data.database.MoviesRepositoryImpl
import com.dashagy.data.database.daos.MovieDao
import com.dashagy.data.service.MoviesServiceImpl
import com.dashagy.data.service.api.TheMovieDatabaseAPI
import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.service.MoviesService
import com.dashagy.domain.useCases.GetMovieByIdUseCase
import com.dashagy.domain.useCases.SearchMoviesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val API = "api_key"

    @Singleton
    @Provides
    fun provideMoviesDatabase(@ApplicationContext app: Context) =
        TPChallengesDatabase.getInstance(app)

    @Singleton
    @Provides
    fun provideMovieDao(database: TPChallengesDatabase) = database.movieDao()

    @Provides
    fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val defaultRequest = chain.request()
                val defaultHttpUrl = defaultRequest.url
                val httpUrl = defaultHttpUrl.newBuilder()
                    .addQueryParameter(API, BuildConfig.API_KEY)
                    .build()
                val requestBuilder = defaultRequest.newBuilder().url(httpUrl)
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(chuckerInterceptor)
            .build()
    }

    @Provides
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    //Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Api service
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): TheMovieDatabaseAPI = retrofit.create(TheMovieDatabaseAPI::class.java)

    @Provides
    @Singleton
    fun provideMoviesRepository(movieDao: MovieDao): MoviesRepository = MoviesRepositoryImpl(movieDao)

    @Provides
    @Singleton
    fun provideMoviesService(api: TheMovieDatabaseAPI): MoviesService = MoviesServiceImpl(api)
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    @Provides
    @ViewModelScoped
    fun provideGetMovieByIdUseCase(repository: MoviesRepository, moviesService: MoviesService) =
        GetMovieByIdUseCase(repository, moviesService)

    @Provides
    @ViewModelScoped
    fun provideSearchMovieUseCase(repository: MoviesRepository, moviesService: MoviesService) =
        SearchMoviesUseCase(repository, moviesService)
}