package com.example.interestingdemo.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.example.interestingdemo.GlobalSetting

object SendMessageUtil {

    fun sendMessage(context: Context, phone: String, message: String){
        val sManager = SmsManager.getDefault()
        sManager.sendTextMessage(
            phone,
            null,
            message,
            PendingIntent.getBroadcast(
                context,
                1,
                Intent(GlobalSetting.ACTION_SEND_MESSAGE).putExtra("sendExtra","send"),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ),
            PendingIntent.getBroadcast(
                context,
                2,
                Intent(GlobalSetting.ACTION_DELIVERY_MESSAGE).putExtra("sendExtra","deliver"),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

}