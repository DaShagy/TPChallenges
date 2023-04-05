package com.dashagy.domain.service

import com.dashagy.domain.entities.Location
import com.dashagy.domain.utils.Result

interface LocationService {
    fun saveLocationToService(
        deviceId: String,
        location: Location,
        callback: (Result<String>) -> Unit
    )
}