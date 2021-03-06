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

/**
 * 系统动画时间：短
 */
val Resources.systemShortAnimTime : Long
    get() = getInteger(android.R.integer.config_shortAnimTime).toLong()

/**
 * 系统动画时间：中等
 */
val Resources.systemMediumAnimTime: Long
    get() = getInteger(android.R.integer.config_mediumAnimTime).toLong()

/**
 * 系统动画时间：长
 */
val Resources.systemLongAnimTime: Long
    get() = getInteger(android.R.integer.config_longAnimTime).toLong()

/**
 * dp转像素
 */
fun Resources.dp2px(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).roundToInt()
}