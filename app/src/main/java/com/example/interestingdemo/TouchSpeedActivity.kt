package com.example.interestingdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import com.example.interestingdemo.Util.DialogUtil
import kotlinx.android.synthetic.main.activity_touch_speed.*
import kotlin.math.abs

class TouchSpeedActivity : AppCompatActivity() {

    private var mTracker: VelocityTracker? = null
    private var xSpeed = 0F
    private var ySpeed = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_speed)
        getSpeed.text = "任意滑动即开启测试..."

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                mTracker?.clear()
                mTracker = VelocityTracker.obtain()
                mTracker?.addMovement(event)
            }
            MotionEvent.ACTION_MOVE -> {
                mTracker?.apply {
                    val pointerId = event.getPointerId(event.actionIndex)
                    addMovement(event)
                    computeCurrentVelocity(1000)
                    xSpeed = getXVelocity(pointerId).let {
                        if (it >= 0.0) it else abs(it)
                    }
                    ySpeed = getYVelocity(pointerId).let {
                        if (it >= 0.0) it else abs(it)
                    }
                    getSpeed.text = String.format("X轴速度为%s\n Y轴速度为%s",xSpeed,ySpeed)
                }
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                mTracker?.recycle()
                mTracker = null
                isHighSpeed()
            }
        }
        return true
    }

    private fun isHighSpeed(){
        if (xSpeed > 30000F || ySpeed > 30000F){
            DialogUtil.showCommonDialog(this,"Σ(っ °Д °;)っ","你滑动超速了！！！！")
        }
    }
}