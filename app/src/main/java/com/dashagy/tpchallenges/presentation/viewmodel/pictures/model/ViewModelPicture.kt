package com.dashagy.tpchallenges.presentation.viewmodel.pictures.model

import android.net.Uri

data class ViewModelPicture(
    val localUri: Uri,
    val storagePath: String
)