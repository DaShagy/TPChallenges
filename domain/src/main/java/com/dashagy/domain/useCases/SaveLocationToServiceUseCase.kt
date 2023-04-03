package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Location
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result

class SaveLocationToServiceUseCase(
    private val service: LocationService
) {

    operator fun invoke(
        location: Location,
        callback: (Result<String>) -> Unit
    ) = service.saveLocationToService(location, callback)
}