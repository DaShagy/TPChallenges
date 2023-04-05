package com.dashagy.domain.entities

data class DeviceLocations(
    val deviceId: String = "",
    val locations: List<Location> = listOf()
)