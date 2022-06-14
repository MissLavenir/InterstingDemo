package com.example.interestingdemo

import android.media.RingtoneManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.interestingdemo.extensions.secureDisplay
import com.example.interestingdemo.function.ConfigurationFun
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ConfigurationFun(requireContext()).getConfig(SECURE_MODEL_SETTING)?.configStatus?.let {
            secureSwitch.isChecked = it != 0
        }

        secureSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                ConfigurationFun(requireContext()).addOrUpdate(SECURE_MODEL_SETTING,1)
                activity?.secureDisplay(true)
                //发出声音
                val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(requireContext(), notificationSound)
                ringtone.play()
            }else{
                ConfigurationFun(requireContext()).addOrUpdate(SECURE_MODEL_SETTING,0)
                activity?.secureDisplay(false)
            }
        }

        secureModel.setOnClickListener {
            secureSwitch.isChecked = !secureSwitch.isChecked
        }
    }

    companion object{
        /**
         * 安全模式，0为关闭，1为开启
         */
        const val SECURE_MODEL_SETTING = "secureModel"
    }

}