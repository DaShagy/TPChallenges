package com.dashagy.data.service

import android.net.Uri
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ImageServiceImpl @Inject constructor (
    private val storage: FirebaseStorage
): ImageService {
    override fun uploadImage(
        imageUri: String,
        fileName: String,
        callback: (Result<String>) -> Unit
    ) {

        val uri: Uri = Uri.parse(imageUri)
        val childRef = storage.reference.child("images").child(fileName)

        childRef
            .putFile(uri)
            .addOnFailureListener {
                callback(Result.Error(it))
            }.addOnSuccessListener {
                callback(Result.Success("UPLOAD IMAGE $imageUri into ${childRef.path}"))
            }
    }
}