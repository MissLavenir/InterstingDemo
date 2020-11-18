package com.example.interstingdemo.extensions

import androidx.fragment.app.Fragment


fun Fragment.runOnUiThread(runnable: Runnable){
    activity?.runOnUiThread(runnable)
}

fun Fragment.isAlive():Boolean{
    return activity != null && !isDetached && isAdded && !isRemoving && view != null
}

