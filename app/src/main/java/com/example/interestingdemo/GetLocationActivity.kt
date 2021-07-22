package com.example.interestingdemo

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.interestingdemo.Util.DialogUtil
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.activity_get_location.*
import java.lang.Exception
import java.util.*

class GetLocationActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private var provider: String = ""
    private var isGetLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)

        getLocation.setOnClickListener {
            initLocal()
        }
        stopLocation.setOnClickListener {
            removeUpdate()
            Log.e("debug","停止定位")
        }
    }

    private fun initLocal() {
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
            toast("未打开定位或无权限")
        }

    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            toast("没有定位权限！")
            return
        } else {
            val location =  locationManager.getLastKnownLocation(provider)
            if (location != null){
                getAddress(location)
            } else {
                if (provider != "") {
                    locationManager.requestLocationUpdates(provider, 100L, 0f, listener)
                }
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
}