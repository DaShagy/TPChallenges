package com.dashagy.tpchallenges.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.entities.Location
import com.dashagy.domain.useCases.SaveLocationToServiceUseCase
import com.dashagy.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val saveLocationToServiceUseCase: SaveLocationToServiceUseCase
): ViewModel() {

    private var location: Location? = null

    private var _isLocationAndroidServiceRunning = MutableLiveData(false)
    val isLocationAndroidServiceRunning: LiveData<Boolean>
        get() = _isLocationAndroidServiceRunning

    fun saveLocation(showToast: (String) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        location?.let {
            saveLocationToServiceUseCase(it) { result ->
                when (result) {
                    is Result.Error -> showToast(result.exception.message.toString())
                    is Result.Success -> showToast(result.data)
                }
            }
        } ?: withContext(Dispatchers.Main) { showToast("Couldn't save current location") }
    }

    fun updateIsLocationRunning(isRunning: Boolean){
        _isLocationAndroidServiceRunning.value = isRunning
    }

    fun updateLocation(location: Location?) {
        this.location = location
    }

}