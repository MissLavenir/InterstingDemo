package com.example.interestingdemo.activity

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.interestingdemo.GlobalSetting
import com.example.interestingdemo.R
import com.example.interestingdemo.broadacst.MessageControlBroadcast
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.service.MessageService
import com.example.interestingdemo.util.DialogUtil
import com.example.interestingdemo.util.SendMessageUtil
import kotlinx.android.synthetic.main.fragment_sms_manager.*
import pub.devrel.easypermissions.EasyPermissions

class SmsManagerActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    //开启后后台会发送一次短信转发
    private var isStartService = false
    //开启后，会在Activity中注册广播，会进行短信转发，若服务也开启，则会出现两次短信转发,谨慎开启测试。
    private var isTest = false

    private val broadcastReceiver by lazy { MessageControlBroadcast }
    private val permissions = arrayOf(Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sms_manager)

        if (isTest){
            registerReceiver(broadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            registerReceiver(broadcastReceiver, IntentFilter("ACTION_SEND_MESSAGE"))
            registerReceiver(broadcastReceiver, IntentFilter("ACTION_DELIVERY_MESSAGE"))
        }

        if (GlobalSetting.phoneNumber.isNotEmpty()){
            inputPhone.setText(GlobalSetting.phoneNumber)
            receivePhone.setText(GlobalSetting.phoneNumber)
        }
        savePhone.setOnClickListener {
            if (receivePhone.text.toString().length != 11){
                return@setOnClickListener toast("手机号码错误,保存失败")
            }
            GlobalSetting.phoneNumber = receivePhone.text.toString()
            toast("手机号码保存成功")
        }
        val intentService = Intent(applicationContext, MessageService::class.java)
        startService.setOnClickListener {
            if (isTest){
                DialogUtil.showConfirmDialog(this, "转发测试状态启动中，服务开启后将会转发两次短信，是否关闭测试？", "警告"){ boolean ->
                    if (boolean){
                        isTest = false
                        sendMessageLayout.visibility = View.GONE
                        unregisterReceiver(broadcastReceiver)
                    }
                }
                return@setOnClickListener
            }
            if (isStartService){
                startService.text = "开启前台服务"
                stopService(intentService)
                isStartService = false
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    val permissions = arrayOf(Manifest.permission.FOREGROUND_SERVICE)
                    if (EasyPermissions.hasPermissions(this, *permissions)){
                        startForegroundService(intentService)
                        startService.text = "关闭前台服务"
                        isStartService = true
                    } else {
                        EasyPermissions.requestPermissions(this,"需要开启前台服务",
                            APPLY_SERVICE_PERMISSION, *permissions)
                    }
                } else {
                    startService(Intent(applicationContext, MessageService::class.java))
                    startService.text = "关闭前台服务"
                    isStartService = true
                }
            }
        }


        if (isTest){
            sendMessageLayout.visibility = View.VISIBLE
            sendSms.setOnClickListener {
                if (inputPhone.text.toString().length != 11){
                    return@setOnClickListener toast("手机号码错误")
                }
                GlobalSetting.phoneNumber = inputPhone.text.toString()

                if (inputMessage.text.toString().isEmpty()){
                    return@setOnClickListener toast("短信内容不能为空")
                }
            }
        }

        if (EasyPermissions.hasPermissions(this, *permissions)){
            applyPermission.text = "已有短信相关权限"
            applyPermission.setTextColor(ResourcesCompat.getColor(resources, R.color.green_a700, theme))
            applyPermission.setBackgroundResource(R.drawable.bg_round_rectangle_stroke_green)
        } else {
            applyPermission.setOnClickListener {
                EasyPermissions.requestPermissions(this,"需要短信权限才可进行发送短信",
                    APPLY_SMS_PERMISSION, *permissions)
            }
        }

    }

    private fun sendPhoneMessage(phone:String, text: String){
        SendMessageUtil.sendMessage(this, phone, text)
    }

    companion object{
        const val APPLY_SMS_PERMISSION = 1011
        const val APPLY_SERVICE_PERMISSION = 1012
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        toast("已成功授权")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        DialogUtil.showConfirmDialog(this,"申请权限被拒绝，是否进入设置手动开启权限"){
            if (it){
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")))
            }
        }
    }

    override fun onDestroy() {
        if (isTest){
            unregisterReceiver(broadcastReceiver)
        }
        super.onDestroy()
    }
}