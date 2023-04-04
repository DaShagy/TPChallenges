package com.dashagy.tpchallenges.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dashagy.domain.entities.Location
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.location.LocationClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationAndroidService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    @Inject lateinit var locationClient: LocationClient

    private val binder = LocationAndroidServiceBinder()
    private var callback: Callback? = null

    private lateinit var stopServicePendingIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()

        stopServicePendingIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, LocationAndroidService::class.java).apply { action = ACTION_STOP },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT } else { FLAG_UPDATE_CURRENT }
        )
    }

    fun registerCallback(callback: Callback){
        this.callback = callback
    }

    fun unregisterCallback() {
        this.callback = null
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        val notification = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle("Tracking location...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .addAction(R.drawable.baseline_stop_24,"Stop", stopServicePendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        job = locationClient
            .getLocation(10_000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                callback?.onCallback(this, location, true)
                val updatedNotification = notification.setContentText(
                    "Lat: ${location.latitude}, Long: ${location.longitude}"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop(){
        stopForeground(true)
        job?.cancel()
        callback?.onCallback(this,null,false)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    inner class LocationAndroidServiceBinder: Binder(){
        fun getService(): LocationAndroidService = this@LocationAndroidService
    }

    interface Callback {
        fun onCallback(service: LocationAndroidService, location: Location?, isServiceRunning: Boolean)
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}