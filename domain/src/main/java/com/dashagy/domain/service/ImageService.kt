package com.dashagy.domain.service

import com.dashagy.domain.entities.Picture
import com.dashagy.domain.utils.Result

interface ImageService {
    fun uploadImage(
        picture: Picture,
        callback: (Result<Picture>) -> Unit
    )
}
