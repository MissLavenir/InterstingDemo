package com.example.interestingdemo.extensions

import android.util.Log
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

fun Fragment.finish(){
    activity?.finish()
}

fun Fragment.dLog(tag:String, string: String){
    Log.d("debug_$tag",string)
}

fun Fragment.eLog(tag:String, string: String){
    Log.e("debug_$tag",string)
}
