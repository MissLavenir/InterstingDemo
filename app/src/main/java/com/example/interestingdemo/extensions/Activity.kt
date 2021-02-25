package com.example.interestingdemo.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.ResultReceiver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.StringRes


/**
 * 隐藏软键盘
 */
fun Activity.hideSoftKeyBoard(flag: Int = 0, receiver: ResultReceiver? = null){
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken,flag,receiver)
    }
}

/**
 * 启动安全显示
 * 启用后不可截图、不可录屏、概览窗口也看不到
 * 对root后的手机可能无效
 */
fun Activity.secureDisplay(enable: Boolean = true){
    if (enable){
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }else{
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

/**
 * 设置状态栏的颜色
 * 如果是Android LOLLIPOP以下，则没有效果
 *
 * @param color 色彩int值，非资源id，可用Color.parseColor("#ef5350")得到Int值
 */
fun Activity.setStatusBarColor(@ColorInt color : Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        val window = window
        //这一步最好要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or  WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        getWindow().statusBarColor = color
    }
}

/**
 * 向系统表示要分享一段文字
 *
 * @param content 要分享的内容
 * @param titleRes 分享的标题的文字资源
 */
fun Activity.shareTextContent(content : String, titleRes : String = "分享到"){
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, content)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, titleRes))
}
