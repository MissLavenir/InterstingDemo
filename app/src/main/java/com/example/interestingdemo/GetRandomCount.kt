package com.example.interestingdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.extensions.round2Scale
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
        resetNum()

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
            if (inputMinNum.text.toString().isNotEmpty()){
                minNumber.text = inputMinNum.text
            }
            if (inputMaxNum.text.toString().isNotEmpty()){
                maxNumber.text = inputMaxNum.text
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager


        startRandom.setOnClickListener {
            clickNum.text = "${getTextInt(clickNum) + 1}"

            randomCount.text = "${(getTextInt(minNumber) until getTextInt(maxNumber)).random()}"
            totalNum.text = "${getTextInt(totalNum) + getTextInt(randomCount)}"
            averageNum.text = (getTextInt(totalNum).toDouble()/getTextInt(clickNum).toDouble()).round2Scale()
            thisPercent.text = "${(getTextInt(randomCount).toDouble()/getTextInt(maxNumber) * 100).round2Scale()}%"
            totalPercent.text = "${(getTextInt(totalNum).toDouble()/(getTextInt(maxNumber)*getTextInt(clickNum)) * 100).round2Scale()}%"

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
        recyclerViewTip.visibility = View.INVISIBLE
        adapter.notifyDataSetChanged()
    }

    private fun getTextInt(textView: TextView) : Int{
        return textView.text.trim().toString().toInt()

    }
    class ListAdapter(private val arrayList: ArrayList<Int>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val text: AppCompatTextView = itemView.findViewById(R.id.text)
            val count: AppCompatTextView = itemView.findViewById(R.id.count)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_random_count,parent,false))
        }

        override fun getItemCount(): Int = arrayList.size

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.text.text = arrayList[position].toString()
            holder.count.text = "第${position +1}次"
        }

    }

}
