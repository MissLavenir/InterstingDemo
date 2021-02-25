package com.example.interestingdemo.extensions

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * 判断给定的x,y坐标是否落在View上
 *
 * @param x 相对于父层的x
 * @param y 相对于父层的y
 */
fun View.hitTest(x: Int, y: Int): Boolean {
    val tx = translationX
    val ty = translationY

    val left = left + tx
    val right = right + tx
    val top = top + ty
    val bottom = bottom + ty

    return (x >= left) && (x <= right) && (y >= top) && (y <= bottom)
}

/**
 * 非常快速的点击响应
 * 就在用户触碰屏幕的一瞬间
 *
 * 通过setOnTouchListener实现，并且不会阻塞其逻辑运行，因为要确保波纹动画的进行
 * 因此使用时切记不要再对View设置普通的onClickListener和setOnLongClickListener以及setOnTouchListener
 */
@SuppressLint("ClickableViewAccessibility")
fun View.setRealTimeClickListener(onclickListener: View.OnClickListener){
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN){
            onclickListener.onClick(this)
        }
        false
    }
}