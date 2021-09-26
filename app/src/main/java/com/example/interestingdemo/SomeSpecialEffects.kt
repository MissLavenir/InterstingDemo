package com.example.interestingdemo

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.interestingdemo.Util.DialogUtil
import com.example.interestingdemo.extensions.*
import com.example.interestingdemo.popwindow.RightQuickFunctionWindow
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_some_special_effects.*
import java.net.NetworkInterface
import java.util.*


class SomeSpecialEffects : Fragment() {

    private var isChangeColor = false

    private var selectWindow: RightQuickFunctionWindow? = null

    private var needText = ""

    //一天倒计时
    private var hourCountDown = 0L
    private var minuteCountDown = 0L
    private var secondCountDown = 0L
    private var timer: CountDownTimer? = null

    //返回刷新
    private var resumeRefresh = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_some_special_effects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshTime()
        timer?.start()

        secondCountdownTV.setOnClickListener { timer?.cancel() }
        hourCountdownTV.setOnClickListener { timer?.start() }

        longTextView.makeTextClick(
                Pair("《服务协议》", object : View.OnClickListener {
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
                Pair("《隐私政策》", object : View.OnClickListener {
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
                Pair("别文字的", object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        toast("仅仅代表可以从任意地方设置点击。")
                    }
                }),
                Pair("拨打电话", object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        DialogUtil.showCallPhoneDialog(requireContext(),"18796852407")
                    }
                }),
                Pair("分享", object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        activity?.shareTextContent("分享这段文字内容")
                    }
                }),
                Pair("设置状态栏颜色", object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        isChangeColor = !isChangeColor
                        if (isChangeColor) {
                            activity?.setStatusBarColor(ResourcesCompat.getColor(resources, R.color.blue_300, context?.theme))
                        } else {
                            activity?.setStatusBarColor(ResourcesCompat.getColor(resources, R.color.blue_800, context?.theme))
                        }
                    }
                }),
            Pair("查看自定义View", object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    activity?.startActivity(Intent(requireContext(),CustomViewActivity::class.java))
                }
            })
        )

        drag_and_expand_recyclerView.setOnClickListener {
            view.findNavController().navigate(R.id.action_someSpecialEffects_to_schemeGroupList2)
        }

        timeGet.setOnClickListener {
            DialogUtil.showDateYMDHMPicker(requireContext(), timeGet.text.toString()){
                timeGet.text = it
            }
        }

        rightSelect.setOnClickListener {
            if (selectWindow == null){
                val array = arrayListOf("膜拜大佬", "感谢美工", "别改需求了", "这不是bug", "上代码啊", "上报错信息", "上页面显示", "你找别人吧我不会")
                selectWindow = RightQuickFunctionWindow(requireContext(), array, object : RightQuickFunctionWindow.OnSelectListener {
                    override fun onSelect(select: ArrayList<String>) {
                        toast("已选择$select")
                        selectWindow?.dismiss()
                    }
                })
            }
            shadow.visibility = View.VISIBLE
            selectWindow?.showAtLocation(activity?.window?.decorView, Gravity.END, 0, 0)
            selectWindow?.setOnDismissListener {
                shadow.visibility = View.GONE
            }
        }

        getMac.setOnClickListener {
//            getMac.text = getMacAddress()
            getMacAddress()
            getAndroidId()
            getUUID()
            val dialog = LayoutInflater.from(requireContext()).inflate(R.layout.item_single_textview, null, false)
            val alertDialog = AlertDialog.Builder(requireContext()).setView(dialog).create()
            val singleText = dialog.findViewById<AppCompatTextView>(R.id.textViewName)
            singleText.text = needText
            alertDialog.setCancelable(true)
            alertDialog.show()
        }
    }

    private fun getMacAddress() {
        try {
            val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                needText += "wifi_mac:$res1 \n"
            }
        } catch (ex: Exception) {
            Log.e("macAddressDebug", "getMacAddress() throw a Exception")
        }
    }


    private fun getAndroidId(){
        val androidId = Settings.System.getString(activity?.contentResolver, Settings.Secure.ANDROID_ID)
        needText += "androidId:$androidId \n"
        if (!androidId.isNullOrEmpty() && androidId != "9774d56d682e549c"){
            needText += try {
                val uuid1 = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf-8"))).toString()
                "uuid_1:$uuid1 \n"
            } catch (e: java.lang.Exception){
                "uuid_1_exception \n"
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun getUUID(){
        val mDevID = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + (if (null != Build.CPU_ABI) Build.CPU_ABI.length else 0) % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 //13 位
        needText += "mDevID:$mDevID \n"

        //使用硬件信息拼凑出来的15位号码
        val uuidFake = UUID.nameUUIDFromBytes(mDevID.toByteArray(charset("utf8"))).toString()
        Log.e("debug_uuid_2", uuidFake)
        needText += "uuid_2:$uuidFake \n"
    }

    private fun refreshTime(){
        //先制空，在赋值
        timer = null
        timer = object : CountDownTimer(25*60*60L,998L) {//系统运算会自动加上运算时间
        override fun onTick(p0: Long) {
            computeTime()
            hourCountdownTV.text = getTV(hourCountDown)
            minuteCountdownTV.text = getTV(minuteCountDown)
            secondCountdownTV.text = getTV(secondCountDown)
            if (hourCountDown == 0L && minuteCountDown == 0L && secondCountDown == 0L){
                cancel()
                dayOver()
            }
            dLog("timeCountDown",p0.toString())
        }

            override fun onFinish() {
                //总时间结束，暂时用不到这个
            }
        }

        val nowTime = System.currentTimeMillis()/1000 //现在的时间
        val daySecond = 60*60*24 //一天的时间单位
        val dayTime = nowTime - (nowTime + 8 * 3600)%daySecond + daySecond //第二天凌晨的时间
        val endTime = dayTime - nowTime

        hourCountDown = endTime/(60*60)
        minuteCountDown = (endTime - (hourCountDown * 60 * 60)) / 60
        secondCountDown = endTime - (hourCountDown * 60 * 60) - (minuteCountDown * 60)
    }

    private fun getTV(l: Long): String{
        return if (l >= 10){
            "$l"
        } else {
            "0$l" //小于10则在前面补零
        }
    }

    private fun computeTime(){
        secondCountDown--
        if (secondCountDown < 0){
            minuteCountDown--
            secondCountDown = 59
            if (minuteCountDown < 0){
                hourCountDown--
                minuteCountDown = 59
                if (hourCountDown < 0){
                    //倒计时结束
                    hourCountDown = 0
                    minuteCountDown = 0
                    secondCountDown = 0
                }
            }
        }
    }

    private fun dayOver(){
        refreshTime()
        timer?.start()
    }

    override fun onResume() {
        super.onResume()
        if (resumeRefresh){
            timer?.cancel()
            refreshTime()
            timer?.start()
            resumeRefresh = false
        }
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        resumeRefresh = true
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

}