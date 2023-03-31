package com.dashagy.data.service

import android.net.Uri
import com.dashagy.domain.entities.Picture
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ImageServiceImpl @Inject constructor (
    private val storage: FirebaseStorage
): ImageService {
    override fun uploadImage(
        picture: Picture,
        callback: (Result<Picture>) -> Unit
    ) {

        val uri: Uri = Uri.parse(picture.localUri)
        val childRef = storage.reference.child("images").child(picture.storageFilepath)

        childRef
            .putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    callback(Result.Error(task.exception ?: Exception("Unknown Error")))
                }
                childRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Result.Success(picture.apply { downloadUri = task.result.toString() }))
                } else {
                    callback(Result.Error(task.exception ?: Exception("Unknown Error")))
                }
            }
    }
}