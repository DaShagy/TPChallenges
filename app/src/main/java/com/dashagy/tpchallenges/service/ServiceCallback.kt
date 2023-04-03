package com.dashagy.tpchallenges.service

import com.dashagy.domain.entities.Location

interface ServiceCallback {
    fun updateIsServiceRunning(isServiceRunning: Boolean)
    fun setLocationFromService(location: Location?)
}