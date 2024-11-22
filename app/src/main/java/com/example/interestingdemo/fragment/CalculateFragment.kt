package com.example.interestingdemo.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.interestingdemo.R
import com.example.interestingdemo.extensions.dp2px
import com.example.interestingdemo.extensions.systemMediumAnimTime
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_calculate.*
import kotlinx.android.synthetic.main.item_calculate_problem.view.*


class CalculateFragment : Fragment() {
    private val countNumber = MutableLiveData<Int>()
    private val kindArray = ArrayList<Int>()
    private var maxNumber = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kindArray.add(0)
        kindArray.add(1)
        kindArray.add(2)
        kindArray.add(3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//先初始化一次
        reCreateProblem()

        //监听分数
        countNumber.observe(viewLifecycleOwner, Observer {
            getCount.text = countNumber.value.toString()
            if (getCount.text.toString().toInt() >= 60){
                getCount.setTextColor(ResourcesCompat.getColor(resources, R.color.red_300,context?.theme))
            } else {
                getCount.setTextColor(ResourcesCompat.getColor(resources, R.color.grey_800,context?.theme))
            }
        })

        create.setOnClickListener {
            if (calculateContainer.size < 100){
                for (i in 0..10){
                    createProblem()
                }
            } else {
                toast("已经达到了题目上限。")
            }
        }

        setting.setOnClickListener {
            showSettingPanel()
        }
        overlay.setOnClickListener {
            hideSettingPanel()
        }

        reCreate.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            alert.setCancelable(true)
            dialog.sureTitle.text = "重置确认"
            dialog.sureMessage.text = "正在重置题目，重置后会情况现有的题目和分数，请确定。"
            dialog.sureBtn.setOnClickListener {
                reCreateProblem()
                alert.dismiss()
            }
            dialog.cancelBtn.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }

        changeBtn.setOnClickListener {
            maxNumber = when {
                oneNumber.isChecked -> {
                    9
                }
                twoNumber.isChecked -> {
                    99
                }
                threeNumber.isChecked -> {
                    999
                }
                else -> {
                    99
                }
            }
            if (mathPlus.isChecked){
                if (!kindArray.contains(0)){
                    kindArray.add(0)
                }
            }else{
                if (kindArray.contains(0)){
                    kindArray.remove(0)
                }
            }
            if (mathMinus.isChecked){
                if (!kindArray.contains(1)){
                    kindArray.add(1)
                }
            }else{
                if (kindArray.contains(1)){
                    kindArray.remove(1)
                }
            }
            if (mathMultiply.isChecked){
                if (!kindArray.contains(2)){
                    kindArray.add(2)
                }
            }else{
                if (kindArray.contains(2)){
                    kindArray.remove(2)
                }
            }
            if (mathDivide.isChecked){
                if (!kindArray.contains(3)){
                    kindArray.add(3)
                }
            }else{
                if (kindArray.contains(3)){
                    kindArray.remove(3)
                }
            }

            hideSettingPanel()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if ((countNumber.value ?:0) < 60){
                    toast("未达到及格分，无法退出,预留左上角返回可以退出")
                } else {
                    val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                    val alert = AlertDialog.Builder(context).setView(dialog).create()
                    alert.setCancelable(true)
                    dialog.sureTitle.text = "退出确认"
                    dialog.sureMessage.text = "确定要退出吗？"
                    dialog.sureBtn.setOnClickListener {
                        findNavController().popBackStack()
                    }
                    dialog.cancelBtn.setOnClickListener {
                        alert.dismiss()
                    }
                    alert.show()
                }
            }
        })

        submit.setOnClickListener {
            if (getCount.text.toString().toInt() >= 60){
                val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                val alert = AlertDialog.Builder(context).setView(dialog).create()
                dialog.sureTitle.text = "恭喜你"
                dialog.sureMessage.text = "你已经达到了及格分数，现在可以退出学习工具了。"
                dialog.sureBtn.setOnClickListener {
                    alert.dismiss()
                }
                dialog.cancelBtn.visibility = View.GONE
                alert.show()
            } else {
                toast("请认真做题，至少达到及格分才可以退出。")
            }
        }

    }

    private fun reCreateProblem(){
        countNumber.value = 0
        maxNumber = 99
        calculateContainer.removeAllViews()
        kindArray.clear()
        kindArray.add(0)
        kindArray.add(1)
        kindArray.add(2)
        kindArray.add(3)
        twoNumber.isChecked = true
        mathPlus.isChecked = true
        mathMinus.isChecked = true
        mathMultiply.isChecked = true
        mathDivide.isChecked = true
    }

    private fun createProblem(){
        if (kindArray.isNotEmpty()){
            val problem = LayoutInflater.from(context).inflate(R.layout.item_calculate_problem,calculateContainer,false)
            var checkAnswer = 0
            when(kindArray.random()){
                0 -> {
                    val firstRan = (0..maxNumber).random()
                    val secondRan = (0..maxNumber).random()
                    problem.firstNumber.text = "$firstRan"
                    problem.secondNumber.text = "$secondRan"
                    problem.operator.setBackgroundResource(R.drawable.ic_math_jia)
                    checkAnswer = firstRan + secondRan
                }
                1 -> {
                    val firstRan = (0.. 2*maxNumber).random()
                    val secondRan = (0..firstRan).random()
                    problem.firstNumber.text = "$firstRan"
                    problem.secondNumber.text = "$secondRan"
                    problem.operator.setBackgroundResource(R.drawable.ic_math_jian)
                    checkAnswer = firstRan - secondRan
                }
                2 -> {
                    val firstRan = (0..maxNumber).random()
                    val secondRan = (0..maxNumber).random()
                    problem.firstNumber.text = "$firstRan"
                    problem.secondNumber.text = "$secondRan"
                    problem.operator.setBackgroundResource(R.drawable.ic_math_cheng)
                    checkAnswer = firstRan * secondRan
                }
                3 -> {
                    val firstRan = (0..maxNumber).random()
                    val secondRan = (1..(if (firstRan == maxNumber) 1 else maxNumber-firstRan)).random()
                    problem.firstNumber.text = "${firstRan * secondRan}"
                    problem.secondNumber.text = "$secondRan"
                    problem.operator.setBackgroundResource(R.drawable.ic_math_chu)
                    checkAnswer = firstRan
                }
            }
            problem.approvalLayout.setOnClickListener {
                if (problem.answerInput.text.toString() == checkAnswer.toString()){
                    problem.approval.setBackgroundResource(R.drawable.ic_math_right)
                    countNumber.value = countNumber.value?.plus(1)
                    problem.approvalLayout.isClickable = false
                    problem.approvalLayout.isFocusable = false
                    problem.answerInput.isEnabled = false
                } else {
                    problem.approval.setBackgroundResource(R.drawable.ic_math_false_black)
                }
            }
            calculateContainer.addView(problem)
        } else {
            toast("请在设置中选择加、减、乘、除中的至少一项！")
        }

    }

    private fun showSettingPanel(){
        if (overlay.visibility == View.VISIBLE) return
        overlay.visibility = View.VISIBLE
        settingPanel.animate()
                .withStartAction { settingPanel.visibility = View.VISIBLE }
                .translationY(0f)
                .alpha(1f)
                .setDuration(resources.systemMediumAnimTime)
                .start()
    }

    private fun hideSettingPanel(){
        overlay.visibility = View.GONE
        settingPanel.animate()
                .translationY(resources.dp2px(100f).toFloat())
                .alpha(0f)
                .setDuration(resources.systemMediumAnimTime)
                .withStartAction { settingPanel.visibility = View.GONE }
                .start()
    }

}