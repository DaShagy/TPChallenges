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

    private var _pictureList: MutableLiveData<List<Picture>> = MutableLiveData(listOf())
    val pictureList: LiveData<List<Picture>>
        get() = _pictureList

    private var cameraPicture: Picture? = null

    fun uploadImages() = viewModelScope.launch(Dispatchers.IO) {
        _picturesState.postValue(PicturesState.Loading)
        pictureList.value?.forEach { picture ->
            uploadImageToServiceUseCase(picture, ::updateStateOnUploadPicture)
        }
    }

    private fun updateStateOnUploadPicture(result: Result<Picture>) {
        when (result) {
            is Result.Success -> {
                _picturesState.postValue(
                    PicturesState.Success
                )
            }
            is Result.Error -> _picturesState.postValue(
                PicturesState.Error(
                    result.exception
                )
            )
        }
    }

    fun updateStateOnAddPicture(pic: Picture?, exception: Exception? = null) {

        _picturesState.value = PicturesState.Loading

        _picturesState.value =
            pic?.let {
                PicturesState.Success
            } ?: exception?.let { e ->
                PicturesState.Error(e)
            } ?: PicturesState.Error(Exception("Couldn't add picture"))

    }

    fun addPicture(uri: Uri?, path: String) {
        _picturesState.value = PicturesState.Loading
        uri?.let {
            val picture = Picture(it.toString(), path)

            pictureList.value?.let { pictures ->
                if (!pictures.any { addedPicture -> addedPicture.localUri == it.toString() }) {
                    val tempList = pictures.toMutableList()
                    tempList.add(picture)
                    _pictureList.value = tempList
                    updateStateOnAddPicture(picture)
                } else {
                    updateStateOnAddPicture(null, Exception("Picture already loaded"))
                }
            }
        }
    }

    fun updateCameraPicture(uri: Uri, path:String) {
        cameraPicture = Picture(uri.toString(), path)
    }

    fun addCameraPicture() {
        cameraPicture?.let { picture ->
            addPicture(Uri.parse(picture.localUri), picture.storageFilepath)
        }
        cameraPicture = null
    }

    sealed class PicturesState {
        object Success : PicturesState()
        class Error(val exception: Exception): PicturesState()
        object Loading: PicturesState()
    }
}