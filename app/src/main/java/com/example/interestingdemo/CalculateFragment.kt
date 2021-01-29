package com.example.interestingdemo

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
import com.example.interestingdemo.extensions.finish
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_calculate.*
import kotlinx.android.synthetic.main.item_calculate_problem.view.*


class CalculateFragment : Fragment() {
    private val countNumber = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countNumber.value = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countNumber.observe(viewLifecycleOwner, Observer {
            getCount.text = countNumber.value.toString()
            if (getCount.text.toString().toInt() >= 60){
                getCount.setTextColor(ResourcesCompat.getColor(resources,R.color.red_300,context?.theme))
            } else {
                getCount.setTextColor(ResourcesCompat.getColor(resources,R.color.grey_800,context?.theme))
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if ((countNumber.value ?:0) < 60){
                    toast("未达到及格分，无法退出")
                } else {
                    findNavController().popBackStack()
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
                    finish()
                    alert.dismiss()
                }
                dialog.cancelBtn.visibility = View.GONE
                alert.show()
            }
        }

    }

    private fun createProblem(){
        val problem = LayoutInflater.from(context).inflate(R.layout.item_calculate_problem,null,false)
        var checkAnswer = 0
        when((0..3).random()){
            0 -> {
                val firstRan = (0..999).random()
                val secondRan = (0..999).random()
                problem.firstNumber.text = "$firstRan"
                problem.secondNumber.text = "$secondRan"
                problem.operator.setBackgroundResource(R.drawable.ic_math_jia)
                checkAnswer = firstRan + secondRan
            }
            1 -> {
                val firstRan = (0..999).random()
                val secondRan = (0..firstRan).random()
                problem.firstNumber.text = "$firstRan"
                problem.secondNumber.text = "$secondRan"
                problem.operator.setBackgroundResource(R.drawable.ic_math_jian)
                checkAnswer = firstRan - secondRan
            }
            2 -> {
                val firstRan = (0..99).random()
                val secondRan = (0..(100-firstRan)).random()
                problem.firstNumber.text = "$firstRan"
                problem.secondNumber.text = "$secondRan"
                problem.operator.setBackgroundResource(R.drawable.ic_math_cheng)
                checkAnswer = firstRan * secondRan
            }
            3 -> {
                val firstRan = (0..99).random()
                val secondRan = (1..(100-firstRan)).random()
                problem.firstNumber.text = "${firstRan * secondRan}"
                problem.secondNumber.text = "$firstRan"
                problem.operator.setBackgroundResource(R.drawable.ic_math_chu)
                checkAnswer = if (firstRan == 0) 0 else secondRan
            }
        }
        problem.approval.setOnClickListener {
            if (problem.answerInput.text.toString() == checkAnswer.toString()){
                problem.approval.setBackgroundResource(R.drawable.ic_math_right)
                countNumber.value = countNumber.value?.plus(1)
                problem.approval.isClickable = false
                problem.approval.isFocusable = false
            } else {
                problem.approval.setBackgroundResource(R.drawable.ic_math_false_black)
            }
        }
        calculateContainer.addView(problem)
    }

}