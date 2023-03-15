package com.dashagy.data.service.api

import com.dashagy.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.data.service.responseSchemas.MovieResponseSchema
import com.dashagy.data.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDatabaseAPI {

    @GET("movie/{movieId}")
    fun getMovieById(@Path("movieId") movieId: Int): Call<MovieResponseSchema>

    @GET("search/movie")
    fun searchMovieByName(@Query("query") query: String): Call<MovieListResponseSchema>

    @GET("movie/popular")
    fun getPopularMovies(): Call<MovieListResponseSchema>
}
