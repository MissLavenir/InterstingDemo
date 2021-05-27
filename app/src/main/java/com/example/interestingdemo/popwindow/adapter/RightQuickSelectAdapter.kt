package com.example.interestingdemo.popwindow.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.R

class RightQuickSelectAdapter(var itemList : ArrayList<String>,val onSelect: (String, Boolean) -> Unit): RecyclerView.Adapter<RightQuickSelectAdapter.SelectViewHolder>() {

    inner class SelectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val checkbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectViewHolder {
        return SelectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_select_view,parent,false))
    }

    override fun onBindViewHolder(holder: SelectViewHolder, position: Int) {
        val item = itemList[position]
        holder.checkbox.text = item
        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked){
                onSelect(item,true)
            } else {
                onSelect(item,false)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}