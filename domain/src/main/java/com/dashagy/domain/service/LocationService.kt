package com.dashagy.domain.service

import com.dashagy.domain.entities.DeviceLocations
import com.dashagy.domain.entities.Location
import com.dashagy.domain.utils.Result

interface LocationService {
    fun saveLocationToService(
        deviceId: String,
        location: Location,
        callback: (Result<String>) -> Unit
    )

    fun getLocations(deviceId: String, callback: (Result<DeviceLocations>) -> Unit)
}