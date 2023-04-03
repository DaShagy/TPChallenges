package com.dashagy.domain.service

import com.dashagy.domain.entities.Location
import com.dashagy.domain.utils.Result

interface LocationService {
    fun saveLocationToService(location: Location, callback: (Result<String>) -> Unit)
}