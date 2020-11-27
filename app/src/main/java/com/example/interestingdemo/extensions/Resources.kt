package com.example.interestingdemo.extensions

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt


//dp转像素
fun Resources.dp2Px(dp : Float) : Int{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,displayMetrics).roundToInt()
}

//像素转dp
fun Resources.px2dp(px : Int) : Float{
    return px / displayMetrics.density
}