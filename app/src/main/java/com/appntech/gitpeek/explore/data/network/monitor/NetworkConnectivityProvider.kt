package com.appntech.gitpeek.explore.data.network.monitor

import android.net.Network

interface NetworkConnectivityProvider {
    fun registerNetworkCallback(callback: (Boolean) -> Unit)
    fun unregisterNetworkCallback()
    fun getActiveNetwork(): Network?
    fun isConnected(): Boolean
}