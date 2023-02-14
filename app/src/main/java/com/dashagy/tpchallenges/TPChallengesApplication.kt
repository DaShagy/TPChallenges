package com.dashagy.tpchallenges

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.dashagy.tpchallenges.data.database.TPChallengesDatabase
import com.dashagy.tpchallenges.data.service.api.TheMovieDatabaseAPI
import com.dashagy.tpchallenges.domain.useCases.GetMovieByIdUseCase
import com.dashagy.tpchallenges.domain.useCases.SearchMoviesUseCase
import com.dashagy.tpchallenges.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TPChallengesApplication: Application() {

    //Room
    private val database by lazy {
        TPChallengesDatabase.getInstance(this)
    }

    //Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Api service
    private val theMovieDatabaseAPI: TheMovieDatabaseAPI by lazy {
        retrofit.create(TheMovieDatabaseAPI::class.java)
    }

    val searchMoviesUseCase get() = SearchMoviesUseCase(theMovieDatabaseAPI, database)
    val getMovieByIdUseCase get() = GetMovieByIdUseCase(theMovieDatabaseAPI, database)

    val isDeviceOnline: Boolean get() {
        val connectivityManager = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)){
                return if (this == null) false
                else { hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
            }
        }
        return false
    }

    override fun onCreate() {
        super.onCreate()
    }
}