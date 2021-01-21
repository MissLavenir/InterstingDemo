package com.example.interestingdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.interestingdemo.extensions.makeTextClick
import com.example.interestingdemo.extensions.toast
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
                    toast("服务协议被点击了，进入服务协议。")
                }
            }),
            Pair("《隐私政策》", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    toast("隐私政策被点击了，进入隐私政策。")
                }
            }),
            Pair("别文字的", object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    toast("仅仅代表可以从任意地方设置点击。")
                }
            })
        )
    }



}