package com.example.interestingdemo

import android.Manifest
import android.annotation.SuppressLint
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.interestingdemo.Util.DialogUtil
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.activity_get_location.*
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.util.*

class GetLocationActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {
    private lateinit var locationManager: LocationManager
    private var provider: String = ""
    private var isGetLocation = true
    private var hasPermission = false

    companion object{
        const val REQUEST_LOCATION_PERMISSION = 231
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)

        getLocation.setOnClickListener {
            if (hasPermission){
                getLocation()
            } else {
                checkPermission()
            }
        }
        stopLocation.setOnClickListener {
            removeUpdate()
            Log.e("debug","停止定位")
        }
    }

    private fun checkPermission() {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)){
            initLocal()
        } else {
            EasyPermissions.requestPermissions(this,"需要拥有定位权限才可以使用此功能", REQUEST_LOCATION_PERMISSION,*perms)
        }

    }

    private fun initLocal(){
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isAltitudeRequired = true
        criteria.isBearingRequired = true
        criteria.isCostAllowed = true
        criteria.isSpeedRequired = false
        provider = locationManager.getBestProvider(criteria, true) ?: ""
        if (provider != ""){
            getLocation()
        } else {
            toast("无法获取系统定位器")
        }
    }

    //到此处时已经请求过权限了
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val location =  locationManager.getLastKnownLocation(provider)
        if (location != null){
            getAddress(location)
        } else {
            if (provider != "") {
                locationManager.requestLocationUpdates(provider, 100L, 0f, listener)
            }
        }

    }

    private fun getAddress(location: Location){
        val coder = Geocoder(this, Locale.CHINESE)
        try {
            val addressList = coder.getFromLocation(location.latitude, location.longitude, 1)
            if (addressList.isNotEmpty()){
                val address = addressList[0].getAddressLine(0)
                DialogUtil.showCommonDialog(this,"定位获取的位置",address)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun removeUpdate(){
        locationManager.removeUpdates(listener)
    }

    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (isGetLocation){
                getAddress(location)
                removeUpdate()
                isGetLocation = false
            }

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }

        override fun onProviderEnabled(provider: String) {
            Log.e("debug_GPS","打开GPS")
            getLocation()
        }

        override fun onProviderDisabled(provider: String) {
            Log.e("debug_GPS","关闭GPS")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initLocal()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        DialogUtil.showCommonDialog(this,"提示","未拥有定位权限")
    }
}