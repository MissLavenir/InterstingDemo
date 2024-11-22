package com.example.interestingdemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.R
import com.example.interestingdemo.extensions.visible

class ClientListAdapter(private val callback: (String) -> Unit) : RecyclerView.Adapter<ClientListAdapter.MyViewHolder>() {

    private val dataList = arrayListOf<String>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)
        val tvClose: AppCompatTextView = itemView.findViewById(R.id.tvClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_im_client_address,parent,false))
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvName.text = item
        holder.tvClose.visible(false)// todo 功能完成时删除！！
        holder.tvClose.setOnClickListener {
            callback.invoke(item)
            dataList.remove(item)
            notifyDataSetChanged()
        }
    }

    fun setData(data: List<String>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: String){
        dataList.add(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<String>){
        val oldPosition = dataList.size - 1
        dataList.addAll(data)
        notifyDataSetChanged()
    }

}