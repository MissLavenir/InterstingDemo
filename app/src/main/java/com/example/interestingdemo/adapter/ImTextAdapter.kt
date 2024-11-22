package com.example.interestingdemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.R
import com.example.interestingdemo.model.socket.BaseSocketModel

class ImTextAdapter : RecyclerView.Adapter<ImTextAdapter.MyViewHolder>() {

    private val dataList = arrayListOf<BaseSocketModel>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)
        val tvContent: AppCompatTextView = itemView.findViewById(R.id.tvContent)
        val clItem: ConstraintLayout = itemView.findViewById(R.id.clItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_im_text_type,parent,false))
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]

        val conSet = ConstraintSet()
        conSet.clone(holder.clItem)
        val bias = if (item.clAddress == "本机") 1f else 0f
        conSet.setHorizontalBias(holder.tvName.id,bias)
        conSet.setHorizontalBias(holder.tvContent.id,bias)
        conSet.applyTo(holder.clItem)

        holder.tvName.text = item.clAddress
        holder.tvContent.text = item.data.toString()
    }

    fun setData(data: List<BaseSocketModel>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: BaseSocketModel){
        dataList.add(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<BaseSocketModel>){
        val oldPosition = dataList.size - 1
        dataList.addAll(data)
        notifyDataSetChanged()
    }

}