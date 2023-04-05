package com.dashagy.tpchallenges.utils

import android.content.Context
import android.provider.Settings

object DeviceUtils {

    fun getDeviceId(context: Context) = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}