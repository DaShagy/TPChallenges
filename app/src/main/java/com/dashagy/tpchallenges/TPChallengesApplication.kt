package com.dashagy.tpchallenges

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TPChallengesApplication: Application() {
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
}