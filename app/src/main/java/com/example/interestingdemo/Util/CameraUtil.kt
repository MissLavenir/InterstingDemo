package com.example.interestingdemo.Util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.lang.Exception

class CameraUtil {

    companion object{
        const val PHOTO_TAKE = 101
        const val PHOTO_CHOOSE = 102
        const val VIDEO_TAKE = 103
        const val VIDEO_CHOOSE = 104
        private const val logTag = "CameraUtil"
        var tempPhotoPath = ""
        var tempVideoPath = ""

        /**
         * @param fileName 不带/ 带jpg后缀
         *                 eg. 123456.jpg
         * @return "/storage/emulated/0/Android/data/com.example.interestingdemo/cache/id/123456.jpg"
         */
        fun getPhotoPath(context: Context, fileName: String) : String {
            return getFileParentPath(context) + fileName
        }

        private fun getFileParentPath(context: Context) : String {
            var photoPath = ""
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()){
                val cacheFile = ContextCompat.getExternalCacheDirs(context)
                if (cacheFile.isNotEmpty() && cacheFile[0] != null){
                    photoPath += cacheFile[0].absolutePath + File.separator + "id" + File.separator
                }
            } else {
                photoPath += context.cacheDir.absolutePath + File.separator + "id" + File.separator
            }
            val file = File(photoPath)
            if (file.exists()){
                val mk = file.mkdirs()
                Log.d(logTag, "photoPath not exist ,so mkdirs = $mk")
            }
            return photoPath

        }

        /**
         * 选择图片（不带code值）
         */
        fun choosePhoto(activity : Activity){
            choosePhoto(activity, PHOTO_CHOOSE)
        }
        /**
         * 选择图片（带code值）
         */
        fun choosePhoto(activity: Activity, requestCode: Int){
            try {
                val intent = Intent().setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    intent.action = Intent.ACTION_PICK
                } else {
                    intent.action = Intent.ACTION_GET_CONTENT
                }
                val wrapperIntent = Intent.createChooser(intent, "选择图片")
                activity.startActivityForResult(wrapperIntent,requestCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun choosePhoto(@NonNull fragment: Fragment){
            choosePhoto(fragment, PHOTO_CHOOSE)
        }

        fun choosePhoto(@NonNull fragment: Fragment, requestCode: Int){
            try {
                val intent = Intent()
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    intent.action = Intent.ACTION_PICK
                } else {
                    intent.action = Intent.ACTION_GET_CONTENT
                }
                val wrapperIntent = Intent.createChooser(intent, "选择图片")
                fragment.startActivityForResult(wrapperIntent, requestCode)
            } catch (e : Exception){
                e.printStackTrace()
            }
        }

        /**
         * 拍照
         */
        fun takePhoto(activity: Activity) : String {
            tempPhotoPath = getFileParentPath(activity) + File.separator + "id.png"
            return takePhoto(activity, PHOTO_TAKE, tempPhotoPath)
        }

        /* 拍照，指定照片保存路径 picPath，会直接保存照片流到picPath，不再返回data */
        fun takePhoto(activity: Activity, requestCode: Int, picPath : String) : String{
            val file = File(picPath)
            if (file.exists()){
                val del = file.delete()
                Log.d(logTag, "delete file camera.png + $del")
            }
            val intentGraph = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intentGraph.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentGraph.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val uri = getFileUri(activity, file)
            intentGraph.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            activity.startActivityForResult(intentGraph, requestCode)
            return picPath
        }

        fun takePhoto(@NonNull fragment: Fragment, requestCode: Int) : String{
            fragment.context?.let { context ->
                tempPhotoPath = getFileParentPath(context) + File.separator + "id.png"
            }

            return takePhoto(fragment, requestCode, tempPhotoPath)

        }
        fun takePhoto(@NonNull fragment: Fragment, requestCode: Int, picString: String) : String{
            fragment.context?.let { context ->
                val file = File(picString)
                if (file.exists()){
                    val del = file.delete()
                    Log.d(logTag, "delete file camera.png + $del")
                } else {
                    file.mkdirs()
                }
                val intentGraph = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intentGraph.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intentGraph.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val uri = getFileUri(context, file)
                intentGraph.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                fragment.startActivityForResult(intentGraph, requestCode)
            } ?: kotlin.run {
                Log.e(logTag, "fragment context is null")
            }
            return picString
        }

        /*请注意，这里拿的是缓存文件，缓存文件会被重复使用！有可能被覆盖的风险*/
        fun getTempFileUri(context: Context) : Uri{
            tempPhotoPath = getFileParentPath(context) + File.separator + "id.png"
            return getFileUri(context, tempPhotoPath)
        }

        fun getFileUri(context: Context, filePath: String) : Uri{
            return getFileUri(context, File(filePath))
        }

        fun getFileUri(context: Context, file: File) : Uri{
            val uri : Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uri = FileProvider.getUriForFile(context,"com.example.interestingdemo.fileProvider", file )
            } else {
                uri = Uri.fromFile(file)
            }
            return uri
        }


        /**
         * 选择视频
         */
        fun chooseVideo(activity: Activity){
            chooseVideo(activity, VIDEO_CHOOSE)
        }

        fun chooseVideo(activity: Activity, requestCode: Int){
            val intent = Intent().apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*")
                action = Intent.ACTION_GET_CONTENT
            }
            val wrapperIntent = Intent.createChooser(intent,"选择视频")
            activity.startActivityForResult(wrapperIntent, requestCode)
        }

        fun chooseVideo(fragment: Fragment){
            chooseVideo(fragment, VIDEO_CHOOSE)
        }

        fun chooseVideo(fragment: Fragment, requestCode: Int){
            val intent = Intent().apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*")
                action = Intent.ACTION_GET_CONTENT
            }
            val wrapperIntent = Intent.createChooser(intent, "选择视频")
            fragment.startActivityForResult(wrapperIntent, requestCode)
        }

        /**
         * 拍视频
         */
        fun takeVideo(activity: Activity) : String{
            tempVideoPath = getFileParentPath(activity) + File.separator + "id.mp4"
            return takeVideo(activity, VIDEO_TAKE, tempVideoPath)
        }

        fun takeVideo(activity: Activity, requestCode: Int, videoPath: String) : String{
            val file = File(videoPath)
            if (file.exists()){
                val del = file.delete()
                Log.d(logTag, "delete file camera + $del")
            }
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val uri = getFileUri(activity, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            activity.startActivityForResult(intent, requestCode)
            return videoPath
        }

        fun takeVideo(fragment: Fragment, requestCode: Int) : String{
            fragment.context?.let { context ->
                tempVideoPath = getFileParentPath(context) + File.separator + "id.mp4"
            }
            return takeVideo(fragment, requestCode, tempVideoPath)
        }

        fun takeVideo(fragment: Fragment, requestCode: Int, videoPath: String) : String{
            fragment.context?.let { context ->
                val file = File(videoPath)
                if (file.exists()){
                    val del = file.delete()
                    Log.d(logTag, "delete file camera + $del")
                }else {
                    file.mkdirs()
                }
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val uri = getFileUri(context, file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                fragment.startActivityForResult(intent, requestCode)
            } ?: kotlin.run {
                Log.e(logTag, "fragment context is null")
            }
            return videoPath
        }

    }
}