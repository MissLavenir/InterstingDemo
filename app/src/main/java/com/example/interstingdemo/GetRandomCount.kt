package com.example.interstingdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.get_random_count.*


class GetRandomCount : Fragment() {
    private var randomArrayList = ArrayList<Int>()
    private val adapter = ListAdapter(randomArrayList)
    private val layoutManager = LinearLayoutManager(this.context,RecyclerView.HORIZONTAL,false)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.get_random_count, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startRandom.setOnClickListener {

        }
        changeScope.setOnClickListener {
            numberPanel.visibility = View.GONE
            inputText.visibility = View.VISIBLE
        }
        hidePanel.setOnClickListener {
            inputText.visibility = View.GONE
            numberPanel.visibility = View.VISIBLE
        }

        sureBtn.setOnClickListener {
            resetNum()
            if (minNumber.text.toString() != ""){
                minNumber.text = inputMinNum.text
            }
            if (maxNumber.text.toString() != ""){
                maxNumber.text = inputMaxNum.text
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager


        startRandom.setOnClickListener {
            clickNum.text = "${getTextInt(clickNum) + 1}"

            randomCount.text = "${(getTextInt(minNumber) until getTextInt(maxNumber)).random()}"
            totalNum.text = "${getTextInt(totalNum) + getTextInt(randomCount)}"
            averageNum.text = "${getTextInt(totalNum).toFloat()/getTextInt(clickNum).toFloat()}"
            thisPercent.text = "${getTextInt(randomCount).toFloat()/getTextInt(maxNumber) * 100}%"
            totalPercent.text = "${getTextInt(totalNum).toFloat()/(getTextInt(maxNumber)*getTextInt(clickNum)) * 100}%"

            randomArrayList.add(getTextInt(randomCount))
            if (randomArrayList.size >= 5) recyclerViewTip.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }

        reset.setOnClickListener {
            resetNum()
        }

    }

    @SuppressLint("SetTextI18n")
    fun resetNum(){
        clickNum.text = "0"
        minNumber.text = "1"
        maxNumber.text = "100"
        totalNum.text = "0"
        randomCount.text = "0"
        averageNum.text = "0"
        thisPercent.text = "0%"
        totalPercent.text = "0%"
        randomArrayList.clear()
        recyclerViewTip.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    private fun getTextInt(textView: TextView) : Int{
        return textView.text.trim().toString().toInt()

    }


}
