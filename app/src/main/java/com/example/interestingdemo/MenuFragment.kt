package com.example.interestingdemo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.example.interestingdemo.extensions.secureDisplay
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment() {
    var secureSwitch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRandom.setOnClickListener{
            view.findNavController().navigate(R.id.action_MenuFragment_to_GetRandomCount)
        }

        rankRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_dragRecyclerView)
        }

        getLuckCount.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_luckGenerator)
        }

        getTextToSpeak.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_textToSpeak)
        }

        getTextToQrCode.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_makeQrCode)
        }

        cropPicture.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_cropPicture)
        }

        getSecureStatus()

        secure.setOnClickListener {
            if (secureSwitch){
                secureSwitch = false
                activity?.secureDisplay(secureSwitch)
                getSecureStatus()
                toast("您已关闭安全模式！")
            }else{
                secureSwitch = true
                activity?.secureDisplay(secureSwitch)
                getSecureStatus()
                val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
                val alert = AlertDialog.Builder(context).setView(dialog).create()
                dialog.sureTitle.text = "安全模式"
                dialog.sureMessage.text = "您已开启系统安全模式，此模式下不可录屏、不可截图,概览窗口也将看不到内容（但此模式对root的手机可能无效）。"
                dialog.sureBtn.setOnClickListener {
                    alert.dismiss()
                }
                dialog.cancelBtn.visibility = View.GONE
                alert.show()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        getSecureStatus()
    }

    private fun getSecureStatus(){
        if (secureSwitch){
            secure.setTextColor(ResourcesCompat.getColor(resources, R.color.red_a200, context?.theme))
        }else{
            secure.setTextColor(ResourcesCompat.getColor(resources, R.color.grey_500, context?.theme))
        }
    }

}