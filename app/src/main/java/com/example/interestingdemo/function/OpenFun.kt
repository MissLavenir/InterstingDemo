package com.example.interestingdemo.function

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*

open class OpenFun {

    /**
     * 创建二维码
     * @param string 字符串内容
     * @param width 宽度
     * @param height 高度
     * @param error_level 容错率L：7% M：15% Q：25% H：35%
     * @param logoBitmap 中心logo图片
     * @param logoPercent logo所占百分比[0-1]之间。
     * @param backgroundBitmap 背景logo图片
     */
    fun createQRImage(string : String,width : Int, height : Int, error_level : String ?= "H", logoBitmap : Bitmap ?= null, logoPercent : Float ?= 0.15F,backgroundBitmap: Bitmap ?= null): Bitmap? {
        if (string == "" || string.isEmpty()) return null
        val hints = Hashtable<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        val bitMatrix = QRCodeWriter().encode(string, BarcodeFormat.QR_CODE, width, height, hints)
        val pixels = IntArray(width*height)

        if (!TextUtils.isEmpty(error_level)){
            hints[EncodeHintType.ERROR_CORRECTION] = error_level
        }

        //渲染颜色
        if (backgroundBitmap != null){
            val background = Bitmap.createScaledBitmap(backgroundBitmap,width,height,false)
            for ( i in 0 until width){
                for (j in 0 until height){
                    pixels[i*width+j] = if (bitMatrix.get(j,i)) Color.BLACK else background.getPixel(j,i)
                }
            }
        }else{
            for ( i in 0 until width){
                for (j in 0 until height){
                    pixels[i*width+j] = if (bitMatrix.get(j,i)) Color.BLACK else Color.WHITE
                }
            }
        }

        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        if (logoBitmap != null){
            return addLogo(bitmap,logoBitmap,logoPercent ?: 0.2F)
        }
        return bitmap
    }


    /**
     * 给二维码加上log
     * @srcBitmap 二维码原图
     * @logoBitmap logo图片
     * @logoPercent logo图片百分比[0-1],不能过大会导致二维码扫描失败，建议0.2F
     */
    private fun addLogo(srcBitmap : Bitmap, logoBitmap : Bitmap, logoPercent: Float) : Bitmap{
        val srcWidth = srcBitmap.width
        val srcHeight = srcBitmap.height
        val logoWidth = logoBitmap.width
        val logoHeight = logoBitmap.height

        val scaleWidth : Float
        val scaleHeight : Float

        if (logoPercent < 0F || logoPercent > 1F) {
            scaleWidth = srcWidth * 0.2F / logoWidth
            scaleHeight = srcHeight * 0.2F / logoHeight
        }else{
            scaleWidth = srcWidth * logoPercent / logoWidth
            scaleHeight = srcHeight * logoPercent / logoHeight
        }

        val bitmap = Bitmap.createBitmap(srcWidth,srcHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap,0F,0F,null)
        canvas.scale(scaleWidth,scaleHeight,srcWidth/2F,srcHeight/2F)
        canvas.drawBitmap(logoBitmap, srcWidth/2F - logoWidth/2, srcHeight/2F - logoHeight/2, null)

        return bitmap
    }


}