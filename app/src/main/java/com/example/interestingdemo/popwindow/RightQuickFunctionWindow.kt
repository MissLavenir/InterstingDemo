package com.example.interestingdemo.popwindow

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.R
import com.example.interestingdemo.extensions.dp2Px
import com.example.interestingdemo.popwindow.adapter.RightQuickSelectAdapter

class RightQuickFunctionWindow(private val context: Context,
                               private var itemList: ArrayList<String> = arrayListOf(),
                               private var listener: OnSelectListener?) : PopupWindow() {

    private lateinit var selectRecyclerView: RecyclerView
    private lateinit var sureBtn: TextView
    private lateinit var cancelBtn: TextView
    private lateinit var adapter : RightQuickSelectAdapter
    private var selectList = ArrayList<String>()

    interface OnSelectListener{
        fun onSelect(select: ArrayList<String>)
    }

    init {
        contentView = LayoutInflater.from(context).inflate(R.layout.right_quick_select_list,null)
        width = context.resources.dp2Px(300f)
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        animationStyle =R.style.pop_up_window_anim_right
        initView(contentView)
    }

    private fun initView(view: View){
        selectRecyclerView = view.findViewById(R.id.selectRecyclerView)
        sureBtn = view.findViewById(R.id.sureBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)

        adapter = RightQuickSelectAdapter(itemList){ str, status ->
            if (status){
                if (!selectList.contains(str)){
                    selectList.add(str)
                }
            } else {
                if (selectList.contains(str)){
                    selectList.remove(str)
                }
            }
        }
        selectRecyclerView.adapter = adapter
        selectRecyclerView.layoutManager = LinearLayoutManager(context)

        cancelBtn.setOnClickListener {
            dismiss()
        }
        sureBtn.setOnClickListener {
            listener?.onSelect(selectList)
        }


    }

}