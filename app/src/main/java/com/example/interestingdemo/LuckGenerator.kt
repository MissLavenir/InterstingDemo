package com.example.interestingdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.example.interestingdemo.extensions.runOnUiThread
import kotlinx.android.synthetic.main.fragment_luck_generator.*
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat


class LuckGenerator : Fragment() {
    private val autoUpdate = AutoUpdateStatus()
    private var isSpecial = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_luck_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoUpdate.start()
        start.setOnClickListener {
            start.visibility = View.GONE
            end.visibility = View.VISIBLE
            autoUpdate.resumeThread()
        }
        end.setOnClickListener {
            start.visibility = View.VISIBLE
            end.visibility = View.GONE
            autoUpdate.pauseThread()
        }
        specialTest.setOnClickListener {
            specialTest.setTextColor(ResourcesCompat.getColor(resources,R.color.blue_500,context?.theme))
            specialTest.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f)
            normalTest.setTextColor(ResourcesCompat.getColor(resources,R.color.grey_600,context?.theme))
            normalTest.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
            relationStatus.visibility = View.VISIBLE
            isSpecial = true
        }
        normalTest.setOnClickListener {
            specialTest.setTextColor(ResourcesCompat.getColor(resources,R.color.grey_600,context?.theme))
            specialTest.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
            normalTest.setTextColor(ResourcesCompat.getColor(resources,R.color.blue_500,context?.theme))
            normalTest.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f)
            relationStatus.visibility = View.GONE
            isSpecial = false
        }

    }

    inner class AutoUpdateStatus : Thread(){
        private var pause = true
        private val lock = Object()

        fun pauseThread(){
            pause = true
        }

        fun resumeThread(){
            pause = false
            synchronized(lock){
                lock.notifyAll()
            }
        }

        private fun onPauseLock(){
            synchronized(lock){
                try {
                    lock.wait()
                }catch (e : InterruptedException){
                    e.printStackTrace()
                }
            }
        }

        override fun run() {
            super.run()
            try {
                while (true){
                    while (pause){
                        onPauseLock()
                    }
                    try {
                        if (!isAlive) break
                        sleep(getTextInt(createTime)*1000.toLong())
                        runOnUiThread(Runnable {
                            if (isSpecial){
                                getSpecialLuck()
                            }else{
                                getNormalLuck()
                            }
                        })
                    }catch (e:InterruptedException){
                        break
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    private fun getTextInt(editText: AppCompatEditText) : Int{
        return if (editText.text.isNullOrEmpty()) 0 else editText.text.toString().toInt()
    }

    private fun getTwoDigits(number : Double): String{
        val format = DecimalFormat("0.##")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

    @SuppressLint("SetTextI18n")
    private fun getNormalLuck(){
        val luck = ((0..100).random() + (0..100).random() + (0..100).random())/3
        luckValue.text = "$luck"

        when (luck) {
            0 -> {
                luckTip.text = "已经没有人运气比你更差了，真的。"
                luckValue.setTextColor(ResourcesCompat.getColor(resources, R.color.black, context?.theme))
            }
            in 1..10 -> {
                luckTip.text = "你可知道这运气是一百万人才出${luck}个的非酋啊。"
            }
            in 11..30 -> {
                luckTip.text = "请保持乐观，虽然你运气不好，但是还有${luck}%的人比你更差。"
            }
            in 31..40 -> {
                luckTip.text = "你只是比普通人的运气差了一些而已。"
            }
            in 41..60 -> {
                luckTip.text = "你只是个普通人。"
            }
            in 61..80 -> {
                luckTip.text = "你运气比普通人好一些，但是请不要过度相信自己的运气。"
            }
            in 81..90 -> {
                luckTip.text = "你的运气很好，不是每一件事情都能顺风顺水，但结果一定不会差。"
            }
            in 91..99 -> {
                luckTip.text = "总有那么一部分人，做什么都特别的顺利。"
            }
            100 -> {
                luckTip.text = "已经没有人比你运气更欧了，想干什么就干什么吧，结果永远不会差。"
                luckValue.setTextColor(ResourcesCompat.getColor(resources, R.color.red_a200, context?.theme))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getSpecialLuck(){
        val heartCount = when{
            getTextInt(heartRate) in (71 .. 80)->{
                val ran = (8 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(heartRate) in (60 .. 70) ->{
                val ran = (7 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(heartRate) in (81 .. 90) ->{
                val ran = (7 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(heartRate) in (91 .. 100) -> {
                val ran = (6 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            else -> {
                val ran = (0 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
        }
        val pressureCount = when{
            getTextInt(pressureRate) in (0 .. 20) -> {
                val ran = (4 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(pressureRate) in (71 .. 80) -> {
                val ran = (4 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }

            getTextInt(pressureRate) in (21 .. 30) -> {
                val ran = (6 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(pressureRate) in (61 .. 70) -> {
                val ran = (6 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            getTextInt(pressureRate) in (31 .. 60) -> {
                val ran = (8 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            else -> {
                val ran = (0 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
        }

        val moodCount = when(mood.selectedItemPosition) {
            0 -> {
                val ran = (4 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            1 -> {
                val ran = (6 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            2 -> {
                val ran = (8 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            3 -> {
                val ran = (7 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            4 -> {
                val ran = (2 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
            else -> {
                val ran = (0 .. 12).random().toFloat()/10
                ((0 .. 100).random() * ran).toInt()
            }
        }
        val total = (heartCount + pressureCount + moodCount)/100.0
        relate.text = "心率占比: ${getTwoDigits(heartCount/total)}%\n压力占比: ${getTwoDigits(pressureCount/total)}%\n心情占比: ${getTwoDigits(moodCount/total)}%\n"

        val luck = ((heartCount + pressureCount + moodCount)/3.0).toInt()
        luckValue.text = "$luck"

        when (luck) {
            0 -> {
                luckTip.text = "已经没有人运气比你更差了，真的。"
                luckValue.setTextColor(ResourcesCompat.getColor(resources,R.color.black,context?.theme))
            }
            in 1..10 -> {
                luckTip.text = "你可知道这运气是一百万人才出${luck}个的非酋啊。"
            }
            in 11..30 -> {
                luckTip.text = "请保持乐观，虽然你运气不好，但是还有${luck}%的人比你更差。"
            }
            in 31..40 -> {
                luckTip.text = "你只是比普通人的运气差了一些而已。"
            }
            in 41..60 -> {
                luckTip.text = "你只是个普通人。"
            }
            in 61..80 -> {
                luckTip.text = "你运气比普通人好一些，但是请不要过度相信自己的运气。"
            }
            in 81..90 -> {
                luckTip.text = "你的运气很好，不是每一件事情都能顺风顺水，但结果一定不会差。"
            }
            in 91..99 -> {
                luckTip.text = "总有那么一部分人，做什么都特别的顺利。"
            }
            100 -> {
                luckTip.text = "已经没有人比你运气更欧了，想干什么就干什么吧，结果永远不会差。"
                luckValue.setTextColor(ResourcesCompat.getColor(resources,R.color.red_a200,context?.theme))
            }
        }

    }

}