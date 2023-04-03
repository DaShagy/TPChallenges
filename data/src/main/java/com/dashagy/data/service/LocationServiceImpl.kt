package com.dashagy.data.service

import com.dashagy.domain.entities.Location
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result
import com.google.firebase.firestore.FirebaseFirestore

class LocationServiceImpl(
    private val database: FirebaseFirestore
): LocationService {

    override fun saveLocationToService(
        location: Location,
        callback: (Result<String>) -> Unit
    ) {

        database.collection("location").document(location.id)
            .set(location)
            .addOnSuccessListener {
                callback(Result.Success("Location successfully added"))
            }
            .addOnFailureListener {
                callback(Result.Error(it))
            }

    }
}