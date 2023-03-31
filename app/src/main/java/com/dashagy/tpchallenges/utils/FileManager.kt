package com.dashagy.tpchallenges.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class FileManager(private val context: Context) {

    fun createImageLocalUri(): Pair<Uri?, String> {
        val timestamp = TimeUtil.getFormattedTimestamp()
        var uri: Uri?

        try {
            val file = File.createTempFile(
                "JPEG_${timestamp}_",
                ".jpg",
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileProvider",
                file
            )

        } catch (e: IOException) {
            uri = null
        }

        return Pair(uri, timestamp)
    }

}