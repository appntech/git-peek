package com.appntech.gitpeek.explore.data.network.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkConnectivityProvider {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkStateCallback: ((Boolean) -> Unit)? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    // Register network callback and notify the listener (callback) when the network state changes
    override fun registerNetworkCallback(callback: (Boolean) -> Unit) {
        this.networkStateCallback = callback

        // Create a network callback to monitor the network state
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Notify that the network is available
                networkStateCallback?.invoke(true)
            }

            override fun onLost(network: Network) {
                // Notify that the network is lost
                networkStateCallback?.invoke(false)
            }
        }

        // Register the network callback with a network request
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        this.networkCallback = networkCallback
    }

    // Unregister the network callback
    override fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }

    override fun getActiveNetwork(): Network? {
        return connectivityManager.activeNetwork
    }

    override fun isConnected(): Boolean {
        val activeNetwork = getActiveNetwork() ?: return false  // Return false if no active network
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}

