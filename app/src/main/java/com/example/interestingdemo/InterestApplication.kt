package com.example.interestingdemo

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class InterestApplication : MultiDexApplication() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}