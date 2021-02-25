package com.example.interestingdemo

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.interestingdemo.extensions.makeTextClick
import com.example.interestingdemo.extensions.setStatusBarColor
import com.example.interestingdemo.extensions.shareTextContent
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_some_special_effects.*


class SomeSpecialEffects : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_some_special_effects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        longTextView.makeTextClick(
            Pair("《服务协议》", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                    val alert = AlertDialog.Builder(context).setView(dialog).create()
                    dialog.sureTitle.text = "文字点击效果"
                    dialog.sureMessage.text = "服务协议被点击了，进入服务协议。"
                    dialog.cancelBtn.visibility = View.GONE
                    dialog.sureBtn.setOnClickListener {
                        alert.dismiss()
                    }
                    alert.show()
                }
            }),
            Pair("《隐私政策》", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                    val alert = AlertDialog.Builder(context).setView(dialog).create()
                    dialog.sureTitle.text = "文字点击效果"
                    dialog.sureMessage.text = "隐私政策被点击了，进入隐私政策。"
                    dialog.cancelBtn.visibility = View.GONE
                    dialog.sureBtn.setOnClickListener {
                        alert.dismiss()
                    }
                    alert.show()
                }
            }),
            Pair("别文字的", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    toast("仅仅代表可以从任意地方设置点击。")
                }
            }),
            Pair("分享", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    activity?.shareTextContent("分享这段文字内容")
                }
            }),
            Pair("设置状态栏颜色", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    activity?.setStatusBarColor(Color.parseColor("#ef5350"))
                }
            })
        )

        drag_and_expand_recyclerView.setOnClickListener {
            view.findNavController().navigate(R.id.action_someSpecialEffects_to_schemeGroupList2)
        }
    }



}