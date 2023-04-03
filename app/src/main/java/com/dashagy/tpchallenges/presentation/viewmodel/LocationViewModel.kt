package com.dashagy.tpchallenges.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.entities.Location
import com.dashagy.domain.useCases.SaveLocationToServiceUseCase
import com.dashagy.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val saveLocationToServiceUseCase: SaveLocationToServiceUseCase
): ViewModel() {

    fun saveLocation(location: Location, showToast: (String) -> Unit) = viewModelScope.launch(
        Dispatchers.IO) {
        saveLocationToServiceUseCase(location){
            when (it) {
                is Result.Error -> showToast(it.exception.message.toString())
                is Result.Success -> showToast(it.data)
            }
        }
    }

}