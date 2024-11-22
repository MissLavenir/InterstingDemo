package com.example.interestingdemo.util

import android.app.Application
import android.widget.Toast

/**
 * 全局toast
 */
object ToastUtil {
    private lateinit var application: Application

    fun initUtil(application: Application){
        this.application = application
    }

    /**
     * 全局toast
     */
    fun show(message: String){
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }

}