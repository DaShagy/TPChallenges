package com.dashagy.tpchallenges.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import com.dashagy.domain.entities.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClientImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    @SuppressLint("MissingPermission", "VisibleForTests")
    override fun getLocation(interval: Long): Flow<Location> {
        return callbackFlow {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

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
                                Build.ID,
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
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}