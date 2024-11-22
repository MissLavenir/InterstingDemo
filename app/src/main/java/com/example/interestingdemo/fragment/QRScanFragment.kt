package com.example.interestingdemo.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.interestingdemo.R
import com.example.interestingdemo.extensions.toast
import com.king.zxing.CaptureHelper
import com.king.zxing.OnCaptureCallback
import com.king.zxing.camera.FrontLightMode
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_qr_scan.*
import pub.devrel.easypermissions.EasyPermissions


class QRScanFragment : Fragment(),EasyPermissions.PermissionCallbacks,OnCaptureCallback {

    /**
     * 扫码辅助对象
     * 必须在onViewCreated方法开始才能调用
     */
    private val captureHelper by lazy {
        CaptureHelper(this,surfaceView,viewfinderView,torch)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
    }

    private fun initCapture(){
        captureHelper.apply {
            supportVerticalCode(true)//支持扫条形码
            supportAutoZoom(false)//自动缩放
            continuousScan(true)//连续扫描
            autoRestartPreviewAndDecode(false)
            tooDarkLux(1000f)
            brightEnoughLux(999f)
            playBeep(true)//播放音效
            vibrate(true)//震动特效
            frontLightMode(FrontLightMode.OFF)
            setOnCaptureCallback(this@QRScanFragment)
            onCreate()
        }
        torch.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    private fun checkCameraPermission(){
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(requireContext(),*perms)){
            initCapture()
        }else{
            EasyPermissions.requestPermissions(this,"需要使用摄像头才能进行二维码扫描。", REQUEST_CAMERA_PERMISSION,*perms)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        toast("您未授权使用摄像头的权限，无法扫描！")
        activity?.finish()

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        captureHelper.onResume()
        captureHelper.restartPreviewAndDecode()
    }

    override fun onPause() {
        super.onPause()
        captureHelper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureHelper.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onResultCallback(result: String?): Boolean {
        if (result == null) return true
        if (result.trim().startsWith("https://") || result.trim().startsWith("www.")){
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "扫描内容"
            dialog.sureBtn.text = "进入"
            dialog.sureMessage.text = "内容如下网址：\n${result}\n任何内容与本程序都无关，本程序仅仅只是提供一个扫描途径。"
            alert.setCancelable(false)
            dialog.sureBtn.setOnClickListener {
                val uri: String = if (result.startsWith("www.")){
                    "https://$result"
                } else {
                    result
                }
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(uri))
                if (intent.resolveActivity(requireContext().packageManager) != null){
                    startActivity(intent)
                } else {
                    startActivity(Intent.createChooser(intent,"请选择打开方式"))
                }
                alert.dismiss()
            }
            dialog.cancelBtn.setOnClickListener {
                alert.dismiss()
                captureHelper.restartPreviewAndDecode()
            }
            alert.show()
        }else{
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "扫描内容"
            dialog.sureMessage.text = "内容如下：\n${result}\n任何内容与本程序都无关，本程序仅仅只是提供一个扫描途径。"
            dialog.cancelBtn.visibility = View.GONE
            alert.setCancelable(false)
            dialog.sureBtn.setOnClickListener {
                alert.dismiss()
                captureHelper.restartPreviewAndDecode()
            }
            alert.show()
        }
        return true
    }

    companion object{
        const val REQUEST_CAMERA_PERMISSION = 1566
    }
}