package com.example.interestingdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import com.example.interestingdemo.extensions.toast
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_crop_picture.*


@Suppress("DEPRECATION")
class CropPicture : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crop_picture, container, false)
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
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_IMAGE -> {
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
        }else{
            toast("图片裁剪失败,请重试！")
        }
    }


    private fun cropRawPicture(uri : Uri){
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)//输出格式
        options.setMaxScaleMultiplier(2f)//最大缩放比
        options.setCropGridColor(Color.RED)//剪辑框内线条颜色
        options.setCropGridColumnCount(2)//剪辑框内竖线数量
        options.setCropGridRowCount(2)//剪辑框内横线数量
        options.setFreeStyleCropEnabled(true)//键即框宽高比可变

        UCrop.of(uri,uri)
            .withAspectRatio(1f,1f)
            .withMaxResultSize(500,500)
            .withOptions(options)
            .start(requireContext(),this,UCrop.REQUEST_CROP)
    }

    companion object {
        const val REQUEST_IMAGE = 1528

    }
}