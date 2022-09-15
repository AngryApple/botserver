package com.mikers.botserver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mikers.botserver.ServiceState.ACTION_START

object ServiceState {
    const val ACTION_START = "ACTION_START"
    const val ACTION_STOP = "ACTION_STOP"
}

interface IBroadcastSender {
    fun sendBroadcastFunc(intent: Intent)
}

class BackgroundService : Service(), IBroadcastSender {

    private val CHANNEL_ID = "opencoffeebot606"
    private val CHANNEL_NAME = "opencoffee"
    private val CHANNEL_DESCRIPTION = "Test"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START -> {
                addNotification()
                startService()
            }
            ServiceState.ACTION_STOP -> stopForegroundService()
        }
        return START_STICKY
    }

    override fun sendBroadcastFunc(intent: Intent) {
        sendBroadcast(intent)
    }

    private fun addNotification() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = CHANNEL_DESCRIPTION

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Bot working!")
            .setContentText("Bot working, all message reading!")
        startForeground(1001, notification.build())
    }

    private fun startService() {
        BotServiceJava.getInstance().start(this)
    }

    private fun stopForegroundService() {
        BotServiceJava.getInstance().stop()
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
    }

}