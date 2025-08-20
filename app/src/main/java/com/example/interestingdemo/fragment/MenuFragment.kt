package com.example.interestingdemo.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.interestingdemo.R
import com.example.interestingdemo.activity.GetLocationActivity
import com.example.interestingdemo.activity.SmsManagerActivity
import com.example.interestingdemo.activity.SocketClientActivity
import com.example.interestingdemo.activity.SocketServerActivity
import com.example.interestingdemo.activity.TouchSpeedActivity
import com.example.interestingdemo.util.SocketManagerUtil
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : Fragment() {

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

        rankRecord.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_dragRecyclerView)
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

        setting.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_settingFragment)
        }

        rewardPunishment.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_rewardPunishment)
        }

        specialEffects.setOnClickListener {
            view.findNavController().navigate(R.id.action_MenuFragment_to_someSpecialEffects)
        }

        smsManage.setOnClickListener {
            startActivity(Intent(requireContext(), SmsManagerActivity::class.java))
        }

        getSpeed.setOnClickListener {
            startActivity(Intent(requireContext(), TouchSpeedActivity::class.java))
        }

        locationGet.setOnClickListener{
            startActivity(Intent(requireContext(), GetLocationActivity::class.java))
        }

        socketServer.setOnClickListener{
            startActivity(Intent(requireContext(), SocketServerActivity::class.java))
        }

        socketClient.setOnClickListener{
            startActivity(Intent(requireContext(), SocketClientActivity::class.java))
        }

        stopSocketServer.setOnClickListener{
            SocketManagerUtil.stopServerSocket()
            SocketManagerUtil.stopClientSocket()
        }

    }

}