package com.example.interestingdemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.interestingdemo.extensions.isAlive
import com.example.interestingdemo.extensions.toast
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_crop_picture.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


@Suppress("DEPRECATION")
class CropPicture : Fragment() {

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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when(requestCode){
                    REQUEST_IMAGE -> {
                        data?.data?.let { uri ->
                            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
                            val alert = AlertDialog.Builder(context).setView(dialog).create()
                            dialog.sureTitle.text = "提示："
                            dialog.sureMessage.text = "是否裁剪图片"
                            dialog.sureBtn.setOnClickListener {
                                startUCropPicture(uri)
                                alert.dismiss()
                            }
                            dialog.cancelBtn.setOnClickListener {
                                getPicture.setImageURI(uri)
                                alert.dismiss()
                            }
                            alert.show()

                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        if (data != null){
                            UCrop.getOutput(data)?.let { uri ->
                                getPicture.setImageURI(uri)
                            }
                        }
                    }
                }
            }
            UCrop.RESULT_ERROR -> {
                toast("图片裁剪失败,请重试！")
            }
            else -> {
                toast("未知错误")
            }
        }
    }

    @AfterPermissionGranted(FILE_WRITE_PERMISSION)
    private fun startUCropPicture(uri: Uri){
        if (!isAlive()) return
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(),*perms)){
            cropRawPicture(uri)
        }else{
            EasyPermissions.requestPermissions(this,"需要文件读写权限才能裁剪", FILE_WRITE_PERMISSION,*perms)
        }
    }


    private fun cropRawPicture(uri : Uri){
        val options = UCrop.Options()
        val destinationUri = Uri.fromFile(File(context?.cacheDir,"uCrop.jpg"))
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

    companion object {
        const val REQUEST_IMAGE = 1528

        const val FILE_WRITE_PERMISSION = 1

    }
}