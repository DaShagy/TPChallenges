package com.dashagy.domain.useCases

import com.dashagy.domain.entities.DeviceLocations
import com.dashagy.domain.entities.Location
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result

class SaveLocationToServiceUseCase(
    private val service: LocationService
) {

    operator fun invoke(
        deviceId: String,
        location: Location,
        callback: (Result<String>) -> Unit
    ) = service.saveLocationToService(deviceId, location, callback)
}