package com.dashagy.tpchallenges

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TheMovieDatabaseAPI {

     @GET("movie/{movieId}?api_key=${Constants.API_KEY}")
     suspend fun getMovieById(@Path("movieId") movieId: Int): Response<Movie>
}
