package com.example.interestingdemo.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.interestingdemo.GlobalSetting
import com.example.interestingdemo.R

@RequiresApi(Build.VERSION_CODES.O)
class NotificationUtil(context: Context): ContextWrapper(context) {

    init {
        val channel = NotificationChannel(
            GlobalSetting.MESSAGE_SERVICE_CHANNEL_ID,
            GlobalSetting.MESSAGE_SERVICE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    fun getNotification(title: String, body: String, pi: PendingIntent? = null): Notification.Builder{
        return Notification.Builder(applicationContext,GlobalSetting.MESSAGE_SERVICE_CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.mipmap.ic_launcher)
            setAutoCancel(false)
            if (pi != null){
                setContentIntent(pi)
            }
        }
    }
}