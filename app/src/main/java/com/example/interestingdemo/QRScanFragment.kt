package com.example.interestingdemo

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
import com.example.interestingdemo.extensions.toast
import com.king.zxing.CaptureHelper
import com.king.zxing.OnCaptureCallback
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
            supportAutoZoom(true)//自动缩放
            continuousScan(false)//连续扫描
            playBeep(true)//播放音效
            vibrate(true)//震动特效
            setOnCaptureCallback(this@QRScanFragment)
            onCreate()
        }
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
        checkCameraPermission()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        toast("您未授权使用摄像头的权限，无法扫描！")
        activity?.finish()
    }

    override fun onResume() {
        super.onResume()
        captureHelper.onResume()
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
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(result.trim())
            }
            startActivity(intent)
        }else{
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "扫描内容"
            dialog.sureMessage.text = "内容如下：\n${result}\n任何内容与本程序都无关，本程序仅仅只是提供一个扫描途径。"
            dialog.cancelBtn.visibility = View.GONE
            dialog.sureBtn.setOnClickListener {
                alert.dismiss()
            }
            alert.show()
        }
        activity?.finish()
        return true
    }

    companion object{
        const val REQUEST_CAMERA_PERMISSION = 1566
    }
}