package com.dashagy.tpchallenges.presentation.utils

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.utils.Constants

fun ImageView.loadImage(ctx: Context, url: String?) {
    url?.let {
        Glide.with(ctx).load("${Constants.API_IMAGE_BASE_URL}w500/$it")
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(this)
    }
}

fun SearchView.clean() {
    this.setQuery("", false)
    this.clearFocus()
}