package com.marcorighini.notifydanger.misc.services

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.marcorighini.notifydanger.R


class NotificationController(private val application: Application, private val notificationManager: NotificationManager) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    fun monitoringNotification(): Notification {
        val builder = NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_DEFAULT_ID).apply {
            setContentTitle(application.getString(R.string.monitoring_title))
            setContentText(application.getString(R.string.monitoring_message))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setSmallIcon(R.mipmap.ic_launcher)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
        }

        return builder.build()
    }

    fun showDangerNotification() {
        val builder = NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_HIGH_ID).apply {
            setContentTitle(application.getString(R.string.danger_title))
            setContentText(application.getString(R.string.danger_message))
            setSmallIcon(R.mipmap.ic_launcher)
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(Notification.DEFAULT_ALL)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
        }
        notificationManager.notify(DANGER_NOTIFICATION_ID, builder.build())
    }

    fun removeDangerNotification() {
        notificationManager.cancel(DANGER_NOTIFICATION_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val service = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL_DEFAULT_ID, "NotifyDanger Activities", NotificationManager.IMPORTANCE_DEFAULT))
        service.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL_HIGH_ID, "NotifyDanger Dangers", NotificationManager.IMPORTANCE_HIGH))
    }

    companion object {
        const val NOTIFICATION_CHANNEL_DEFAULT_ID = "location_notification_channel_default"
        const val NOTIFICATION_CHANNEL_HIGH_ID = "location_notification_channel_max"
        const val DANGER_NOTIFICATION_ID = 12345
    }

}