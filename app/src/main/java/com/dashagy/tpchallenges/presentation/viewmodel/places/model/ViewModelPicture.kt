package com.dashagy.tpchallenges.presentation.viewmodel.places.model

import android.net.Uri

data class ViewModelPicture(
    val localUri: Uri,
    val storagePath: String
)