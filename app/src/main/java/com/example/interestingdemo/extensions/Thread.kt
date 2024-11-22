package com.example.interestingdemo.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 延迟加载
 */
fun loadingDelay(times: Long = 200, onCallBack: () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch {
        delay(times)
        launch(Dispatchers.Main) {
            onCallBack.invoke()
            cancel()
        }
    }

}