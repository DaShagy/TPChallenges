package com.dashagy.tpchallenges.presentation.viewmodel.pictures

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.useCases.UploadImageToServiceUseCase
import com.dashagy.domain.utils.Result
import com.dashagy.tpchallenges.presentation.viewmodel.pictures.model.ViewModelPicture
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

    private val pictureList: MutableList<ViewModelPicture> = mutableListOf()

    fun uploadImages() = viewModelScope.launch(Dispatchers.IO) {
        _picturesState.postValue(PicturesState.Loading)
        pictureList.forEach { picture ->
            uploadImageToServiceUseCase(
                picture.localUri.toString(),
                picture.storagePath,
                ::updateStateOnUploadPicture
            )
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

    fun updateStateOnAddPicture(exception: Exception? = null) {
        _picturesState.value = PicturesState.Loading
        exception?.let {
            _picturesState.value = PicturesState.AddPictureError(it)
            return
        }

        if (pictureList.isNotEmpty()){
            _picturesState.value = PicturesState.AddPictureSuccess(pictureList)
            return
        }

        _picturesState.value = PicturesState.AddPictureError(
            Exception("Couldn't add picture")
        )

    }

    fun addPicture(uri: Uri?, path: String) {
        _picturesState.value = PicturesState.Loading
        uri?.let {
            val picture = ViewModelPicture(it, path)
            if (!pictureList.any { addedPicture -> addedPicture.localUri == it }) {
                pictureList.add(picture)
                updateStateOnAddPicture()
            } else {
                updateStateOnAddPicture(Exception("Picture already loaded"))
            }
        }
        _isPictureListEmpty.value = pictureList.isEmpty()
    }

    fun getLastAddedPicture(): ViewModelPicture? {
        return if (pictureList.isNotEmpty()) pictureList.last() else null
    }


    sealed class PicturesState {
        class UploadSuccess(val downloadUrl: String): PicturesState()
        class UploadError(val exception: Exception): PicturesState()
        class AddPictureSuccess(val pictures: List<ViewModelPicture>): PicturesState()
        class AddPictureError(val exception: Exception): PicturesState()
        object Loading: PicturesState()
    }
}