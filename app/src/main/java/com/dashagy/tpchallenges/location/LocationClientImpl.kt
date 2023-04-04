package com.dashagy.tpchallenges.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.Settings
import com.dashagy.domain.entities.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationClientImpl @Inject constructor(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    @SuppressLint("MissingPermission", "VisibleForTests")
    override fun getLocation(interval: Long): Flow<Location> {
        return callbackFlow {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val handlerThread = HandlerThread("LocationUpdates")
            handlerThread.start()
            val handler = Handler(handlerThread.looper)

            val request = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                this.interval = interval
                fastestInterval = interval
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(
                            Location(
                                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID),
                                location.latitude,
                                location.longitude
                            )
                        ) }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                handler.looper
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
                handlerThread.quit()
            }
        }.flowOn(Dispatchers.IO)
    }
}