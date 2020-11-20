package com.example.interestingdemo.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.runOnUiThread(runnable: Runnable){
    activity?.runOnUiThread(runnable)
}

fun Fragment.isAlive():Boolean{
    return activity != null && !isDetached && isAdded && !isRemoving && view != null
}

fun Fragment.toast(string: String){
    Toast.makeText(this.context,string,Toast.LENGTH_LONG).show()
}
