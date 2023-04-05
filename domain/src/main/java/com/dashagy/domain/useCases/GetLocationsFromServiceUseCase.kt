package com.dashagy.domain.useCases

import com.dashagy.domain.entities.DeviceLocations
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result

class GetLocationsFromServiceUseCase(
    private val service: LocationService
) {

    operator fun invoke(deviceId: String, callback: (Result<DeviceLocations>) -> Unit) = service.getLocations(deviceId, callback)
}