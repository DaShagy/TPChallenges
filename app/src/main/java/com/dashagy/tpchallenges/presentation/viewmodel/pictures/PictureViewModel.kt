package com.dashagy.tpchallenges.presentation.viewmodel.pictures

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.entities.Picture
import com.dashagy.domain.useCases.UploadImageToServiceUseCase
import com.dashagy.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    private val uploadImageToServiceUseCase: UploadImageToServiceUseCase
): ViewModel() {

    private var _picturesState: MutableLiveData<PicturesState> = MutableLiveData()
    val picturesState: LiveData<PicturesState>
        get() = _picturesState

    private var _isPictureListEmpty: MutableLiveData<Boolean> = MutableLiveData(true)
    val isPictureListEmpty
        get() = _isPictureListEmpty

    private val pictureList: MutableList<Picture> = mutableListOf()

    fun uploadImages() = viewModelScope.launch(Dispatchers.IO) {
        _picturesState.postValue(PicturesState.Loading)
        pictureList.forEach { picture ->
            uploadImageToServiceUseCase(picture, ::updateStateOnUploadPicture)
        }
    }

    private fun updateStateOnUploadPicture(result: Result<String>) {
        when (result) {
            is Result.Success -> _picturesState.postValue(
                PicturesState.UploadSuccess(
                    result.data
                )
            )
            is Result.Error -> _picturesState.postValue(
                PicturesState.UploadError(
                    result.exception
                )
            )
        }
    }

    fun updateStateOnAddPicture(pic: Picture?, exception: Exception? = null) {

        _picturesState.value = PicturesState.Loading

        _picturesState.value =
            pic?.let { picture ->
                PicturesState.AddPictureSuccess(picture)
            } ?: exception?.let { e ->
                PicturesState.AddPictureError(e)
            } ?: PicturesState.AddPictureError(Exception("Couldn't add picture"))

    }

    fun addPicture(uri: Uri?, path: String) {
        _picturesState.value = PicturesState.Loading
        uri?.let {
            val picture = Picture(it.toString(), path)
            if (!pictureList.any { addedPicture -> addedPicture.localUri == it.toString() }) {
                pictureList.add(picture)
                updateStateOnAddPicture(picture)
            } else {
                updateStateOnAddPicture(null, Exception("Picture already loaded"))
            }
        }
        _isPictureListEmpty.value = pictureList.isEmpty()
    }

    fun getLastAddedPicture(): Picture? {
        return if (pictureList.isNotEmpty()) pictureList.last() else null
    }


    sealed class PicturesState {
        class UploadSuccess(val downloadUrl: String): PicturesState()
        class UploadError(val exception: Exception): PicturesState()
        class AddPictureSuccess(val picture: Picture): PicturesState()
        class AddPictureError(val exception: Exception): PicturesState()
        object Loading: PicturesState()
    }
}