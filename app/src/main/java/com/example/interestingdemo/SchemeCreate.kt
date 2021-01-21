package com.example.interestingdemo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.database.scheme.Scheme
import com.example.interestingdemo.database.scheme.SchemeGroup
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.function.SchemeFun
import kotlinx.android.synthetic.main.dialog_add_scheme_group.view.*
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_scheme_create.*

class SchemeCreate : Fragment() {
    private val schemeList = ArrayList<Scheme>()
    private val adapter = MyAdapter(schemeList)

    /**
     * 当前的分组
     */
    private var currentGroup : SchemeGroup ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheme_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        schemeRecyclerView.adapter = adapter
        schemeRecyclerView.layoutManager = layoutManager

        recyclerTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        recyclerTitle.isFocusable = true
        recyclerTitle.isSingleLine = true
        recyclerTitle.isSelected = true
        recyclerTitle.setHorizontallyScrolling(true)
        recyclerTitle.marqueeRepeatLimit = -1

        schemeGroupName.text = "未选择"
        selectSchemeGroup.setOnClickListener {
            selectGroup()
        }

        groupList.setOnClickListener {
            view.findNavController().navigate(R.id.action_rewardPunishment_to_schemeGroupList)
        }

        changeTitle.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_scheme_group, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.inputSchemeGroupName.requestFocus()
            dialog.inputSchemeGroupName.setText(currentGroup?.schemeGroupName)
            dialog.inputDescription.setText(currentGroup?.description)
            dialog.ok_add_scheme_btn.setOnClickListener {
                if (dialog.inputSchemeGroupName.text.isNullOrBlank()){
                    dialog.inputLayout.apply {
                        isErrorEnabled = true
                        error = "分组名称不能为空!"
                        dialog.inputSchemeGroupName.requestFocus()
                    }
                }else{
                    dialog.inputLayout.isErrorEnabled = false
                    currentGroup?.schemeGroupName = dialog.inputSchemeGroupName.text.toString()
                    currentGroup?.description = dialog.inputDescription.text.toString()
                    currentGroup?.let {
                        SchemeFun(requireContext()).updateGroup(it)
                        refresh()
                    }
                    alert.dismiss()
                }
            }
            dialog.cancel_add_scheme_btn.setOnClickListener {
                    alert.dismiss()
            }
            alert.show()
        }


        if (currentGroup == null){
            addScheme.visibility = View.GONE
            bankerClick.visibility = View.GONE
            playerClick.visibility = View.GONE
            groupPanel.visibility = View.GONE
        }

        bankerClick.setOnClickListener {
            btnClick(1)
        }
        playerClick.setOnClickListener {
            btnClick(2)
        }
        addSchemeGroup.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_scheme_group, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.inputSchemeGroupName.requestFocus()
            dialog.ok_add_scheme_btn.setOnClickListener {
                if (dialog.inputSchemeGroupName.text.isNullOrBlank()){
                    dialog.inputLayout.apply {
                        isErrorEnabled = true
                        error = "分组名称不能为空!"
                        dialog.inputSchemeGroupName.requestFocus()
                    }
                }else{
                    dialog.inputLayout.isErrorEnabled = false
                    val schemeGroup = SchemeGroup(schemeGroupName = dialog.inputSchemeGroupName.text.toString(), description = dialog.inputDescription.text.toString(), common = 0)
                    SchemeFun(requireContext()).insertGroup(schemeGroup)
                    alert.dismiss()
                }
            }
            dialog.cancel_add_scheme_btn.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }
        addScheme.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_scheme_group, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.title.text = "在“${schemeGroupName.text}”中添加方案"
            dialog.inputSchemeGroupName.requestFocus()
            dialog.inputLayout.hint = "请输入方案名称"
            dialog.inputDescription.hint = "请输入方案内容："
            dialog.schemeType.visibility = View.VISIBLE
            dialog.ok_add_scheme_btn.setOnClickListener {
                if (dialog.inputSchemeGroupName.text.isNullOrBlank()){
                    dialog.inputLayout.apply {
                        isErrorEnabled = true
                        error = "方案名称不能为空!"
                        dialog.inputSchemeGroupName.requestFocus()
                    }
                }else{
                    dialog.inputLayout.isErrorEnabled = false
                    if (!dialog.bankerPlay.isChecked && !dialog.playerPlay.isChecked){
                        val dialog2 = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                        val alert2 = AlertDialog.Builder(context).setView(dialog2).create()
                        dialog2.sureTitle.text = "添加失败"
                        dialog2.sureMessage.text = "庄家、玩家至少选择一种类型。"
                        dialog2.sureBtn.setOnClickListener {
                            alert2.dismiss()
                        }
                        dialog2.cancelBtn.visibility = View.GONE
                        alert2.show()
                    }else{
                        val scheme = Scheme(
                                groupId = currentGroup?.id ?: 0,
                                title = dialog.inputSchemeGroupName.text.toString(),
                                content = dialog.inputDescription.text.toString(),
                                type = if (dialog.bankerPlay.isChecked) 1 else 2
                        )
                        SchemeFun(requireContext()).insertScheme(scheme)
                        refresh()
                        alert.dismiss()
                    }
                }
            }
            dialog.cancel_add_scheme_btn.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }
    }

    private fun btnClick(type : Int){
        val list = schemeList.filter { it.type == type }
        if (list.isEmpty()) {
            toast("暂无${if (type == 1) "庄家" else "玩家"}方案，请在下方添加方案后再点击。")
            return
        }
        val rand = (0 until (list.size)).random()
        schemeTitle.text = list[rand].title
        schemeContent.text = list[rand].content
    }

    private fun selectGroup(){
        val groupList = SchemeFun(requireContext()).getAllSchemeGroup()
        if(groupList?.size == 0) {
            toast("没有方案分组，请先添加。")
            return
        }else{
            AlertDialog.Builder(requireContext()).apply {
                setTitle("选择方案")
                setNegativeButton("取消"){ btn, _ ->
                    btn.dismiss()
                }
                val schemeItem = ArrayAdapter<SchemeGroup>(requireContext(),android.R.layout.select_dialog_item)
                groupList?.forEach {
                    schemeItem.add(it)
                }
                setAdapter(schemeItem){ btn, which ->
                    currentGroup = schemeItem.getItem(which)
                    refresh()
                    addScheme.visibility = View.VISIBLE
                    btn.dismiss()
                }
            }.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refresh(){
        schemeList.clear()
        SchemeFun(requireContext()).getSchemeListByGroupId(currentGroup?.id ?: 0)?.forEach {
            schemeList.add(it)
        }
        schemeGroupName.text = currentGroup?.schemeGroupName
        recyclerTitle.text = "分组名称:${currentGroup?.schemeGroupName}"
        if (currentGroup == null){
            bankerClick.visibility = View.GONE
            playerClick.visibility = View.GONE
            groupPanel.visibility = View.GONE
        }else{
            bankerClick.visibility = View.VISIBLE
            playerClick.visibility = View.VISIBLE
            groupPanel.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    inner class MyAdapter(list : ArrayList<Scheme>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
        private val array = list
        inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            val type: AppCompatTextView = itemView.findViewById(R.id.type)
            val title: AppCompatTextView = itemView.findViewById(R.id.title)
            val content: AppCompatTextView = itemView.findViewById(R.id.content)
            val change: AppCompatImageView = itemView.findViewById(R.id.change)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_reward_punishment,parent,false))
        }

        override fun getItemCount(): Int = array.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.type.text = if (array[position].type == 1) "主" else "次"
            holder.title.text = array[position].title
            holder.content.text = array[position].content
            holder.change.setOnClickListener {
                val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_scheme_group, null, false)
                val alert = AlertDialog.Builder(context).setView(dialog).create()
                dialog.title.text = "修改方案"
                dialog.inputLayout.hint = "请输入方案名称"
                dialog.inputSchemeGroupName.requestFocus()
                dialog.inputSchemeGroupName.setText(array[position].title)
                dialog.inputDescription.hint = "请输入方案内容："
                dialog.inputDescription.setText(array[position].content)
                dialog.schemeType.visibility = View.VISIBLE
                if (array[position].type == 1) dialog.bankerPlay.isChecked = true else dialog.playerPlay.isChecked = true
                dialog.ok_add_scheme_btn.setOnClickListener {
                    if (dialog.inputSchemeGroupName.text.isNullOrBlank()){
                        dialog.inputLayout.apply {
                            isErrorEnabled = true
                            error = "方案名称不能为空!"
                            dialog.inputSchemeGroupName.requestFocus()
                        }
                    }else{
                        dialog.inputLayout.isErrorEnabled = false
                        if (!dialog.bankerPlay.isChecked && !dialog.playerPlay.isChecked){
                            toast("庄家、玩家至少选择一项")
                        }else{
                            val scheme = Scheme(
                                    id = array[position].id,
                                    groupId = array[position].groupId,
                                    title = dialog.inputSchemeGroupName.text.toString(),
                                    content = dialog.inputDescription.text.toString(),
                                    type = if (dialog.bankerPlay.isChecked) 1 else 2
                            )
                            SchemeFun(requireContext()).updateScheme(scheme)
                            alert.dismiss()
                        }
                    }
                }
                dialog.cancel_add_scheme_btn.setOnClickListener {
                    alert.dismiss()
                }
                alert.show()
            }
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            if (payloads.isNullOrEmpty()){
                onBindViewHolder(holder, position)
            }else{
                when{
                    payloads.contains(1) -> {
                        holder.type.text = if (array[position].type == 1) "主" else "次"
                    }
                    payloads.contains(2) -> {
                        holder.title.text = array[position].title
                    }
                    payloads.contains(3) -> {
                        holder.content.text = array[position].content
                    }
                }
            }
        }
    }
}