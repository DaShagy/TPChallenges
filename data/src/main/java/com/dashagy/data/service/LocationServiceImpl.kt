package com.dashagy.data.service

import com.dashagy.domain.entities.DeviceLocations
import com.dashagy.domain.entities.Location
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.utils.Result
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LocationServiceImpl(
    private val database: FirebaseFirestore
): LocationService {

    override fun saveLocationToService(
        deviceId: String,
        location: Location,
        callback: (Result<String>) -> Unit
    ) {

        val locationMap = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to location.timestamp
        )

        val documentReference = database.collection("location").document(deviceId)

        val setFieldsTask = documentReference.set(
            hashMapOf("id" to deviceId),
            SetOptions.merge()
        )

        val updateLocationsTask = documentReference.update("locations", FieldValue.arrayUnion(locationMap))

        Tasks.whenAll(setFieldsTask, updateLocationsTask)
            .addOnSuccessListener {
                callback(Result.Success("Location successfully added"))
            }
            .addOnFailureListener {
                callback(Result.Error(it))
            }
    }
}