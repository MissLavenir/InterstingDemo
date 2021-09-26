package com.example.interestingdemo

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.animation.*
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.example.interestingdemo.viewModel.CustomViewViewModel
import kotlinx.android.synthetic.main.activity_custom_view.*

class CustomViewActivity : AppCompatActivity() {

    private val mViewModel: CustomViewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

/**
 * TestViews的动画
        val animator = ObjectAnimator.ofFloat(customView,"progress",90f)
        animator.duration = 3000
        animator.repeatCount = -1
        animator.start()

        val animator2 = ObjectAnimator.ofFloat(customView, "linearTransX",900f)
        animator2.duration = 3000
        animator2.repeatCount = -1
        animator2.interpolator = LinearInterpolator()
        animator2.start()

        val animator3 = ObjectAnimator.ofFloat(customView, "acAndDeTransX",900f)
        animator3.duration = 3000
        animator3.repeatCount = -1
        animator3.interpolator = AccelerateDecelerateInterpolator()
        animator3.start()

        val animator4 = ObjectAnimator.ofFloat(customView, "acTransX",900f)
        animator4.duration = 3000
        animator4.repeatCount = -1
        animator4.interpolator = AccelerateInterpolator()
        animator4.start()

        val animator5 = ObjectAnimator.ofFloat(customView, "deTransX",900f)
        animator5.duration = 3000
        animator5.repeatCount = -1
        animator5.interpolator = DecelerateInterpolator()
        animator5.start()

        val animator6 = ObjectAnimator.ofFloat(customView, "anTransX",900f)
        animator6.duration = 3000
        animator6.repeatCount = -1
        animator6.interpolator = AnticipateInterpolator()
        animator6.start()

        val animator7 = ObjectAnimator.ofFloat(customView, "overTransX",900f)
        animator7.duration = 3000
        animator7.repeatCount = -1
        animator7.interpolator = OvershootInterpolator()
        animator7.start()

        val animator8 = ObjectAnimator.ofFloat(customView, "anAndOverTranX",900f)
        animator8.duration = 3000
        animator8.repeatCount = -1
        animator8.interpolator = AnticipateOvershootInterpolator()
        animator8.start()
         */

        val animator9 = ObjectAnimator.ofFloat(customView,"rotateY",-30f)
        animator9.duration = 2000
        animator9.repeatCount = -1
        animator9.repeatMode = ValueAnimator.REVERSE
        animator9.start()

        val animator10 = ObjectAnimator.ofFloat(customView,"textProgress",1f)
        animator10.duration = 3000
        animator10.repeatCount = -1
        animator10.repeatMode = ValueAnimator.REVERSE
        animator10.start()


        getData.setOnClickListener {
            mViewModel.getDataList()
        }

        mViewModel.itemList.observe(this) { list ->
            var string = ""
            list.forEach { model ->
                string += "${model.StoreName}\n"
            }
            getData.text = string
        }
    }

}