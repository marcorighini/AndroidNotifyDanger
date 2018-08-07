package com.marcorighini.notifydanger.misc.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.marcorighini.notifydanger.component
import javax.inject.Inject


@SuppressLint("MissingPermission")
class LocationService : Service() {
    @Inject
    lateinit var locationClient: FusedLocationProviderClient
    @Inject
    lateinit var notificationController: NotificationController

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            onNewLocation(locationResult?.lastLocation)
        }
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this) // todo
        startForeground(NOTIFICATION_ID, notificationController.monitoringNotification())

        locationClient.lastLocation?.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                onNewLocation(it.result)
            }
        }
        requestLocationUpdates()
    }

    private fun onNewLocation(location: Location?) {
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()
        request.interval = 5000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationClient.requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val PACKAGE = "com.marcorighini.notifydanger.misc.services.LocationService"
        const val ACTION_BROADCAST = "$PACKAGE.locationbroadcast"
        const val EXTRA_LOCATION = "$PACKAGE.locationextra"
        const val NOTIFICATION_ID = 13579
    }
}