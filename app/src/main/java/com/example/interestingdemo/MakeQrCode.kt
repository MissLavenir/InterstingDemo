package com.example.interestingdemo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.fragment_make_qr_code.*


class MakeQrCode : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textToQrCodeInputLayout.requestFocus()
        startQrCode.setOnClickListener {
            if (textToQrCodeInput.text.toString().isNotBlank()){
                val logo = BitmapFactory.decodeResource(resources,R.drawable.ic_launcher)
                val string = "\n${textToQrCodeInput.text.toString()}\n(二维码来源：${resources.getString(R.string.app_name)})\n作者:MissLavenir"
                val bitmap = OpenFun().createQRImage(string,1000,1000, logoBitmap = logo)
                qrCode.setImageBitmap(bitmap)
            }else{
                toast("输入内容不能为空！")
            }
        }
    }
}