package com.dashagy.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.dashagy.data.service.api.TheMovieDatabaseAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val API = "api_key"

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
    fun provideApiService(retrofit: Retrofit): TheMovieDatabaseAPI = retrofit.create(
        TheMovieDatabaseAPI::class.java)
}