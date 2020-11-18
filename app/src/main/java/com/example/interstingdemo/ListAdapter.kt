package com.example.interstingdemo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

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