package com.lygttpod.android.auto.tools.ktx

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

fun getPhoneWifiIpAddress(): String? {
    try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.getHostAddress()
                }
            }
        }
    } catch (e: SocketException) {
        e.printStackTrace()
        return null
    }

    return null
}