package com.example.interestingdemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.interestingdemo.Util.CameraUtil
import com.example.interestingdemo.extensions.isAlive
import com.example.interestingdemo.extensions.toast
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_crop_picture.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class CropPicture : Fragment(), EasyPermissions.PermissionCallbacks{

    private var tempPhoto = ""
    private var tempVideo = ""
    private lateinit var videoPlayer : SimpleExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crop_picture, container, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectPicture.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "请选择..."
            dialog.sureMessage.text = "请选择以下方式来获取手机图片"
            dialog.sureBtn.text = "拍照"
            dialog.cancelBtn.text = "相册"
            dialog.cancelBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.blue_300, context?.theme))
            dialog.sureBtn.setOnClickListener {
                startTake(REQUEST_TAKE_PHOTO)
                alert.dismiss()
            }
            dialog.cancelBtn.setOnClickListener {
                startChoose(REQUEST_CHOOSE_PHOTO)
                //打开文档
//                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                    addCategory(Intent.CATEGORY_OPENABLE)
//                    type = "image/*"
//                }
//                startActivityForResult(intent, REQUEST_CHOOSE_PHOTO)
                alert.dismiss()
            }
            alert.show()
        }

        selectVideo.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "请选择..."
            dialog.sureMessage.text = "请选择以下方式获取手机视频"
            dialog.sureBtn.text = "摄像"
            dialog.cancelBtn.text = "相册"
            dialog.cancelBtn.setTextColor(ResourcesCompat.getColor(resources, R.color.blue_300, context?.theme))
            dialog.sureBtn.setOnClickListener {
                startTake(REQUEST_TAKE_VIDEO)
                alert.dismiss()
            }
            dialog.cancelBtn.setOnClickListener {
                startChoose(REQUEST_CHOOSE_VIDEO)
                alert.dismiss()
            }
            alert.show()
        }

        speed0_2.setOnClickListener { changeSpeed(0.2f) }
        speed0_5.setOnClickListener { changeSpeed(0.5f) }
        speed0_75.setOnClickListener { changeSpeed(0.75f) }
        speed1_0.setOnClickListener { changeSpeed(1.0f) }
        speed1_25.setOnClickListener { changeSpeed(1.25f) }
        speed1_5.setOnClickListener { changeSpeed(1.5f) }
        speed2_0.setOnClickListener { changeSpeed(2.0f) }
        speed2_5.setOnClickListener { changeSpeed(2.5f) }
        speed3_0.setOnClickListener { changeSpeed(3.0f) }

        speedCurrent.setOnClickListener {
            speedCurrent.visibility = View.GONE
            speedLayout.visibility = View.VISIBLE
        }

        deletePicture.setOnClickListener {
            getPicture.visibility = View.GONE
            deletePicture.visibility = View.GONE
        }

        videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        getVideo.player = videoPlayer

    }

    private fun playVideo(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        videoPlayer.setMediaItem(mediaItem)
        videoPlayer.prepare()
        videoPlayer.play()
    }


    private fun changeSpeed(speed: Float){
        speedCurrent.text = "×$speed"
        speedCurrent.visibility = View.VISIBLE
        speedLayout.visibility = View.GONE
        val playbackParam = PlaybackParameters(speed)
        videoPlayer.setPlaybackParameters(playbackParam)

    }

    override fun onPause() {
        super.onPause()
        videoPlayer.playWhenReady = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when(requestCode){
                    REQUEST_CHOOSE_PHOTO -> {
                        data?.data?.let { uri ->
                            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
                            val alert = AlertDialog.Builder(context).setView(dialog).create()
                            dialog.sureTitle.text = "提示："
                            dialog.sureMessage.text = "是否裁剪图片"
                            dialog.sureBtn.setOnClickListener {
                                cropRawPicture(uri)
                                alert.dismiss()
                            }
                            dialog.cancelBtn.setOnClickListener {
                                getPicture.setImageURI(uri)
                                getPicture.visibility = View.VISIBLE
                                deletePicture.visibility = View.VISIBLE
                                alert.dismiss()
                            }
                            alert.show()
                        }
                    }

                    REQUEST_TAKE_PHOTO -> {
                        val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
                        val alert = AlertDialog.Builder(context).setView(dialog).create()
                        dialog.sureTitle.text = "提示："
                        dialog.sureMessage.text = "是否裁剪图片"
                        dialog.sureBtn.setOnClickListener {
                            cropRawPicture(File(tempPhoto).toUri())
                            alert.dismiss()
                        }
                        dialog.cancelBtn.setOnClickListener {
                            getPicture.setImageURI(tempPhoto.toUri())
                            getPicture.visibility = View.VISIBLE
                            deletePicture.visibility = View.VISIBLE
                            alert.dismiss()
                        }
                        alert.show()

                    }

                    UCrop.REQUEST_CROP -> {
                        if (data != null){
                            UCrop.getOutput(data)?.let { uri ->
                                getPicture.setImageURI(uri)
                                getPicture.visibility = View.VISIBLE
                                deletePicture.visibility = View.VISIBLE
                            }
                        }
                    }

                    REQUEST_CHOOSE_VIDEO -> {
                        if (data != null){
                            val uri = data.data
                            if (uri != null){
                                playVideo(uri)
                            }
                        }
                    }

                    REQUEST_TAKE_VIDEO -> {
                        if (tempVideo.isNotBlank()){
                            playVideo(tempVideo.toUri())
                        }
                    }
                }
            }
            UCrop.RESULT_ERROR -> {
                toast("图片裁剪失败,请重试！")
            }
            else -> {
                //取消
            }
        }
    }


    private fun startChoose(requestCode: Int){
        if (!isAlive()) return
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(),*perms)){
            when(requestCode){
                REQUEST_CHOOSE_PHOTO -> {
                    CameraUtil.choosePhoto(this, requestCode)
                }
                REQUEST_CHOOSE_VIDEO -> {
                    CameraUtil.chooseVideo(this, requestCode)
                }
            }
        }else{
            EasyPermissions.requestPermissions(this,"需要文件读写权限才能裁剪", FILE_WRITE_PERMISSION,*perms)
        }
    }


    private fun startTake(requestCode: Int){
        if (!isAlive()) return
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(requireContext(), *perms)){
            when(requestCode){
                REQUEST_TAKE_PHOTO -> {
                    tempPhoto = CameraUtil.takePhoto(this, requestCode)
                }
                REQUEST_TAKE_VIDEO -> {
                    tempVideo = CameraUtil.takeVideo(this, requestCode)
                }
            }

        }else{
            EasyPermissions.requestPermissions(this,"需要相机、文件读写权限才能拍照", TAKE_PHOTO_PERMISSION,*perms)
        }
    }


    private fun cropRawPicture(uri : Uri){
        val options = UCrop.Options()
        val tempString = System.currentTimeMillis()
        val destinationUri = Uri.fromFile(File(context?.cacheDir,"uCrop${tempString}.jpg"))
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)//输出格式
        options.setMaxScaleMultiplier(2f)//最大缩放比
        options.setFreeStyleCropEnabled(true)//框宽高比可变,默认为false,建议为true
        options.setToolbarTitle("正在进行裁剪")//标题栏的文字
        options.setHideBottomControls(false)//隐藏下方控制栏，默认false
        options.setCircleDimmedLayer(false)//圆形裁剪框，默认false
        options.setToolbarColor(ResourcesCompat.getColor(resources,R.color.black,context?.theme))//标题栏颜色
        options.setToolbarWidgetColor(ResourcesCompat.getColor(resources,R.color.white,context?.theme))//标题和按钮颜色
        options.setStatusBarColor(ResourcesCompat.getColor(resources,R.color.black,context?.theme))//最上方状态栏颜色
        options.setCropGridColumnCount(2)//剪辑框内纵向网格线数量
        options.setCropGridRowCount(2)//剪辑框内横向网格线数量
        options.setCropGridStrokeWidth(1)//剪辑框网格线宽度
        options.setCropGridColor(ResourcesCompat.getColor(resources,R.color.white,context?.theme))//剪辑框网格线颜色
        options.setCropFrameStrokeWidth(2)//剪辑框宽度
        options.setCropFrameColor(ResourcesCompat.getColor(resources,R.color.red_a200,context?.theme))//裁剪框颜色
//        options.setToolbarCropDrawable(R.drawable.up_down_move)//设置确定图标
//        options.setToolbarCancelDrawable(R.drawable.up_down_move)//设置返回图标
//        options.setCropGridCornerColor(ResourcesCompat.getColor(resources,R.color.grey_500,context?.theme))//剪辑框四角颜色
//        options.setDimmedLayerColor(ResourcesCompat.getColor(resources,R.color.grey_300,context?.theme))//裁剪框外的颜色

        UCrop.of(uri,destinationUri)
            .withAspectRatio(1f,1f)
            .withMaxResultSize(500,500)
            .withOptions(options)
            .start(requireContext(),this,UCrop.REQUEST_CROP)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()

    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        toast("已授权")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        toast("您未授权")
    }

    companion object {
        const val REQUEST_CHOOSE_PHOTO = 1528

        const val REQUEST_TAKE_PHOTO = 1529

        const val REQUEST_CHOOSE_VIDEO = 1530

        const val REQUEST_TAKE_VIDEO = 1531

        const val FILE_WRITE_PERMISSION = 111

        const val TAKE_PHOTO_PERMISSION = 112

    }
}