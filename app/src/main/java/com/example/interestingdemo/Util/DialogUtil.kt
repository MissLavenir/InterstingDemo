package com.example.interestingdemo.Util

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.example.interestingdemo.R
import java.util.*

object DialogUtil {

    fun showCommonDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("确定", null)
        builder.show()
    }

    fun showCallPhoneDialog(context: Context, phone: String){
        if (phone.isEmpty()){
            Toast.makeText(context, "电话号码不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val alert = AlertDialog.Builder(context)
        alert.setMessage("请问：\n是否拨打此电话号码：\n${phone}")
        alert.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        alert.setPositiveButton("拨打"){ dialog, _ ->
            val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel$phone"))
            if (ActivityCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "没有拨打电话的权限", Toast.LENGTH_SHORT).show()
            } else {
                context.startActivity(intent)
            }
            dialog.dismiss()
        }
        alert.setCancelable(true)
        alert.create().show()

    }

    /* 年月日时分 */
    fun showDateYMDHMPicker(
        context: Context,
        ymdhmDateStr: String? = null,
        callBack: (String) -> Unit
    ) {
        val pvTime = TimePickerBuilder(context
        ) { date: Date, _ ->
            callBack(DateUtil.getYMDHMTime(date))
        }.setType(booleanArrayOf(true, true, true, true, true, false))
            .isDialog(true)
            .setItemVisibleCount(7)
            .build()
        if (ymdhmDateStr != null && ymdhmDateStr.isNotEmpty()) {
            val date = DateUtil.getYMDHMTimeDate(ymdhmDateStr)
            if (date != null) {
                val c = Calendar.getInstance()
                c.time = date
                pvTime.setDate(c)
            }
        }
        val wlp = pvTime.dialogContainerLayout?.layoutParams
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        pvTime.dialogContainerLayout?.layoutParams = wlp
        pvTime.dialog?.setGravity(Gravity.BOTTOM)
        pvTime.show()
    }

    private fun Dialog?.setGravity(
        gravity: Int = Gravity.BOTTOM,
        width: Int = WindowManager.LayoutParams.MATCH_PARENT,
        height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        @DrawableRes backgroundDrawable: Int = R.color.white,
        hPadding: Int = 0,
        vPadding: Int = 0
    ) {
        val window = this?.window
        window?.setBackgroundDrawableResource(backgroundDrawable)
        window?.decorView?.setPadding(vPadding, hPadding, vPadding, hPadding)
        val wlp = window?.attributes
        wlp?.gravity = gravity
        wlp?.width = width
        wlp?.height = height
        window?.attributes = wlp
        //fragment动画默认为从中心伸展出来
        //下面的代码可以改成从下方弹出
        window?.setWindowAnimations(R.style.pop_up_window_anim)
    }
}