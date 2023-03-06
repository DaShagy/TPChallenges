package com.dashagy.tpchallenges.data.service.api

import com.dashagy.tpchallenges.data.service.responseSchemas.MovieListResponseSchema
import com.dashagy.tpchallenges.data.service.responseSchemas.MovieResponseSchema
import com.dashagy.tpchallenges.utils.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDatabaseAPI {

     @GET("movie/{movieId}?api_key=${Constants.API_KEY}")
     fun getMovieById(@Path("movieId") movieId: Int): Call<MovieResponseSchema>

     @GET("search/movie?api_key=${Constants.API_KEY}")
     fun searchMovieByName(@Query("query") query: String): Call<MovieListResponseSchema>
}
