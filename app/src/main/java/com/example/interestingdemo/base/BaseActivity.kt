package com.example.interestingdemo.base

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.common_title_layout.*

abstract class BaseActivity: AppCompatActivity() {

    fun initTitle(title: String){
        ivBack.setOnClickListener { finish() }
        tvTitle.text = title
    }
}