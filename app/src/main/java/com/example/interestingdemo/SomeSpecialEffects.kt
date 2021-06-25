package com.example.interestingdemo

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.example.interestingdemo.Util.DialogUtil
import com.example.interestingdemo.extensions.makeTextClick
import com.example.interestingdemo.extensions.setStatusBarColor
import com.example.interestingdemo.extensions.shareTextContent
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.popwindow.RightQuickFunctionWindow
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_some_special_effects.*
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList


class SomeSpecialEffects : Fragment() {

    private var isChangeColor = false

    private var selectWindow: RightQuickFunctionWindow? = null

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
                    isChangeColor = !isChangeColor
                    if (isChangeColor){
                        activity?.setStatusBarColor(ResourcesCompat.getColor(resources,R.color.blue_300,context?.theme))
                    } else {
                        activity?.setStatusBarColor(ResourcesCompat.getColor(resources,R.color.blue_800,context?.theme))
                    }
                }
            })
        )

        drag_and_expand_recyclerView.setOnClickListener {
            view.findNavController().navigate(R.id.action_someSpecialEffects_to_schemeGroupList2)
        }

        timeGet.setOnClickListener {
            DialogUtil.showDateYMDHMPicker(requireContext(),timeGet.text.toString()){
                timeGet.text = it
            }
        }

        rightSelect.setOnClickListener {
            if (selectWindow == null){
                val array = arrayListOf("膜拜大佬","感谢美工","别改需求了","这不是bug","上代码啊","上报错信息","上页面显示","你找别人吧我不会")
                selectWindow = RightQuickFunctionWindow(requireContext(), array,object: RightQuickFunctionWindow.OnSelectListener{
                    override fun onSelect(select: ArrayList<String>) {
                        toast("已选择$select")
                        selectWindow?.dismiss()
                    }
                })
            }
            shadow.visibility = View.VISIBLE
            selectWindow?.showAtLocation(activity?.window?.decorView,Gravity.END,0,0)
            selectWindow?.setOnDismissListener {
                shadow.visibility = View.GONE
            }
        }

        getMac.setOnClickListener {
            getMac.text = getMacAddress()
        }
    }

    private fun getMacAddress(): String {
        try {
            val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            Log.d("macAddressDebug","getMacAddress() throw a Exception")
        }
        return "02:00:00:00:00:00"
    }


}