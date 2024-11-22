package com.example.interestingdemo.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.R
import com.example.interestingdemo.database.scheme.Scheme
import com.example.interestingdemo.database.scheme.SchemeGroup
import com.example.interestingdemo.extensions.hitTest
import com.example.interestingdemo.extensions.initSchemeColors
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.function.SchemeFun
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.expandable.*
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import kotlinx.android.synthetic.main.dialog_add_scheme_group.view.*
import kotlinx.android.synthetic.main.dialog_scheme_manager_menu.view.*
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_scheme_group_list.*
import kotlinx.android.synthetic.main.viewholder_scheme_group_manger.view.*
import kotlinx.android.synthetic.main.viewholder_scheme_manger.view.*


class SchemeGroupListFragment : Fragment(){
    //能否跨分组移动
    private var mAllowItemsMoveAcrossSections = true
    private val adapter = ExpandableDraggableWithSectionAdapter()
    private lateinit var expandableItemManager : RecyclerViewExpandableItemManager
    private var dragDropManager = RecyclerViewDragDropManager()
    private lateinit var mWrappedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setHasStableIds(true)
        expandableItemManager = RecyclerViewExpandableItemManager(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheme_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.initSchemeColors()
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            refreshSchemeGroup()
        }

        addGroup.setOnClickListener {
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_scheme_group, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.ok_add_scheme_btn.setOnClickListener {
                if (dialog.inputSchemeGroupName.text.isNullOrBlank()){
                    dialog.inputLayout.apply {
                        isErrorEnabled = true
                        error = "分组名称不能为空"
                        dialog.inputSchemeGroupName.requestFocus()
                    }
                }else{
                    dialog.inputLayout.isErrorEnabled = false
                    val schemeGroup = SchemeGroup(schemeGroupName = dialog.inputSchemeGroupName.text.toString(), description = dialog.inputDescription.text.toString(), common = 0)
                    SchemeFun(requireContext()).insertGroup(schemeGroup)
                    refreshSchemeGroup()
                    alert.dismiss()
                }
            }
            dialog.cancel_add_scheme_btn.setOnClickListener {
                alert.dismiss()
            }
            alert.setCancelable(false)
            alert.show()
        }

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        mWrappedAdapter = expandableItemManager.createWrappedAdapter(adapter)
        mWrappedAdapter = dragDropManager.createWrappedAdapter(mWrappedAdapter)

        dragDropManager.isCheckCanDropEnabled = mAllowItemsMoveAcrossSections
        adapter.setAllowItemsMoveAcrossSections(mAllowItemsMoveAcrossSections)

        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false

        schemeGroupRecyclerView.layoutManager = layoutManager
        schemeGroupRecyclerView.adapter = mWrappedAdapter
        schemeGroupRecyclerView.setHasFixedSize(false)
        schemeGroupRecyclerView.itemAnimator = animator

        dragDropManager.attachRecyclerView(schemeGroupRecyclerView)
        expandableItemManager.attachRecyclerView(schemeGroupRecyclerView)

        refreshSchemeGroup()

    }

    private fun refreshSchemeGroup(){
        adapter.groupData.clear()
        adapter.childData.clear()
        val allGroup = SchemeFun(requireContext()).getAllSchemeGroup()
        adapter.groupData.apply {
            addAll((allGroup as ArrayList<SchemeGroup>).sortedBy { it.common })
            adapter.childData.apply {
                allGroup.forEach { group ->
                    group.id?.let { groupId ->
                        val schemeList = SchemeFun(requireContext()).getSchemeListByGroupId(groupId)
                        val schemeArrayList = ArrayList<Scheme>()
                        schemeList?.sortedBy { it.rank }?.forEach {
                            schemeArrayList.add(it)
                        }
                        put(groupId,schemeArrayList)
                    }
                }
            }
        }
        swipeRefreshLayout.isRefreshing = false
        adapter.notifyDataSetChanged()
    }


    open inner class  MyBaseViewHolder(itemView : View) : AbstractDraggableItemViewHolder(itemView), ExpandableItemViewHolder{
        private val mExpandState = ExpandableItemState()
        private val mDragState = DraggableItemState()

        override fun getExpandState(): ExpandableItemState = mExpandState

        override fun getExpandStateFlags(): Int = mExpandState.flags

        override fun setExpandStateFlags(flags: Int) {
            mExpandState.flags = flags
        }

        override fun getDragState(): DraggableItemState = mDragState

        override fun getDragStateFlags(): Int = mDragState.flags

        override fun setDragStateFlags(flags: Int) {
            mDragState.flags = flags
        }

    }

    inner class GroupViewHolder(itemView: View) : MyBaseViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun applyData(data : SchemeGroup){
            if (expandState.isExpanded){
                itemView.downArrow.rotation = 90f
                itemView.downArrow.setColorFilter(ResourcesCompat.getColor(resources,
                    R.color.blue_200, context?.theme))
                itemView.groupText.setTextColor(ResourcesCompat.getColor(resources,
                    R.color.blue_500, context?.theme))
            }else{
                itemView.downArrow.rotation = 0f
                itemView.downArrow.setColorFilter(ResourcesCompat.getColor(resources,
                    R.color.grey_400, context?.theme))
                itemView.groupText.setTextColor(ResourcesCompat.getColor(resources,
                    R.color.grey_800, context?.theme))
            }
            itemView.groupText.text = data.schemeGroupName
            itemView.groupMenu.setOnClickListener {
                val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_scheme_manager_menu, null, false)
                val alert = AlertDialog.Builder(context).setView(dialog).create()
                dialog.changeGroupName.setText(data.schemeGroupName)
                dialog.changeGroupDescription.setText(data.description)

                dialog.close.setOnClickListener {
                    refreshSchemeGroup()
                    alert.dismiss()
                }
                dialog.changeTitle.setOnClickListener {
                    dialog.changeBody.visibility = View.VISIBLE
                    dialog.addBody.visibility = View.GONE
                    dialog.deleteBody.visibility = View.GONE
                }
                dialog.addTitle.setOnClickListener {
                    dialog.changeBody.visibility = View.GONE
                    dialog.addBody.visibility = View.VISIBLE
                    dialog.deleteBody.visibility = View.GONE
                }
                dialog.deleteTitle.setOnClickListener {
                    dialog.changeBody.visibility = View.GONE
                    dialog.addBody.visibility = View.GONE
                    dialog.deleteBody.visibility = View.VISIBLE
                }
                dialog.packUp1.setOnClickListener {
                    dialog.changeBody.visibility = View.GONE
                }
                dialog.packUp2.setOnClickListener {
                    dialog.addBody.visibility= View.GONE
                }
                dialog.packUp3.setOnClickListener {
                    dialog.deleteBody.visibility = View.GONE
                }
                dialog.changeBtn.setOnClickListener {
                    val schemeGroup = SchemeGroup(
                            id = data.id,
                            schemeGroupName = dialog.changeGroupName.text.toString(),
                            description = dialog.changeGroupDescription.text.toString(),
                            common = data.common
                    )
                    SchemeFun(requireContext()).updateGroup(schemeGroup)
                    toast("修改成功")
                    dialog.close.callOnClick()
                }
                dialog.addBtn.setOnClickListener {
                    val scheme = Scheme(
                            groupId = data.id ?: 0,
                            title = dialog.addSchemeName.text.toString(),
                            content = dialog.addSchemeContent.text.toString(),
                            type = if (dialog.banker.isChecked) 1 else 2
                    )
                    SchemeFun(requireContext()).insertScheme(scheme)
                    toast("添加成功")
                    dialog.packUp2.callOnClick()
                }
                dialog.deleteBtn.setOnClickListener {
                    val sureDialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                    val sureAlert = AlertDialog.Builder(context).setView(sureDialog).create()
                    sureDialog.sureTitle.text = "防误删确认"
                    sureDialog.sureMessage.text = "您正在删除名为“${data.schemeGroupName}”的方案分组，删除方案分组后，该分组下的所有方案也会被删除，请确认。"
                    sureDialog.sureBtn.setOnClickListener {
                        SchemeFun(requireContext()).deleteSchemeGroup(data.id ?: 0)
                        sureAlert.dismiss()
                        dialog.close.callOnClick()
                    }
                    sureDialog.cancelBtn.setOnClickListener {
                        sureAlert.dismiss()
                    }
                    sureAlert.show()
                }
                alert.show()
            }

            itemView.container.setOnClickListener {
                schemeGroupRecyclerView.adapter?.let { itAdapter ->
                    val groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(
                        expandableItemManager.getExpandablePosition(
                            WrapperAdapterUtils.unwrapPosition(
                                itAdapter,
                                adapter,
                                adapterPosition
                            )
                        )
                    )
                    if (expandableItemManager.isGroupExpanded(groupPosition)){
                        expandableItemManager.collapseGroup(groupPosition)
                    } else {
                        expandableItemManager.expandGroup(groupPosition)
                    }
                }
            }
        }
    }

    inner class ChildViewHolder(itemView: View) : MyBaseViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun applyData(data : Scheme){
            itemView.childText.text = data.title
            itemView.childContent.text = data.content
            itemView.childType.text = if (data.type == 1) "主" else "次"
            itemView.childMenu.setOnClickListener {
                val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_scheme_manager_menu, null, false)
                val alert = AlertDialog.Builder(context).setView(dialog).create()
                dialog.changeTitle.visibility = View.GONE
                dialog.addTitleText.text = "修改方案"
                dialog.deleteText.text = "删除方案"
                dialog.addSchemeName.setText(data.title)
                dialog.addSchemeContent.setText(data.content)
                if (data.type == 1) dialog.banker.isChecked = true else dialog.player.isChecked = true

                dialog.close.setOnClickListener {
                    refreshSchemeGroup()
                    alert.dismiss()
                }
                dialog.addTitle.setOnClickListener {
                    dialog.addBody.visibility = View.VISIBLE
                    dialog.deleteBody.visibility = View.GONE
                }
                dialog.deleteTitle.setOnClickListener {
                    dialog.addBody.visibility = View.GONE
                    dialog.deleteBody.visibility = View.VISIBLE
                }
                dialog.packUp2.setOnClickListener {
                    dialog.addBody.visibility= View.GONE
                }
                dialog.packUp3.setOnClickListener {
                    dialog.deleteBody.visibility = View.GONE
                }
                dialog.addBtn.setOnClickListener {
                    val scheme = Scheme(
                            id = data.id,
                            groupId = data.groupId,
                            title = dialog.addSchemeName.text.toString(),
                            content = dialog.addSchemeContent.text.toString(),
                            type = if (dialog.banker.isChecked) 1 else 2
                    )
                    SchemeFun(requireContext()).updateScheme(scheme)
                    dialog.close.callOnClick()
                }
                dialog.deleteBtn.setOnClickListener {
                    val sureDialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                    val sureAlert = AlertDialog.Builder(context).setView(sureDialog).create()
                    sureDialog.sureTitle.text = "防误删确认"
                    sureDialog.sureMessage.text = "您正在删除名为“${data.title}”的方案，请确认。"
                    sureDialog.sureBtn.setOnClickListener {
                        SchemeFun(requireContext()).deleteSchemeById(data.id ?: 0)
                        sureAlert.dismiss()
                        dialog.close.callOnClick()
                    }
                    sureDialog.cancelBtn.setOnClickListener {
                        sureAlert.dismiss()
                    }
                    sureAlert.show()
                }

                alert.show()
            }
        }
    }

    inner class ExpandableDraggableWithSectionAdapter : AbstractExpandableItemAdapter<GroupViewHolder, ChildViewHolder>(),
            ExpandableDraggableItemAdapter<GroupViewHolder, ChildViewHolder>{
        val groupData = ArrayList<SchemeGroup>()
        val childData = SparseArray<ArrayList<Scheme>>()

        override fun getGroupCount(): Int = groupData.size

        override fun getChildCount(groupPosition: Int): Int = childData[groupData[groupPosition].id ?: 0]?.size ?: 0

        override fun getGroupId(groupPosition: Int): Long = groupData[groupPosition].id?.toLong() ?: 0L

        override fun getChildId(groupPosition: Int, childPosition: Int): Long = childData[groupData[groupPosition].id ?: 0][childPosition].id?.toLong() ?: 0L

        override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder {
            return GroupViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.viewholder_scheme_group_manger, parent, false))
        }

        override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {
            return ChildViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.viewholder_scheme_manger, parent, false))
        }

        override fun onBindGroupViewHolder(holder: GroupViewHolder, groupPosition: Int, viewType: Int) {
            holder.applyData(groupData[groupPosition])
        }

        override fun onBindChildViewHolder(holder: ChildViewHolder, groupPosition: Int, childPosition: Int, viewType: Int) {
            holder.applyData(childData[groupData[groupPosition].id ?: 0][childPosition])
        }

        override fun onCheckCanExpandOrCollapseGroup(holder: GroupViewHolder, groupPosition: Int, x: Int, y: Int, expand: Boolean): Boolean {
            return holder.itemView.groupDrag.hitTest(x, y)
        }

        override fun onCheckGroupCanStartDrag(holder: GroupViewHolder, groupPosition: Int, x: Int, y: Int): Boolean {
            return holder.itemView.groupDrag.hitTest(x, y)
        }

        override fun onCheckChildCanStartDrag(holder: ChildViewHolder, groupPosition: Int, childPosition: Int, x: Int, y: Int): Boolean {
            return holder.itemView.childDrag.hitTest(x, y)
        }

        override fun onGetGroupItemDraggableRange(holder: GroupViewHolder, groupPosition: Int): ItemDraggableRange? {
            return if (mAllowItemsMoveAcrossSections) {
                null
            } else {
                val start = findFirstSectionItem(groupPosition)
                val end = findLastSectionItem(groupPosition)
                GroupPositionItemDraggableRange(start, end)
            }
        }

        override fun onGetChildItemDraggableRange(holder: ChildViewHolder, groupPosition: Int, childPosition: Int): ItemDraggableRange? {
            return if (mAllowItemsMoveAcrossSections){
                null
            } else {
                GroupPositionItemDraggableRange(groupPosition,groupPosition)
            }
        }

        override fun onCheckGroupCanDrop(draggingGroupPosition: Int, dropGroupPosition: Int): Boolean {
            return true
        }

        override fun onCheckChildCanDrop(draggingGroupPosition: Int, draggingChildPosition: Int, dropGroupPosition: Int, dropChildPosition: Int): Boolean {
            return true
        }

        //分组移动
        override fun onMoveGroupItem(fromGroupPosition: Int, toGroupPosition: Int) {
            val model = groupData[fromGroupPosition]
            groupData.removeAt(fromGroupPosition)
            groupData.add(toGroupPosition, model)

            var common = 1
            groupData.forEach {
                SchemeFun(requireContext()).updateGroupCommon(it.id ?: 0, common++)
            }
        }

        //子项移动
        override fun onMoveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int) {
            val model = childData[groupData[fromGroupPosition].id ?: 0][fromChildPosition]
            model.groupId = groupData[toGroupPosition].id ?: 0
            childData[groupData[fromGroupPosition].id ?: 0].removeAt(fromChildPosition)
            childData[groupData[toGroupPosition].id ?: 0].add(toChildPosition, model)
            //toast("起始组$fromGroupPosition \n起始位置$fromChildPosition \n 目的组$toGroupPosition \n目的位置$toChildPosition")
            var rank = 1
            childData[groupData[toGroupPosition].id ?: 0].forEach {
                SchemeFun(requireContext()).updateSchemeRank(it.id ?: 0, it.groupId, rank++)
            }
        }

        override fun onGroupDragStarted(groupPosition: Int) {
            notifyDataSetChanged()
        }

        override fun onGroupDragFinished(fromGroupPosition: Int, toGroupPosition: Int, result: Boolean) {
            notifyDataSetChanged()
        }

        override fun onChildDragStarted(groupPosition: Int, childPosition: Int) {
            notifyDataSetChanged()
        }

        override fun onChildDragFinished(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int, result: Boolean) {
            notifyDataSetChanged()
        }

        private fun findFirstSectionItem(position: Int) : Int {
            var result = position
            while (position > 0){
                result = position -1
            }
            return result
        }

        private fun findLastSectionItem(position: Int) : Int {
            var result = 0
            val lastIndex = groupCount -1
            while (position < lastIndex){
                result = position + 1
            }
            return result
        }

        fun setAllowItemsMoveAcrossSections(allowed : Boolean){
            mAllowItemsMoveAcrossSections = allowed
        }

    }

}