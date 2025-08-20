package com.example.interestingdemo.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interestingdemo.R
import com.example.interestingdemo.adapter.ClientListAdapter
import com.example.interestingdemo.base.BaseActivity
import com.example.interestingdemo.extensions.initBackground
import com.example.interestingdemo.extensions.initColor
import com.example.interestingdemo.extensions.toast
import com.example.interestingdemo.extensions.visible
import com.example.interestingdemo.util.NetworkUtil
import com.example.interestingdemo.util.SocketManagerUtil
import kotlinx.android.synthetic.main.activity_socket_service.*

class SocketServerActivity : BaseActivity() {
    //服务器是否是开启状态
    private var startData = MutableLiveData<Boolean>()
    private val adapter by lazy {
        ClientListAdapter{
            SocketManagerUtil.stopClientSocket(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_service)

        initTitle("服务器端")
        val host = NetworkUtil.getLocalIpAddress()
        tvIp.text = "地址: ${host?.hostAddress ?: ""}\n\n蜂窝网环境则进入下方网站\nhttps://test-ipv6.com/ \n 复制其中的IPv6公网地址供客户端使用"


        startData.observe(this){
            if (it){
                tvControl.text = "停止服务器"
                tvControl.initColor(R.color.red_500).initBackground(R.drawable.bg_round_rectangle10_stroke_red_500)
                tvIM.visible()

                SocketManagerUtil.startServerSocket(etPort.text.toString().toInt())
            } else {
                tvControl.text = "开启服务器"
                tvControl.initColor(R.color.blue_600).initBackground(R.drawable.bg_round_rectangle_stroke_blue)
                tvIM.visible(false)

                SocketManagerUtil.stopServerSocket()
            }
        }

        tvControl.setOnClickListener {
            if (etPort.text.toString().isEmpty() ) return@setOnClickListener toast("请输入端口号")
            val lastData = startData.value ?: false
            startData.postValue((!lastData))

        }
        tvIM.setOnClickListener {
            startActivity(Intent(this, IMActivity::class.java)
                .putExtra("imType", "服务器端")
            )
        }

        initRv()
        SocketManagerUtil.clAddressListener.observe(this){
            adapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        startData.postValue(SocketManagerUtil.isServerStart())
    }

    private fun initRv(){
        rvClient.layoutManager = LinearLayoutManager(this)
        rvClient.adapter = adapter
    }

}