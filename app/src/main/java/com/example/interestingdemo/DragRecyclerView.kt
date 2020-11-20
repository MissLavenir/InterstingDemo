package com.example.interestingdemo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.Model.SimpleModel
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.*
import kotlinx.android.synthetic.main.dialog_drag_detail.view.*
import kotlinx.android.synthetic.main.fragment_drag_recyclerview.*

class DragRecyclerView : Fragment() {
    private val thisModel = ArrayList<SimpleModel>()
    private var modelId = 0
    private val adapter = DragAdapter(thisModel)
    private lateinit var myWrapperAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>
    private val layoutManager = LinearLayoutManager(this.context,RecyclerView.VERTICAL,false)
    private val dragDropManager = RecyclerViewDragDropManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setHasStableIds(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drag_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sureBtn.setOnClickListener {
            if (inputThingsText.text.toString().trim().isBlank()){
                inputThingsLayout.apply {
                    isErrorEnabled = true
                    error = ""
                    requestFocus()
                    return@setOnClickListener
                }
            }else{
                inputThingsLayout.isErrorEnabled = false
            }
            val model = SimpleModel().apply {
                id = modelId++
                message = inputThingsText.text.toString()
            }
            thisModel.add(model)
            myWrapperAdapter.notifyDataSetChanged()
        }

        myWrapperAdapter = dragDropManager.createWrappedAdapter(adapter)

        dragRecyclerView.adapter = myWrapperAdapter
        dragRecyclerView.layoutManager = layoutManager
        dragRecyclerView.setHasFixedSize(false)
        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false
        dragRecyclerView.itemAnimator = animator
        dragDropManager.attachRecyclerView(dragRecyclerView)
    }


    inner class DragAdapter(private val array : ArrayList<SimpleModel>) : RecyclerView.Adapter<DragAdapter.MyViewHolder>(),DraggableItemAdapter<DragAdapter.MyViewHolder>{
        inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),DraggableItemViewHolder{
            val dragIcon: AppCompatImageView = itemView.findViewById(R.id.dragIcon)
            val dragTextView: AppCompatTextView = itemView.findViewById(R.id.dragTextView)
            private val mDragState = DraggableItemState()

            override fun getDragStateFlags(): Int = mDragState.flags

            override fun setDragStateFlags(flags: Int) {
                mDragState.flags = flags
            }

            override fun getDragState(): DraggableItemState = mDragState

        }

        override fun getItemId(position: Int): Long = (array[position].id ?: 0).toLong()

        override fun getItemCount(): Int = array.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.dragTextView.text = array[position].message
            holder.dragTextView.setOnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_drag_detail,null,false)
                val alert = AlertDialog.Builder(context).setView(dialogView).create()
                dialogView.dragText.text = holder.dragTextView.text
                dialogView.dragDeleteBtn.setOnClickListener {
                    array.removeAt(position)
                    notifyDataSetChanged()
                    alert.dismiss()
                }
                dialogView.dragCancelBtn.setOnClickListener {
                    alert.dismiss()
                }
                alert.show()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_drag,parent,false))
        }

        override fun onMoveItem(fromPosition: Int, toPosition: Int) {
            val model = array[fromPosition]
            array.removeAt(fromPosition)
            array.add(toPosition,model)
        }

        override fun onCheckCanStartDrag(holder: MyViewHolder, position: Int, x: Int, y: Int): Boolean {
            return hitTest(holder.dragIcon,x, y)
        }

        override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
            return true
        }

        override fun onGetItemDraggableRange(holder: MyViewHolder, position: Int): ItemDraggableRange? {
            return null
        }

        override fun onItemDragStarted(position: Int) {
            notifyDataSetChanged()
        }

        override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
            notifyDataSetChanged()
        }

        private fun hitTest(v: View, x: Int, y: Int): Boolean {
            val tx = (v.translationX + 0.5f).toInt()
            val ty = (v.translationY + 0.5f).toInt()
            val left = v.left + tx
            val right = v.right + tx
            val top = v.top + ty
            val bottom = v.bottom + ty
            return x in left..right && y >= top && y <= bottom
        }


    }
}