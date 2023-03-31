package com.dashagy.tpchallenges.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    fun getTimestamp(): Long = System.currentTimeMillis()

    fun getFormattedTimestamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
}