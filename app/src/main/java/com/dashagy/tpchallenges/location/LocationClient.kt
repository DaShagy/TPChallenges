package com.dashagy.tpchallenges.location

import com.dashagy.domain.entities.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocation(interval: Long = 300_000L): Flow<Location>

    class LocationException(message: String): Exception()
}