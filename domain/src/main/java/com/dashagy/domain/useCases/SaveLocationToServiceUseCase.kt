package com.dashagy.domain.useCases

import com.dashagy.domain.entities.DeviceLocations
import com.dashagy.domain.entities.Location
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result
import javax.inject.Inject

class SaveLocationToServiceUseCase @Inject constructor(
    private val service: LocationService
) {

    operator fun invoke(
        deviceId: String,
        location: Location,
        callback: (Result<String>) -> Unit
    ) = service.saveLocationToService(deviceId, location, callback)
}