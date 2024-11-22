package com.example.interestingdemo.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.interestingdemo.R
import com.example.interestingdemo.base.BaseActivity
import com.example.interestingdemo.extensions.initBackground
import com.example.interestingdemo.extensions.initColor
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.extensions.visible
import com.example.interestingdemo.util.SocketManagerUtil
import kotlinx.android.synthetic.main.activity_socket_client.*

class SocketClientActivity : BaseActivity() {
    //客户端是否是开启状态
    private var startData = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_client)

        initTitle("客户端")

        startData.observe(this){
            if (it){
                tvControl.text = "断开连接"
                tvControl.initColor(R.color.red_500).initBackground(R.drawable.bg_round_rectangle10_stroke_red_500)
                tvIM.visible()

                SocketManagerUtil.startClientSocket(etHost.text.toString(), etPort.text.toString().toInt())
            } else {
                tvControl.text = "连接服务器"
                tvControl.initColor(R.color.blue_600).initBackground(R.drawable.bg_round_rectangle_stroke_blue)
                tvIM.visible(false)

                SocketManagerUtil.stopClientSocket()
            }
        }


        tvControl.setOnClickListener {
            if (etHost.text.toString().isEmpty() ) return@setOnClickListener toast("请输入服务器地址")
            if (etPort.text.toString().isEmpty() ) return@setOnClickListener toast("请输入端口号")
            val lastData = startData.value ?: false
            startData.postValue((!lastData))
        }
        tvIM.setOnClickListener {
            startActivity(Intent(this, IMActivity::class.java)
                .putExtra("imType", "客户端")
            )
        }
    }

    override fun onResume() {
        super.onResume()
        startData.postValue(SocketManagerUtil.isClientStart())
    }


}