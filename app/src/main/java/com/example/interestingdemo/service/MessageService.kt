package com.example.interestingdemo.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import com.example.interestingdemo.GlobalSetting
import com.example.interestingdemo.R
import com.example.interestingdemo.SmsManagerActivity
import com.example.interestingdemo.broadacst.MessageControlBroadcast
import com.example.interestingdemo.util.NotificationUtil

class MessageService: Service() {
    private val mcBroadcast = MessageControlBroadcast

    override fun onCreate() {
        super.onCreate()
        Log.e("debugService","MessageService is created")

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pendingIntent = PendingIntent.getActivity(baseContext,1,Intent(baseContext, SmsManagerActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            NotificationUtil(applicationContext).getNotification("短信转发服务","已开启短信转发服务，收到短信时将自动转发到目标手机!", pendingIntent)
        } else {
            Notification.Builder(applicationContext)
                .setContentTitle("短信转发服务")
                .setContentText("已开启短信转发服务，收到短信时将自动转发到目标手机!")
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
        }
        startForeground(GlobalSetting.MESSAGE_SERVICE_FOREGROUND_ID, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("debugService","mcBroadcast is registered")
        registerReceiver(mcBroadcast, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        registerReceiver(mcBroadcast, IntentFilter("ACTION_SEND_MESSAGE"))
        registerReceiver(mcBroadcast, IntentFilter("ACTION_DELIVERY_MESSAGE"))

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e("debugService","mcBroadcast is unregistered")
        unregisterReceiver(mcBroadcast)
        stopForeground(true)
        super.onDestroy()
    }


}