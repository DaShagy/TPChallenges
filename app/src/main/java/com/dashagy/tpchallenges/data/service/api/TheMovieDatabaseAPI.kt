package com.dashagy.tpchallenges.data.service.api

import com.dashagy.tpchallenges.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.tpchallenges.data.service.responseSchemas.MovieResponseSchema
import com.dashagy.tpchallenges.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDatabaseAPI {

     @GET("movie/{movieId}?api_key=${Constants.API_KEY}")
     suspend fun getMovieById(@Path("movieId") movieId: Int): Response<MovieResponseSchema>

     @GET("search/movie?api_key=${Constants.API_KEY}")
     suspend fun searchMovieByName(@Query("query") query: String): Response<MovieListResponseSchema>
}
