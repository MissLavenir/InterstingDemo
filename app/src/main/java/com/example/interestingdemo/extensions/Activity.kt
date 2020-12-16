package com.example.interestingdemo.extensions

import android.app.Activity
import android.content.Context
import android.os.ResultReceiver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


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
