package com.dashagy.data.service

import android.net.Uri
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class ImageServiceImpl @Inject constructor (private val storage: FirebaseStorage): ImageService {
    override fun uploadImage(
        filepath: String,
        callback: (Result<String>) -> Unit
    ) {
        //TODO FIX THIS

        val file = Uri.fromFile(File(filepath))
        val childRef = storage.reference.child("images/${file.lastPathSegment}")

        childRef
            .putFile(file)
            .addOnFailureListener {
                callback(Result.Error(it))
            }.addOnSuccessListener { taskSnapshot ->
                callback(Result.Success(taskSnapshot.toString()))
            }
    }
}