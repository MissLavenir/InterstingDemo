package com.example.interestingdemo.util

import java.net.InetAddress
import java.net.NetworkInterface

object NetworkUtil {
    fun getLocalIpAddress(): InetAddress? {
//        try {
//            val ip = InetAddress.getLocalHost()
//            return ip.hostAddress
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return getLocalIpAddressV2()
    }

    private fun getLocalIpAddressV2(): InetAddress? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (!networkInterface.isUp || networkInterface.isLoopback) continue
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (inetAddress is InetAddress && !inetAddress.isLoopbackAddress && inetAddress.hostAddress?.contains(".") == true) {
                        return inetAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}