package com.example.interestingdemo.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Display
import com.example.interestingdemo.GlobalSetting
import com.example.interestingdemo.R
import com.example.interestingdemo.activity.IMActivity
import com.example.interestingdemo.util.NotificationUtil
import com.example.interestingdemo.util.SocketManagerUtil
import java.io.ByteArrayOutputStream

class ScreenCaptureService: Service() {

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private var imageReader: ImageReader? = null

    override fun onCreate() {
        super.onCreate()
        Log.e("debugService","ScreenCaptureService is created")
        startForegroundService()
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("debugService","ScreenCaptureService is onStartCommand")
        intent?.let {
            val resultCode = intent.getIntExtra("resultCode", -1)
            val data = intent.getParcelableExtra<Intent>("data")

            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
            captureScreen()
        }
        return START_STICKY

    }

    private fun startForegroundService(){
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pendingIntent = PendingIntent.getActivity(baseContext,1,Intent(baseContext, IMActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            NotificationUtil(applicationContext).getNotification("屏幕捕获","已开启屏幕捕获服务!", pendingIntent)
        } else {
            Notification.Builder(applicationContext)
                .setContentTitle("屏幕捕获服务")
                .setContentText("正在进行屏幕捕获!")
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(GlobalSetting.SOCKET_SERVICE_FOREGROUND_ID, notification.build(), FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(GlobalSetting.SOCKET_SERVICE_FOREGROUND_ID, notification.build())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e("debugService","ScreenCaptureService is onDestroy")
        imageReader?.close()
        stopForeground(true)
        super.onDestroy()
    }

    /**
     * 获取当前屏幕帧
     */
    @SuppressLint("WrongConstant")
    private fun captureScreen() {
        val metrics = application.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        if (imageReader == null){
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        }

        imageReader?.setOnImageAvailableListener({ reader ->
            if (SocketManagerUtil.isClientStart()){
                val image = reader.acquireLatestImage()
                if (image != null) {
                    processImage(image)
                }
            } else {
                onDestroy()
            }
        }, null)

        mediaProjection.createVirtualDisplay(
            "screen_capture",
            width,
            height,
            metrics.densityDpi,
            Display.FLAG_SECURE,
            imageReader!!.surface,
            null,
            null
        )
    }

    private fun processImage(image: Image) {
        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width

            val bitmap = Bitmap.createBitmap(image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            SocketManagerUtil.sendMessageToServer(byteArray)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 释放图像资源
            image.close()
//            imageReader?.close()
        }
    }
}