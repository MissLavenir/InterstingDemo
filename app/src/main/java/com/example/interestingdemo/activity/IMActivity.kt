package com.example.interestingdemo.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interestingdemo.R
import com.example.interestingdemo.adapter.ImTextAdapter
import com.example.interestingdemo.base.BaseActivity
import com.example.interestingdemo.extensions.initBackground
import com.example.interestingdemo.extensions.loadingDelay
import com.example.interestingdemo.extensions.visible
import com.example.interestingdemo.model.socket.BaseSocketModel
import com.example.interestingdemo.service.ScreenCaptureService
import com.example.interestingdemo.util.SocketManagerUtil
import kotlinx.android.synthetic.main.activity_im.*
import pub.devrel.easypermissions.EasyPermissions

class IMActivity : BaseActivity() {
    private val adapter = ImTextAdapter()
    private lateinit var sfHolder: SurfaceHolder
    //是否开启捕获
    private var isStartCapture = false
    private val mediaProjectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }

    private var serverIntent : Intent? = null

    private val screenCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            serverIntent?.putExtra("resultCode", result.resultCode)
            serverIntent?.putExtra("data", result.data)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                val permissions = arrayOf(Manifest.permission.FOREGROUND_SERVICE)
                if (EasyPermissions.hasPermissions(this, *permissions)){
                    startForegroundService(serverIntent)
                } else {
                    EasyPermissions.requestPermissions(this,"需要开启前台服务",
                        APPLY_PERMISSION, *permissions)
                }
            } else {
                startService(serverIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_im)
        val itemType = intent.getStringExtra("imType") ?: ""
        if (itemType == "客户端"){
            serverIntent = Intent(applicationContext, ScreenCaptureService::class.java)
            ivShowSurface.visible(false)
            tvShareScreen.visible()
        } else {
            ivShowSurface.visible()
            tvShareScreen.visible(false)
        }

        initTitle(itemType)

        etInput.setOnClickListener {
            loadingDelay {
                if (adapter.itemCount > 0){
                    rvData.smoothScrollToPosition(adapter.itemCount -1)
                }
            }
        }

        ivClose.setOnClickListener {
            llBytes.visible(false)
        }
        ivShowSurface.setOnClickListener {
            llBytes.visible(!llBytes.isVisible)
        }
        tvShareScreen.setOnClickListener {
            isStartCapture = !isStartCapture
            if (isStartCapture){
                //分享屏幕当前内容
                tvShareScreen.text = "停止共享"
                tvShareScreen.initBackground(R.drawable.bg_round_rectangle_red_500)
                screenCaptureLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
            } else {
                //停止分享屏幕当前内容
                tvShareScreen.text = "屏幕共享"
                tvShareScreen.initBackground(R.drawable.bg_round_rectangle_blue_500)
                stopService(serverIntent)
            }
        }

        //渲染SurfaceView
        sfHolder = svBytes.holder
        sfHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.e("debug-IMActivity","surfaceCreated")
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.e("debug-IMActivity","surfaceChanged")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.e("debug-IMActivity","surfaceDestroyed")
            }
        })

        //发送消息
        tvSend.setOnClickListener {
            if (etInput.text.toString().isNotEmpty()){
                val msg = etInput.text.toString()
                if (itemType == "客户端"){
                    SocketManagerUtil.sendMessageToServer(msg)
                } else {
                    SocketManagerUtil.sendMessageToAllClients(msg)
                }

                adapter.addData(BaseSocketModel(3, msg, "本机"))
                rvData.smoothScrollToPosition(adapter.itemCount -1)
            }
        }

        rvData.layoutManager = LinearLayoutManager(this)
        rvData.adapter = adapter

        //接收消息
        lifecycleScope.launchWhenStarted {
            SocketManagerUtil.dataFlow.collect{
                if (it.dataType == 4){
                    Log.e("debug","加载字节数据并转换为bitmap")
                    drawToSurface(it.data as ByteArray)
                } else {
                    adapter.addData(it)
                    rvData.smoothScrollToPosition(adapter.itemCount -1)
                }
            }
        }

    }

    /**
     * 将byte数组绘制到SurfaceView
     */
    private fun drawToSurface(byteArray: ByteArray) {
        val canvas = sfHolder.lockCanvas() ?: return
        try {
            // 将 byte 数组转换为 Bitmap
            val options = BitmapFactory.Options()
            options.inMutable = true
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
            // 调整Bitmap大小
//            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            val dstRect = Rect(0, 0, svBytes.width, svBytes.height)
            canvas.drawBitmap(bitmap, srcRect, dstRect, null)
        } catch (e: Exception){
            e.printStackTrace()
        } finally {
            // 释放 Canvas
            sfHolder.unlockCanvasAndPost(canvas)
        }
    }

    companion object{
        const val APPLY_PERMISSION = 1013
    }

}