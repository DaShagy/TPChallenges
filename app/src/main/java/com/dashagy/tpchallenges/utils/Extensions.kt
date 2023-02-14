package com.dashagy.tpchallenges.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dashagy.tpchallenges.R

fun ImageView.loadImage(ctx: Context, url: String?) {
    url?.let {
        Glide.with(ctx).load("${Constants.API_IMAGE_BASE_URL}$it")
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(this)
    }
}