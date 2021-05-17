package com.example.interestingdemo.Util

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.example.interestingdemo.R
import java.util.*

object DialogUtil {
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
//        window?.setWindowAnimations(R.style.pop_up_window_anim)
    }
}