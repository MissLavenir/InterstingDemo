package com.example.interestingdemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.interestingdemo.extensions.hideSoftKeyBoard
import com.example.interestingdemo.function.OpenFun
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_make_qr_code.*
import pub.devrel.easypermissions.EasyPermissions


class MakeQrCode : Fragment(),EasyPermissions.PermissionCallbacks {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_qr_code, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textToQrCodeInputLayout.requestFocus()
        startQrCode.setOnClickListener {
            val text= textToQrCodeInput.text.toString()
            when {
                text.length >= 10 -> {
                    val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn,null,false)
                    val alert = AlertDialog.Builder(context).setView(dialog).create()
                    dialog.sureTitle.text = "二维码样式。"
                    dialog.sureMessage.text = "请选择以下生成二维码的两种格式之一"
                    dialog.sureBtn.text = "二维码中心加logo"
                    dialog.sureBtn.setOnClickListener {
                        val logoBitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_launcher)
                        val bitmap = OpenFun().createQRImage(text,640,640, logoBitmap = logoBitmap)
                        qrCode.setImageBitmap(bitmap)
                        alert.dismiss()
                    }
                    dialog.cancelBtn.text = "二维码不添加logo"
                    dialog.cancelBtn.setOnClickListener {
                        val bitmap = OpenFun().createQRImage(text,640,640)
                        qrCode.setImageBitmap(bitmap)
                        alert.dismiss()
                    }
                    //背景logo太难看了
//                    dialog.cancelBtn.text = "二维码背景为logo"
//                    dialog.cancelBtn.setTextColor(ResourcesCompat.getColor(resources,R.color.deep_orange_300,context?.theme))
//                    dialog.cancelBtn.setOnClickListener {
//                        val backgroundBitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_launcher)
//                        val bitmap = OpenFun().createQRImage(text,640,640, backgroundBitmap = backgroundBitmap)
//                        qrCode.setImageBitmap(bitmap)
//                        alert.dismiss()
//                    }
                    alert.show()
                }
                text.length in 1..9 -> {
                    val bitmap = OpenFun().createQRImage(text,640,640)
                    qrCode.setImageBitmap(bitmap)
                }
                else -> {
                    toast("输入内容不能为空！")
                }
            }
            activity?.hideSoftKeyBoard()
        }

        qrScan.setOnClickListener {
            val perms = arrayOf(Manifest.permission.CAMERA)
            if (EasyPermissions.hasPermissions(requireContext(),*perms)){
                view.findNavController().navigate(R.id.action_makeQrCode_to_QRScanFragment)
            }else{
                EasyPermissions.requestPermissions(this,"需要使用摄像头权限才能进行二维码扫描。", REQUEST_CAMERA_PERMISSION, *perms)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        toast("您未授权使用摄像头的权限，无法进行扫描二维码！")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        view?.findNavController()?.navigate(R.id.action_makeQrCode_to_QRScanFragment)
    }

    companion object{
        const val REQUEST_CAMERA_PERMISSION = 1566
    }
}