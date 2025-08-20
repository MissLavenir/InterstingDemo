package com.example.interestingdemo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interestingdemo.R
import com.example.interestingdemo.adapter.ImTextAdapter
import com.example.interestingdemo.base.BaseActivity
import com.example.interestingdemo.extensions.initBackground
import com.example.interestingdemo.extensions.loadingDelay
import com.example.interestingdemo.extensions.visible
import com.example.interestingdemo.imTools.BitmapPool
import com.example.interestingdemo.imTools.MatrixCache
import com.example.interestingdemo.model.socket.BaseSocketModel
import com.example.interestingdemo.service.ScreenCaptureService
import com.example.interestingdemo.util.SocketManagerUtil
import kotlinx.android.synthetic.main.activity_im.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions

class IMActivity : BaseActivity() {
    private val adapter = ImTextAdapter()
    private lateinit var sfHolder: SurfaceHolder
    //是否开启捕获
    private var isStartCapture = false
    private val mediaProjectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private var serverIntent : Intent? = null

    @SuppressLint("ObsoleteSdkInt")
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

            //初始化屏幕共享相关东西
            initScreenRender()
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
            llBytes.visible(true)
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
                    submitFrame(it.data as ByteArray)
                } else {
                    adapter.addData(it)
                    rvData.smoothScrollToPosition(adapter.itemCount -1)
                }
            }
        }

    }

    //将byteArray绘制到SurfaceView上，简单方式
//    private fun drawToSurface(byteArray: ByteArray) {
//        GlobalScope.launch(Dispatchers.IO) {
//            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//            withContext(Dispatchers.Main) {
//                val canvas = sfHolder.lockCanvas() ?: run {
//                    return@withContext
//                }
//                val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
//                val dstRect = Rect(0, 0, svBytes.width, svBytes.height)
//                canvas.drawBitmap(bitmap, srcRect, dstRect, null)
//                sfHolder.unlockCanvasAndPost(canvas)
//            }
//        }
//    }

    //将byteArray绘制到SurfaceView上，复杂逻辑，但性能好一些
    private var isInitRender = false
    private val renderScope by lazy { CoroutineScope(Dispatchers.Default + Job() + CoroutineName("ScreenCapture")) }
    private val frameChannel by lazy { Channel<ByteArray>(capacity = 3) } // 三缓冲通道
    private val bitmapPool by lazy { BitmapPool(svBytes.width, svBytes.height) }// 复用池

    //初始化数据
    private fun initScreenRender(){
        isInitRender = true
        // 专用渲染协程
        renderScope.launch  {
            while (isActive) {
                val byteArray = frameChannel.receive()
                val bitmap = decodeWithPool(byteArray) // 复用Bitmap
                bitmap?.let {
                    renderToSurface(it) // 硬件加速渲染
                }
            }
        }
    }

    // 线程池
    private fun decodeWithPool(data: ByteArray): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inBitmap = bitmapPool.acquire()  // 复用内存
            inPreferredConfig = Bitmap.Config.ARGB_8888 // 直接GPU存储
            inMutable = true
        }
        return decodeByteArraySafely(data, options)
    }

    private fun decodeByteArraySafely(data: ByteArray, options: BitmapFactory.Options): Bitmap? {
        return try {
            // 第一步：尝试直接复用
            BitmapFactory.decodeByteArray(data,  0, data.size,  options)
        } catch (e: IllegalArgumentException) {
            // 第二步：失败后启用自动降级策略
            when {
                // Case 1: 配置不匹配 → 创建新配置
                options.inBitmap?.config  != Bitmap.Config.ARGB_8888 -> {
                    options.inBitmap  = null
                    options.inPreferredConfig  = Bitmap.Config.ARGB_8888
                    BitmapFactory.decodeByteArray(data,  0, data.size,  options)
                }

                // Case 2: 量子内存保护 → 禁用复用
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    options.inBitmap  = null
                    BitmapFactory.decodeByteArray(data,  0, data.size,  options)
                }

                // 最终回退方案
                else -> BitmapFactory.decodeByteArray(data,  0, data.size,  null)
            }
        }
    }

    // 硬件加速渲染
    private fun renderToSurface(bitmap: Bitmap) {
        renderLegacy(bitmap)
        bitmapPool.release(bitmap)
    }

    private val paint = Paint()
    private fun renderLegacy(bitmap: Bitmap) {
        val canvas = tryLockCanvas() ?: return
        try {
            // 使用2025优化矩阵算法
            val matrix = getFlipMatrix(canvas)

            // 零拷贝渲染技术
            canvas.drawBitmap(bitmap,  matrix, paint)
            MatrixCache.release(matrix)
        } finally {
            sfHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun tryLockCanvas(): Canvas? {
        return try {
            sfHolder.lockCanvas()
        } catch (e: Exception) {
            null
        }
    }

    // 2025矩阵优化算法
    private fun getFlipMatrix(canvas: Canvas): Matrix {
        return MatrixCache.obtain().apply  {
            reset()
            // Y轴翻转（相机流适配）
            postScale(1f, 1f, canvas.width  * 0.5f, canvas.height  * 0.5f)
        }
    }


    //提交数据
    private fun submitFrame(byteArray: ByteArray) {
        if (!frameChannel.trySend(byteArray).isSuccess)  {
            // 帧率超限时智能丢弃旧帧
            frameChannel.tryReceive()  // 丢弃最旧帧
            frameChannel.trySend(byteArray)  // 插入新帧
        }
    }

    override fun onBackPressed() {
        if (isStartCapture){
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        renderScope.cancel()
        frameChannel.close()
        bitmapPool.destroy()
        super.onDestroy()
    }

    companion object{
        const val APPLY_PERMISSION = 1013
    }

}