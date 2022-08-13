package cn.lqlklu.tools.fileshare.util

import java.net.NetworkInterface

fun getLocalIpAddress(): String? {
    val n = NetworkInterface.getNetworkInterfaces().iterator().asSequence()
    val a = n.flatMap {
        it.inetAddresses.asSequence().filter { inetAddress ->
            inetAddress.isSiteLocalAddress && inetAddress.hostAddress?.contains(":") != true &&
                    inetAddress.hostAddress != "127.0.0.1"
        }.map { inetAddress -> inetAddress.hostAddress }
    }
    return a.firstOrNull()
}
