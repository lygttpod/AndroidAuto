package com.lygttpod.android.auto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class ForegroundService : Service() {
    companion object {
        private const val TAG = "ForegroundService"

        private const val CHANNEL_ID = "ForegroundService"
        private const val CHANNEL_NAME = "前台服务"

        const val ACTION_TOAST = "ACTION_TOAST"
        const val KEY_TOAST_TEXT = "KEY_TOAST_TEXT"
    }

    private  val pendingIntent by lazy {
        val intent =  Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private val notification by lazy {
        Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("前台服务")
            .setContentText("重要，勿删！")
            .setSmallIcon(R.mipmap.icon_logo)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.icon_logo))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private var lastToastTime = 0L
    private var lastToastText = ""

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val text = intent?.getStringExtra(KEY_TOAST_TEXT)
                    ?.takeIf { it.isNotBlank() } ?: return
                Log.d(TAG, "收到Toast: $text")

                if (text == lastToastText && System.currentTimeMillis() - lastToastTime < 5000) {
                    Log.d(TAG, "文本重复，忽略")
                    return
                }

                Toast.makeText(App.instance(), text, Toast.LENGTH_SHORT).show()
                lastToastTime = System.currentTimeMillis()
                lastToastText = text
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)

        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        )

        startForeground(1, notification)

        registerReceiver(receiver, IntentFilter(ACTION_TOAST), RECEIVER_NOT_EXPORTED)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}